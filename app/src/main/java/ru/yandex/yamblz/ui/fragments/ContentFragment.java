package ru.yandex.yamblz.ui.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import butterknife.BindView;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.ui.fragments.brush.Brush;
import ru.yandex.yamblz.ui.fragments.brush.Pencil;
import ru.yandex.yamblz.ui.fragments.brush.Point;
import ru.yandex.yamblz.ui.fragments.dialogs.BrushFragment;
import ru.yandex.yamblz.ui.fragments.dialogs.ColorFragment;
import ru.yandex.yamblz.ui.fragments.dialogs.FileNameEnterFragment;
import ru.yandex.yamblz.ui.fragments.dialogs.OpenFragment;
import ru.yandex.yamblz.ui.fragments.drawing.DrawView;
import ru.yandex.yamblz.ui.fragments.drawing.HistoryCanvas;
import ru.yandex.yamblz.ui.fragments.drawing.TmpDrawer;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import solid.collectors.ToArray;
import solid.stream.Stream;

public class ContentFragment extends BaseFragment implements
        FileNameEnterFragment.OnFileNameEnteredListener,
        OpenFragment.OnFilePickedListener,
        BrushFragment.OnBrushChangeListener,
        ColorFragment.OnColorChangeListener,
        TmpDrawer {

    private static final String EXTENSION = ".bitmap";
    private static final String FOLDER = "Paint";
    private static final int HISTORY_SIZE = 10;

    @BindView(R.id.no_image_text)
    TextView noImageTextView;
    @BindView(R.id.user_image)
    DrawView drawView;

    private MenuItem saveItem;
    private MenuItem undoItem;
    private MenuItem exportItem;

    private Subscription loadSubscription;

    private HistoryCanvas canvas = new HistoryCanvas();
    private Brush brush;

    private View.OnTouchListener drawListener = (view, event) -> {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                canvas.beginNewObject();
                brush.start(new Point(event));
                break;

            case MotionEvent.ACTION_MOVE:
                brush.move(new Point(event));
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                brush.draw(canvas.getObjectCanvas());
                brush.finish();
                undoItem.setEnabled(canvas.canUndo());
                break;
        }

        drawView.invalidate();
        return true;
    };

    private View.OnTouchListener eyeDropperListener = (view, event) -> {
        int color = canvas.getDrawnBitmap().getPixel((int) event.getX(), (int) event.getY());
        brush.getPaint().setColor(color);

        drawView.setOnTouchListener(drawListener);
        DialogFragment dialogFragment = new ColorFragment();
        dialogFragment.show(getChildFragmentManager(), "color");
        return false;
    };

    private Action1<String> fileNameEnteredAction;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Paint paint = new Paint();
        paint.setStrokeWidth(60);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setAntiAlias(true);

        brush = new Pencil();
        brush.setPaint(paint);

        canvas.setTmpDrawer(this);

        // For caching
        Observable.fromCallable(() -> {
            Context context = getContext();
            context.getResources().getDrawable(R.mipmap.android_robot, null);
            context.getResources().getDrawable(R.mipmap.icon_heart, null);
            return null;
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        drawView.setOnTouchListener(drawListener);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        saveItem = menu.findItem(R.id.menu_save);
        undoItem = menu.findItem(R.id.menu_undo);
        exportItem = menu.findItem(R.id.menu_export);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_brush: {
                Bundle arguments = new Bundle();
                arguments.putInt(BrushFragment.MIN_VALUE, drawView.getWidth() / 100);
                arguments.putInt(BrushFragment.MAX_VALUE, drawView.getWidth() / 3);
                arguments.putInt(BrushFragment.DEFAULT_VALUE,
                        (int) brush.getPaint().getStrokeWidth());

                DialogFragment dialogFragment = new BrushFragment();
                dialogFragment.setArguments(arguments);
                dialogFragment.show(getChildFragmentManager(), "brush");
                break;
            }

            case R.id.menu_color: {
                DialogFragment dialogFragment = new ColorFragment();
                dialogFragment.show(getChildFragmentManager(), "color");
                break;
            }

            case R.id.menu_undo:
                canvas.undo();
                drawView.invalidate();
                undoItem.setEnabled(canvas.canUndo());
                break;

            case R.id.menu_new:
                if (loadSubscription != null) {
                    loadSubscription.unsubscribe();
                }
                loadSubscription = Observable
                        .fromCallable(() -> HistoryCanvas.createBitmapStack(HISTORY_SIZE + 1,
                                drawView.getWidth(), drawView.getHeight()))
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onBitmapStackLoaded);
                break;

            case R.id.menu_save: {
                fileNameEnteredAction = (fileName) -> {
                    final Bitmap bitmap = Bitmap.createBitmap(canvas.getDrawnBitmap());
                    Observable.fromCallable(() -> {
                        String longFileName = fileName + EXTENSION;
                        try (OutputStream out = getActivity()
                                .openFileOutput(longFileName, Context.MODE_PRIVATE)) {
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                        } catch (IOException e) {
                            Log.e(ContentFragment.class.getSimpleName(), "couldn't save file", e);
                            Toast.makeText(getContext(),
                                    getString(R.string.not_saved),
                                    Toast.LENGTH_LONG).show();
                        }
                        return null;
                    })
                            .subscribeOn(Schedulers.io())
                            .subscribe();
                };
                DialogFragment dialogFragment = new FileNameEnterFragment();
                dialogFragment.show(getChildFragmentManager(), "save");
                break;
            }

            case R.id.menu_open: {
                Observable.fromCallable(() ->
                        Stream.stream(getActivity().fileList())
                                .filter(file -> file.endsWith(EXTENSION))
                                .map(file -> file.substring(0, file.length() - EXTENSION.length()))
                                .collect(ToArray.toArray(String.class)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .forEach(files -> {
                            DialogFragment dialogFragment = new OpenFragment();

                            Bundle arguments = new Bundle();
                            arguments.putStringArray(OpenFragment.ARGUMENT_FILES, files);
                            dialogFragment.setArguments(arguments);

                            dialogFragment.show(getChildFragmentManager(), "open");
                        });
                break;
            }

            case R.id.menu_export: {
                final Bitmap bitmap = Bitmap.createBitmap(canvas.getDrawnBitmap());
                fileNameEnteredAction = (fileName) ->
                        Observable.fromCallable(() -> {
                            String sdcard = Environment.getExternalStorageDirectory().toString();
                            String location = String.format("%s/%s/%s.png",
                                    sdcard, FOLDER, fileName);
                            File file = new File(location);
                            //noinspection ResultOfMethodCallIgnored
                            file.getParentFile().mkdir();
                            try (FileOutputStream stream = new FileOutputStream(file)) {
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                stream.flush();
                            }
                            return location;
                        })
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(location -> {
                                    Toast.makeText(getContext(), location, Toast.LENGTH_LONG)
                                            .show();
                                }, e -> {
                                    Log.d(this.getClass().getSimpleName(), "image not saved", e);
                                    Toast.makeText(getContext(),
                                            getString(R.string.not_saved),
                                            Toast.LENGTH_LONG).show();
                                });
                DialogFragment dialogFragment = new FileNameEnterFragment();
                dialogFragment.show(getChildFragmentManager(), "export");
                break;
            }

            default:
                Log.w(this.getClass().getSimpleName(), "menu click not handled");
                break;
        }
        return true;
    }

    private void onBitmapStackLoaded(Bitmap bitmapStack[]) {
        loadSubscription = null;

        canvas.setBitmapStack(bitmapStack);

        drawView.setCanvasDrawable(canvas);
        drawView.invalidate();

        noImageTextView.setVisibility(View.INVISIBLE);
        drawView.setVisibility(View.VISIBLE);
        saveItem.setEnabled(true);
        exportItem.setEnabled(true);
        undoItem.setEnabled(canvas.canUndo());
    }

    @Override
    public void onStop() {
        if (loadSubscription != null) {
            loadSubscription.unsubscribe();
            loadSubscription = null;
        }
        super.onStop();
    }

    @Override
    public void onFileNameEntered(String file) {
        fileNameEnteredAction.call(file);
        fileNameEnteredAction = null;
    }

    @Override
    public void onFilePicked(String file) {
        if (loadSubscription != null) {
            loadSubscription.unsubscribe();
            loadSubscription = null;
        }
        loadSubscription = Observable.fromCallable(() -> {
            Bitmap history[] = HistoryCanvas.createBitmapStack(HISTORY_SIZE,
                    drawView.getWidth(), drawView.getHeight());
            Bitmap bitmapStack[] = new Bitmap[history.length + 1];
            System.arraycopy(history, 0, bitmapStack, 1, history.length);

            try (FileInputStream fileInputStream = getActivity().openFileInput(file + EXTENSION)) {
                bitmapStack[0] = BitmapFactory.decodeStream(fileInputStream)
                        .copy(Bitmap.Config.ARGB_8888, true);
                return bitmapStack;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onBitmapStackLoaded);
    }

    @Override
    public Brush getBrush() {
        return brush;
    }

    @Override
    public void onSizeChanged(int newSize) {
        brush.getPaint().setStrokeWidth(newSize);
    }

    @Override
    public void onBrushChanged(Brush newBrush) {
        newBrush.setPaint(brush.getPaint());
        brush = newBrush;
    }

    @Override
    public void onColorChanged(int newColor) {
        brush.getPaint().setColor(newColor);
    }

    @Override
    public void onEyeDropperRequested() {
        drawView.setOnTouchListener(eyeDropperListener);
    }

    @Override
    public void drawTmp(Canvas canvas) {
        brush.draw(canvas);
    }
}

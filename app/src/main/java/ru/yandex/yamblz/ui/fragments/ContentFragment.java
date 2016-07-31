package ru.yandex.yamblz.ui.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import butterknife.BindView;
import butterknife.OnTouch;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.ui.activities.MainActivity;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import solid.collectors.ToArray;
import solid.stream.Stream;

public class ContentFragment extends BaseFragment implements
        SaveFragment.OnFileEnteredListener,
        OpenFragment.OnFilePickedListener {

    private static final String EXTENSION = ".bitmap";
    @BindView(R.id.no_image_text)
    TextView noImageTextView;
    @BindView(R.id.user_image)
    ImageView image;
    private Bitmap bitmap;
    private Canvas canvas;
    private Subscription loadSubscription;
    private MenuItem saveItem;
    private float lastX, lastY;
    private Paint paint = new Paint(); // TODO lol

    {
        paint.setStrokeWidth(60);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ((MainActivity) getActivity()).setOnFileEnteredListener(this);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        saveItem = menu.findItem(R.id.menu_save);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_new:
                loadSubscription = Observable
                        .fromCallable(() -> Bitmap.createBitmap(
                                image.getWidth(), image.getHeight(),
                                Bitmap.Config.ARGB_8888))
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onBitmapLoaded);
                break;

            case R.id.menu_save: {
                ((MainActivity) getActivity()).setOnFileEnteredListener(this);
                DialogFragment dialogFragment = new SaveFragment();
                dialogFragment.show(getFragmentManager(), "save");
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
                            ((MainActivity) getActivity()).setOnFilePickedListener(this);
                            DialogFragment dialogFragment = new OpenFragment();

                            Bundle arguments = new Bundle();
                            arguments.putStringArray(OpenFragment.ARGUMENT_FILES, files);
                            dialogFragment.setArguments(arguments);

                            dialogFragment.show(getFragmentManager(), "open");
                        });
                break;
            }

            default:
                Log.w(this.getClass().getSimpleName(), "menu click not handled");
                break;
        }
        return true;
    }

    private void onBitmapLoaded(Bitmap bitmap) {
        loadSubscription = null;
        this.bitmap = bitmap;
        canvas = new Canvas(bitmap);
        image.setImageBitmap(bitmap);

        noImageTextView.setVisibility(View.INVISIBLE);
        image.setVisibility(View.VISIBLE);
        saveItem.setEnabled(true);
    }

    @Override
    public void onStop() {
        if (loadSubscription != null) {
            loadSubscription.unsubscribe();
            loadSubscription = null;
        }
        ((MainActivity) getActivity()).setOnFileEnteredListener(null);
        ((MainActivity) getActivity()).setOnFilePickedListener(null);
        super.onStop();
    }

    @OnTouch(R.id.user_image)
    boolean onTouch(@SuppressWarnings("UnusedParameters") View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                canvas.drawLine(lastX, lastY, event.getX(), event.getY(), paint);
                break;

            case MotionEvent.ACTION_DOWN:
                canvas.drawCircle(event.getX(), event.getY(), 30, new Paint());
                break;
        }
        lastX = event.getX();
        lastY = event.getY();

        image.invalidate();
        return true;
    }

    @Override
    public void onFileEntered(String file) {
        Observable.just(file)
                .subscribeOn(Schedulers.io())
                .forEach(fileName -> {
                    fileName = fileName + ".bitmap";
                    try (OutputStream out =
                                 getActivity().openFileOutput(fileName, Context.MODE_PRIVATE)) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    } catch (IOException e) {
                        Log.e(ContentFragment.class.getSimpleName(), "couldn't save file", e);
                    }
                });
    }

    @Override
    public void onFilePicked(String file) {
        if (loadSubscription != null) {
            loadSubscription.unsubscribe();
            loadSubscription = null;
        }
        loadSubscription = Observable.fromCallable(() -> {
            try (FileInputStream fileInputStream = getActivity().openFileInput(file + EXTENSION)) {
                return BitmapFactory.decodeStream(fileInputStream)
                        .copy(Bitmap.Config.ARGB_8888, true);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onBitmapLoaded);
    }
}

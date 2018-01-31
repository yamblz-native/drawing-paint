package ru.shmakova.painter.draw;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.io.IOException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import ru.shmakova.painter.R;
import ru.shmakova.painter.app.App;
import ru.shmakova.painter.draw.brush.BrushPickerDialogFragment;
import ru.shmakova.painter.draw.text.TextDialogFragment;
import ru.shmakova.painter.screen.BaseFragment;
import ru.shmakova.painter.utils.ImageUtils;
import rx.Observable;
import rx.subjects.PublishSubject;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

@SuppressWarnings("PMD.GodClass") // more than 47 methods
public class DrawFragment extends BaseFragment implements
        TextDialogFragment.EditTextDialogListener,
        BrushPickerDialogFragment.BrushPickerDialogListener,
        DrawView {
    private static final int GALLERY_PICTURE_REQUEST_CODE = 10;
    private static final int SAVE_WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 20;
    private static final int SHARE_WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 30;
    private static final int TEXT_PICKER_REQUEST_CODE = 40;
    private static final int BRUSH_PICKER_REQUEST_CODE = 60;

    private DataFragment dataFragment;

    @NonNull
    private final PublishSubject<Integer> colorPicks = PublishSubject.create();

    @BindView(R.id.canvas)
    CanvasView canvasView;
    @BindView(R.id.color)
    ImageView colorIcon;

    @Inject
    DrawPresenter presenter;

    @ColorInt
    private int currentColor;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.get(getContext()).applicationComponent().inject(this);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentManager fm = getFragmentManager();
        dataFragment = (DataFragment) fm.findFragmentByTag(DataFragment.TAG);

        if (dataFragment == null) {
            dataFragment = new DataFragment();
            fm.beginTransaction()
                    .add(dataFragment, DataFragment.TAG)
                    .commit();
        } else {
            canvasView.setBitmap(dataFragment.getData());
        }
        setHasOptionsMenu(true);
        presenter.bindView(this);
    }

    @Override
    public void onDestroyView() {
        presenter.unbindView(this);
        dataFragment.setData(canvasView.getBitmap());
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.action_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_btn:
                saveToFile();
                break;
            case R.id.upload_btn:
                loadImageFromGallery();
                break;
            case R.id.clear_btn:
                canvasView.clear();
                break;
            case R.id.share_btn:
                share();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.color_pick_btn)
    public void onColorPickButtonClick() {
        ColorPickerDialogBuilder
                .with(getContext())
                .setTitle(R.string.color_pick)
                .initialColor(currentColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(10)
                .setPositiveButton(R.string.ok, (dialog, selectedColor, allColors) -> colorPicks.onNext(selectedColor))
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                })
                .build()
                .show();
    }

    @OnClick(R.id.text_btn)
    public void onTextButtonClick() {
        FragmentManager fm = getFragmentManager();
        TextDialogFragment textDialogFragment = new TextDialogFragment();
        textDialogFragment.setTargetFragment(this, TEXT_PICKER_REQUEST_CODE);
        textDialogFragment.show(fm, "fragment_text");
    }

    @OnClick(R.id.brush_btn)
    public void onBrushButtonClick() {
        canvasView.setBrush();
        FragmentManager fm = getFragmentManager();
        BrushPickerDialogFragment brushPickerDialogFragment = new BrushPickerDialogFragment();
        brushPickerDialogFragment.setTargetFragment(this, BRUSH_PICKER_REQUEST_CODE);
        brushPickerDialogFragment.show(fm, "fragment_brush_picker");
    }

    private void saveToFile() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            try {
                ImageUtils.saveImageToFile(getContext(), canvasView.getBitmap());
            } catch (IOException e) {
                Timber.e(e);
            }
        } else {
            requestPermissions(
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    SAVE_WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        }
    }

    public void loadImageFromGallery() {
        Intent takeGalleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        takeGalleryIntent.setType("image/");
        startActivityForResult(
                Intent.createChooser(
                        takeGalleryIntent,
                        getString(R.string.choose_photo)),
                GALLERY_PICTURE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICTURE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            canvasView.setBitmap(ImageUtils.loadBitmapFromUri(getContext(), imageUri));
        }
    }

    @Override
    public void onEditText(String text) {
        canvasView.setText(text);
    }


    @Override
    public void onBrushPick(float brushWidth) {
        canvasView.setStrokeWidth(brushWidth);
    }


    @Override
    public Observable<Integer> colorPicks() {
        return colorPicks;
    }

    @Override
    public void setStrokeWidth(float savedStrokeWidth) {
        canvasView.setStrokeWidth(savedStrokeWidth);
    }

    @Override
    public void setColor(@ColorInt int color) {
        currentColor = color;
        canvasView.setColor(color);
    }

    @Override
    public void updateMenuColor(@ColorInt int color) {
        DrawableCompat.setTint(colorIcon.getDrawable(), color);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == SAVE_WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
                saveToFile();
            } else if (requestCode == SHARE_WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
                share();
            }
        } else {
            Toast.makeText(getContext(), R.string.need_permissions, Toast.LENGTH_SHORT).show();
        }
    }

    private void share() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            String bitmapPath = MediaStore.Images.Media.insertImage(
                    getContext().getContentResolver(),
                    canvasView.getBitmap(),
                    String.valueOf(System.currentTimeMillis()),
                    null);
            Uri bitmapUri = Uri.parse(bitmapPath);
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
            shareIntent.setType("image/*");
            startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.share)));
        } else {
            requestPermissions(
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    SHARE_WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        }
    }
}

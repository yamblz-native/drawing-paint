package ru.shmakova.painter.draw;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.ActivityCompat;
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

import com.thebluealliance.spectrum.SpectrumDialog;
import com.yandex.metrica.YandexMetrica;

import java.io.IOException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import ru.shmakova.painter.R;
import ru.shmakova.painter.app.App;
import ru.shmakova.painter.draw.brush.BrushPickerDialogFragment;
import ru.shmakova.painter.draw.filter.FilterPickerDialogFragment;
import ru.shmakova.painter.draw.stamp.StampPickerDialogFragment;
import ru.shmakova.painter.draw.text.TextDialogFragment;
import ru.shmakova.painter.screen.BaseFragment;
import ru.shmakova.painter.utils.ImageUtils;
import rx.Observable;
import rx.subjects.PublishSubject;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

public class DrawFragment extends BaseFragment implements
        FilterPickerDialogFragment.FilterPickerDialogListener,
        TextDialogFragment.EditTextDialogListener,
        StampPickerDialogFragment.StampPickerDialogListener,
        BrushPickerDialogFragment.BrushPickerDialogListener,
        DrawView {
    private static final int GALLERY_PICTURE_REQUEST_CODE = 10;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 20;
    private static final int FILTER_PICKER_REQUEST_CODE = 30;
    private static final int TEXT_PICKER_REQUEST_CODE = 40;
    private static final int STAMP_PICKER_REQUEST_CODE = 50;
    private static final int BRUSH_PICKER_REQUEST_CODE = 60;

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
        injectDependencies();
    }

    private void injectDependencies() {
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
        setHasOptionsMenu(true);
        presenter.bindView(this);
    }

    @Override
    public void onDestroyView() {
        presenter.unbindView(this);
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
            case R.id.donate_btn:
                YandexMetrica.reportEvent("DONATE");
                donate();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.color_pick_btn)
    public void onColorPickButtonClick() {
        new SpectrumDialog.Builder(getContext())
                .setColors(R.array.colors)
                .setTitle(getResources().getString(R.string.color_pick))
                .setSelectedColor(currentColor)
                .setDismissOnColorSelected(false)
                .setOutlineWidth(2)
                .setOnColorSelectedListener((positiveResult, color) -> {
                    if (positiveResult) {
                        colorPicks.onNext(color);
                    }
                }).build().show(getFragmentManager(), "color_picker_dialog");
    }

    @OnClick(R.id.filter_btn)
    public void onFilterButtonClick() {
        FragmentManager fm = getFragmentManager();
        FilterPickerDialogFragment filterPickerDialogFragment = new FilterPickerDialogFragment();
        filterPickerDialogFragment.setTargetFragment(DrawFragment.this, FILTER_PICKER_REQUEST_CODE);
        filterPickerDialogFragment.show(fm, "fragment_filter_picker");
    }

    @OnClick(R.id.text_btn)
    public void onTextButtonClick() {
        FragmentManager fm = getFragmentManager();
        TextDialogFragment textDialogFragment = new TextDialogFragment();
        textDialogFragment.setTargetFragment(DrawFragment.this, TEXT_PICKER_REQUEST_CODE);
        textDialogFragment.show(fm, "fragment_text");
    }

    @OnClick(R.id.brush_btn)
    public void onBrushButtonClick() {
        canvasView.setBrush();
        FragmentManager fm = getFragmentManager();
        BrushPickerDialogFragment brushPickerDialogFragment = new BrushPickerDialogFragment();
        brushPickerDialogFragment.setTargetFragment(DrawFragment.this, BRUSH_PICKER_REQUEST_CODE);
        brushPickerDialogFragment.show(fm, "fragment_brush_picker");

    }

    @OnClick(R.id.stamp_btn)
    public void onStampButtonClick() {
        FragmentManager fm = getFragmentManager();
        StampPickerDialogFragment stampPickerDialogFragment = new StampPickerDialogFragment();
        stampPickerDialogFragment.setTargetFragment(DrawFragment.this, STAMP_PICKER_REQUEST_CODE);
        stampPickerDialogFragment.show(fm, "fragment_stamp_picker");
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
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        }
    }

    @Override
    public void onFilterPick(@IdRes int filter) {
        if (filter == R.id.gray_scale_btn) {
            canvasView.applyGrayScaleFilter();
            return;
        }

        if (filter == R.id.negative_btn) {
            canvasView.applyNegativeFilter();
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

        if (requestCode == GALLERY_PICTURE_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                Uri imageUri = data.getData();
                canvasView.setBitmap(ImageUtils.loadBitmapFromUri(getContext(), imageUri));
            }
        }
    }

    @Override
    public void onEditText(String text) {
        canvasView.setText(text);
    }

    @Override
    public void onStampPick(@IdRes int stamp) {
        switch (stamp) {
            case R.id.sticker_1:
                canvasView.setStamp(ImageUtils.getStampFromDrawable(getContext(), R.drawable.ic_android_black_24dp));
                break;
            case R.id.sticker_2:
                canvasView.setStamp(ImageUtils.getStampFromDrawable(getContext(), R.drawable.ic_favorite_black_24dp));
                break;
            case R.id.sticker_3:
                canvasView.setStamp(ImageUtils.getStampFromDrawable(getContext(), R.drawable.ic_mood_black_24dp));
                break;
            case R.id.sticker_4:
                canvasView.setStamp(ImageUtils.getStampFromDrawable(getContext(), R.drawable.ic_cat));
                break;
            case R.id.sticker_5:
                canvasView.setStamp(ImageUtils.getStampFromDrawable(getContext(), R.drawable.ic_panda));
                break;
            case R.id.sticker_6:
                canvasView.setStamp(ImageUtils.getStampFromDrawable(getContext(), R.drawable.ic_poop));
                break;
            case R.id.sticker_7:
                canvasView.setStamp(ImageUtils.getStampFromDrawable(getContext(), R.drawable.ic_cool));
                break;
            case R.id.sticker_8:
                canvasView.setStamp(ImageUtils.getStampFromDrawable(getContext(), R.drawable.ic_ghost));
                break;
            case R.id.sticker_9:
                canvasView.setStamp(ImageUtils.getStampFromDrawable(getContext(), R.drawable.ic_owl));
                break;
            case R.id.sticker_10:
                canvasView.setStamp(ImageUtils.getStampFromDrawable(getContext(), R.drawable.ic_paw));
                break;
            default:
                break;
        }
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

    public void donate() {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        builder.setShowTitle(true);
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(getContext(), Uri.parse(getString(R.string.donate_url)));
    }
}

package ru.shmakova.painter.ui.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import butterknife.BindView;
import butterknife.OnClick;
import ru.shmakova.painter.R;
import ru.shmakova.painter.ui.android_views.CanvasView;
import ru.shmakova.painter.ui.other.ImageUtils;

import static android.app.Activity.RESULT_OK;

public class ContentFragment extends BaseFragment implements
        FilterPickerDialogFragment.FilterPickerDialogListener,
        TextDialogFragment.EditTextDialogListener,
        StampPickerDialogFragment.StampPickerDialogListener {
    private static final int GALLERY_PICTURE_REQUEST_CODE = 10;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 20;
    private static final int FILTER_PICKER_REQUEST_CODE = 30;
    private static final int TEXT_PICKER_REQUEST_CODE = 40;
    private static final int STAMP_PICKER_REQUEST_CODE = 50;
    private ImageUtils imageUtils;

    @BindView(R.id.canvas)
    CanvasView canvasView;
    @BindView(R.id.color)
    ImageView colorIcon;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        imageUtils = new ImageUtils(getContext());
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
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.color_pick_btn)
    public void onColorPickButtonClick() {
        new SpectrumDialog.Builder(getContext())
                .setColors(R.array.colors)
                .setTitle(getResources().getString(R.string.color_pick))
                .setSelectedColorRes(R.color.md_black)
                .setDismissOnColorSelected(false)
                .setOutlineWidth(2)
                .setOnColorSelectedListener((positiveResult, color) -> {
                    if (positiveResult) {
                        canvasView.setColor(color);
                        changeIconColor(color);
                    }
                }).build().show(getFragmentManager(), "color_picker_dialog");
    }

    @OnClick(R.id.filter_btn)
    public void onFilterButtonClick() {
        FragmentManager fm = getFragmentManager();
        FilterPickerDialogFragment filterPickerDialogFragment = FilterPickerDialogFragment.newInstance();
        filterPickerDialogFragment.setTargetFragment(ContentFragment.this, FILTER_PICKER_REQUEST_CODE);
        filterPickerDialogFragment.show(fm, "fragment_filter_picker");
    }

    @OnClick(R.id.text_btn)
    public void onTextButtonClick() {
        FragmentManager fm = getFragmentManager();
        TextDialogFragment textDialogFragment = TextDialogFragment.newInstance();
        textDialogFragment.setTargetFragment(ContentFragment.this, TEXT_PICKER_REQUEST_CODE);
        textDialogFragment.show(fm, "fragment_text");
    }

    @OnClick(R.id.brush_btn)
    public void onBrushButtonClick() {
        canvasView.setBrush();
    }

    @OnClick(R.id.stamp_btn)
    public void onStampButtonClick() {
        FragmentManager fm = getFragmentManager();
        StampPickerDialogFragment stampPickerDialogFragment = StampPickerDialogFragment.newInstance();
        stampPickerDialogFragment.setTargetFragment(ContentFragment.this, STAMP_PICKER_REQUEST_CODE);
        stampPickerDialogFragment.show(fm, "fragment_stamp_picker");
    }


    /**
     * Checks permissions and saves bitmap to gallery
     */
    private void saveToFile() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            imageUtils.saveImageToFile(canvasView.getBitmap());
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        }
    }

    /**
     * Changes color of icon
     *
     * @param color
     */
    private void changeIconColor(int color) {
        DrawableCompat.setTint(colorIcon.getDrawable(), color);
    }

    @Override
    public void onFilterPick(int filter) {
        switch (filter) {
            case R.id.gray_scale_btn:
                canvasView.applyGrayScaleFilter();
                break;
            case R.id.negative_btn:
                canvasView.applyNegativeFilter();
                break;
        }
    }

    /**
     * Loads image from gallery
     */
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
                canvasView.setBitmap(imageUtils.loadBitmapFromUri(imageUri));
            }
        }
    }

    @Override
    public void onEditText(String text) {
        canvasView.setText(text);
    }

    @Override
    public void onStampPick(int stamp) {
        switch (stamp) {
            case R.id.sticker_1:
                canvasView.setStamp(imageUtils.getStampFromDrawable(R.drawable.sticker_1));
                break;
            case R.id.sticker_2:
                canvasView.setStamp(imageUtils.getStampFromDrawable(R.drawable.sticker_2));
                break;
            case R.id.sticker_3:
                canvasView.setStamp(imageUtils.getStampFromDrawable(R.drawable.sticker_3));
                break;
        }
    }
}

package ru.yandex.yamblz.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.Unbinder;
import ru.yandex.yamblz.FileManager;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.ui.views.DrawingView;

public class ContentFragment extends BaseFragment {
    public static final String DEBUG_TAG = ContentFragment.class.getName();
    public static final int DOWNLOAD_RESULT_CODE = 1234;
    public static final int SAVE_RESULT_CODE = 4321;

    @BindView(R.id.brush_button)
    ImageView brushButton;

    @BindView(R.id.save_button)
    ImageView saveButton;

    @BindView(R.id.download_button)
    ImageView downloadButton;

    @BindView(R.id.my_drawing_view)
    DrawingView drawingView;

    private Unbinder unbinder;

    private final FileManager fm = new FileManager();

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_content, container, false);
        unbinder = ButterKnife.bind(this, v);

        return v;
    }

    @OnLongClick(R.id.brush_button)
    public boolean onLongClick(View v) {
        Log.d(DEBUG_TAG, "In long click");

        ColorPickerDialogBuilder
                .with(v.getContext())
                .setTitle("Choose color")
                .initialColor(Color.GREEN)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(5)
                .setOnColorSelectedListener(selectedColor -> Log.d(DEBUG_TAG, "onColorSelected: 0x" + Integer.toHexString(selectedColor)))
                .setPositiveButton("ok", (dialog, selectedColor, allColors) -> {
                    drawingView.setNewColorForBrush(selectedColor);
                })
                .setNegativeButton("cancel", (dialog, which) -> {
                })
                .build()
                .show();

        return true;
    }

    @OnClick(R.id.save_button)
    public void onSaveClick() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult(intent, SAVE_RESULT_CODE);

    }

    @OnClick(R.id.download_button)
    public void onDownloadClick() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, DOWNLOAD_RESULT_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case DOWNLOAD_RESULT_CODE:
                    if (data != null) {
                        Bitmap result = fm.loadBitmapFromExternalStorage(getContext(), data.getData());
                        if (result != null) {
                            drawingView.setCanvasBitmap(result.copy(Bitmap.Config.ARGB_8888, true));
                            drawingView.invalidate();
                        }
                    }
                    break;
                case SAVE_RESULT_CODE:
                    if (data != null) {
                        fm.saveBitmapToExternalStorage(getActivity(),
                                drawingView.getCanvasBitmap(),
                                data.getData());
                    }
                    break;
                default:
                    // do nothing
                    break;
            }
        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

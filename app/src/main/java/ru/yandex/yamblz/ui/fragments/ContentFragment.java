package ru.yandex.yamblz.ui.fragments;

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
import butterknife.OnLongClick;
import butterknife.Unbinder;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.ui.views.DrawingView;

public class ContentFragment extends BaseFragment {
    public static final String DEBUG_TAG = ContentFragment.class.getName();

    @BindView(R.id.brush_button)
    ImageView brushButton;

    @BindView(R.id.save_button)
    ImageView saveButton;

    @BindView(R.id.download_button)
    ImageView downloadButton;

    @BindView(R.id.my_drawing_view)
    DrawingView drawingView;

    private Unbinder unbinder;

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
                .density(12)
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

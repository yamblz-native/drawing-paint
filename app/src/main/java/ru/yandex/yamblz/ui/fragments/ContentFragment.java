package ru.yandex.yamblz.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.shchurov.horizontalwheelview.HorizontalWheelView;
import com.rarepebble.colorpicker.ColorPickerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.ui.views.CanvasView;
import ru.yandex.yamblz.ui.views.PaintView;

public class ContentFragment extends BaseFragment {
    private ColorPickerView mColorPickerView;
    private AlertDialog mColorChooserDialog;

    @BindView(R.id.fragment_content_wheel_view)
    HorizontalWheelView mWheelView;

    @BindView(R.id.fragment_content_canvas_view)
    CanvasView mCanvasView;

    @BindView(R.id.fragment_content_paint_view)
    PaintView mPaintView;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mWheelView.setOnlyPositiveValues(true);
        mWheelView.setListener(new HorizontalWheelView.Listener() {
            @Override
            public void onRotationChanged(double radians) {
                setPaintSize((float) radians * 10 + 20);
            }
        });

        init();

        mColorChooserDialog = new AlertDialog.Builder(getContext())
                .setView(mColorPickerView)
                .setTitle(getString(R.string.color_chooser_title))
                .setPositiveButton(getString(R.string.color_chooser_positive_button), (dialog, which) -> setPaintColor(mColorPickerView.getColor()))
                .setNegativeButton(getString(R.string.color_chooser_negative_button), (dialog, which) -> {
                })
                .create();

        mPaintView.setOnClickListener(v -> {
            mColorPickerView.setColor(getPaintColor());
            mColorChooserDialog.show();
        });

    }

    private void init() {
        mColorPickerView = new ColorPickerView(getContext());
        mColorPickerView.showHex(false);

        setPaintColor(Color.CYAN);
        setPaintSize(20f);
    }

    private void setPaintSize(float size) {
        mCanvasView.setPaintSize(size);
        mPaintView.setSize(size);
    }

    private void setPaintColor(int color) {
        mPaintView.setColor(color);
        mCanvasView.setPaintColor(color);
    }

    private int getPaintColor() {
        return mPaintView.getColor();
    }
}

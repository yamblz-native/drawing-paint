package ru.yandex.yamblz.ui.fragments.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.ui.fragments.brush.Brush;
import ru.yandex.yamblz.ui.fragments.brush.CatBrush;
import ru.yandex.yamblz.ui.fragments.brush.DashLine;
import ru.yandex.yamblz.ui.fragments.brush.DrawableBrush;
import ru.yandex.yamblz.ui.fragments.brush.Line;
import ru.yandex.yamblz.ui.fragments.brush.Pencil;

public class PaintFragment extends DialogFragment {

    public static final String MIN_VALUE = "min value";
    public static final String MAX_VALUE = "max value";
    public static final String DEFAULT_VALUE = "default value";

    @BindView(R.id.paint_size_image_view)
    BrushImageView imageView;
    @BindView(R.id.paint_size_seek_bar)
    SeekBar seekBar;

    @BindViews({R.id.paint_pencil, R.id.paint_line, R.id.paint_dash,
            R.id.paint_cat, R.id.paint_android, R.id.paint_heart})
    Button brushButtons[];

    private Brush brushes[];

    private OnBrushChangeListener onPaintChangeListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams")
        View view = layoutInflater.inflate(R.layout.dialog_paint, null);
        ButterKnife.bind(this, view);

        BitmapDrawable androidBitmap = (BitmapDrawable) getContext().getResources()
                .getDrawable(R.mipmap.android_robot, null);
        BitmapDrawable heartBitmap = (BitmapDrawable) getContext().getResources()
                .getDrawable(R.mipmap.icon_heart, null);

        brushes = new Brush[]{new Pencil(), new Line(), new DashLine(),
                new CatBrush(),
                new DrawableBrush(androidBitmap, R.id.paint_android),
                new DrawableBrush(heartBitmap, R.id.paint_heart)};

        onPaintChangeListener = (OnBrushChangeListener) getParentFragment();

        Bundle arguments = getArguments();
        final int minValue = arguments.getInt(MIN_VALUE);
        final int maxValue = arguments.getInt(MAX_VALUE);
        final int defaultValue = arguments.getInt(DEFAULT_VALUE);

        imageView.setBrush(onPaintChangeListener.getBrush());

        seekBar.setMax(maxValue - minValue);
        seekBar.setProgress(defaultValue - minValue);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                onPaintChangeListener.onSizeChanged(progress + minValue);
                imageView.setBrush(onPaintChangeListener.getBrush());
                imageView.invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        for (int i = 0; i < brushButtons.length; ++i) {
            brushButtons[i].setEnabled(onPaintChangeListener.getBrush().getId() != brushes[i].getId());

            final int currentI = i;
            brushButtons[i].setOnClickListener(v -> {
                for (Button button : brushButtons) {
                    button.setEnabled(true);
                }
                brushButtons[currentI].setEnabled(false);
                onPaintChangeListener.onBrushChanged(brushes[currentI]);
                imageView.setBrush(onPaintChangeListener.getBrush());
                imageView.invalidate();
            });
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setPositiveButton(getString(R.string.button_ok), null);

        return builder.create();
    }

    public interface OnBrushChangeListener extends BrushProvider {
        void onSizeChanged(int newSize);

        void onBrushChanged(Brush newBrush);
    }
}

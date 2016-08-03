package ru.yandex.yamblz.ui.fragments.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
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

    @BindViews({R.id.paint_pencil, R.id.paint_line, R.id.paint_cat})
    Button brushButtons[];

    private Brush brushes[] = new Brush[]{new Pencil(), new Line(), new CatBrush()};

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams")
        View view = layoutInflater.inflate(R.layout.dialog_paint, null);
        ButterKnife.bind(this, view);

        OnBrushChangeListener onPaintChangeListener = (OnBrushChangeListener) getParentFragment();

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
            if (onPaintChangeListener.getBrush().getId() == brushes[i].getId()) {
                brushButtons[i].setEnabled(false);
            }

            final int closureI = i;

            brushButtons[closureI].setOnClickListener(v -> {
                for (Button button : brushButtons) {
                    button.setEnabled(true);
                }
                brushButtons[closureI].setEnabled(false);
                onPaintChangeListener.onBrushChanged(brushes[closureI]);
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

package ru.yandex.yamblz.ui.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.yandex.yamblz.R;

public class PaintFragment extends DialogFragment {

    public static final String MIN_VALUE = "min value";
    public static final String MAX_VALUE = "max value";
    public static final String DEFAULT_VALUE = "default value";

    @BindView(R.id.paint_size_image_view)
    PaintImageView imageView;
    @BindView(R.id.paint_size_seek_bar)
    SeekBar seekBar;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams")
        View view = layoutInflater.inflate(R.layout.dialog_paint, null);
        ButterKnife.bind(this, view);

        OnSizeChangeListener onSizeChangeListener = (OnSizeChangeListener) getParentFragment();

        Bundle arguments = getArguments();
        final int minValue = arguments.getInt(MIN_VALUE);
        final int maxValue = arguments.getInt(MAX_VALUE);
        final int defaultValue = arguments.getInt(DEFAULT_VALUE);

        imageView.setPaint(onSizeChangeListener.getPaint());

        seekBar.setMax(maxValue - minValue);
        seekBar.setProgress(defaultValue - minValue);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                imageView.setPaint(onSizeChangeListener.onSizeChanged(progress + minValue));
                imageView.invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setPositiveButton(getString(R.string.button_ok), null);

        return builder.create();
    }

    public interface OnSizeChangeListener extends PaintProvider {
        Paint onSizeChanged(int newSize);
    }
}

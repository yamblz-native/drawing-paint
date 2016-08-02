package ru.yandex.yamblz.ui.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import ru.yandex.yamblz.R;

public class ColorFragment extends DialogFragment {

    @BindView(R.id.paint_color_image_view)
    PaintImageView imageView;
    @BindView(R.id.paint_color_edit_text)
    EditText editText;
    @BindViews({R.id.paint_alpha_seek_bar,
            R.id.paint_red_seek_bar, R.id.paint_green_seek_bar, R.id.paint_blue_seek_bar})
    SeekBar seekBars[];
    private OnColorChangeListener onColorChangeListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams")
        View view = layoutInflater.inflate(R.layout.dialog_color, null);
        ButterKnife.bind(this, view);

        onColorChangeListener = (OnColorChangeListener) getParentFragment();

        imageView.setOnClickListener(v -> {
            Paint paint = onColorChangeListener
                    .onColorChanged(PaintImageView.invertColor(composeColor()));
            setPaint(paint);
            setSeekBars(paint.getColor());
        });
        for (SeekBar seekBar : seekBars) {
            seekBar.setMax(0xFF);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar ignored, int progress, boolean fromUser) {
                    setPaint(onColorChangeListener.onColorChanged(composeColor()));
                    imageView.invalidate();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }

        final Paint paint = onColorChangeListener.getPaint();
        setPaint(paint);
        setSeekBars(paint.getColor());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setPositiveButton(getString(R.string.button_ok), null);

        return builder.create();
    }

    @OnClick(R.id.eye_dropper_image_button)
    void onClick(@SuppressWarnings("UnusedParameters") View view) {
        onColorChangeListener.onEyeDropperRequested();
        dismiss();
    }

    @OnEditorAction(R.id.paint_color_edit_text)
    boolean onEditorAction(EditText editText, int actionId,
                           @SuppressWarnings("UnusedParameters") KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            try {
                int color = Integer.parseInt(editText.getText().toString(), 16) | 0xFF000000;
                setSeekBars(color);
                setPaint(onColorChangeListener.onColorChanged(composeColor()));
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), R.string.incorrect_color, Toast.LENGTH_SHORT)
                        .show();
                setPaint(onColorChangeListener.getPaint());
            }
        }
        return false;
    }

    private int getColorComponent(int color, int i) {
        int mask = 0xFF000000 >>> (i * 8);
        return (color & mask) >>> ((3 - i) * 8);
    }

    private void setPaint(Paint paint) {
        imageView.setPaint(onColorChangeListener.getPaint());

        int color = paint.getColor();
        setEditText(color & 0x00FFFFFF);
    }

    private void setEditText(int color) {
        editText.setText(String.format("%06X", color));
    }

    private void setSeekBars(int color) {
        for (int i = 0; i < 4; ++i) {
            int colorComponent = getColorComponent(color, i);
            seekBars[i].setProgress(colorComponent);
        }
    }

    private int composeColor() {
        int color = 0;
        for (int i = 0; i < 4; ++i) {
            color <<= 8;
            color |= seekBars[i].getProgress();
        }
        return color;
    }

    public interface OnColorChangeListener extends PaintProvider {
        Paint onColorChanged(int newColor);

        void onEyeDropperRequested();
    }
}

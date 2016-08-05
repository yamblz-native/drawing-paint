package ru.yandex.yamblz.ui.fragments.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
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
import ru.yandex.yamblz.ui.fragments.Utils;
import ru.yandex.yamblz.ui.fragments.brush.Brush;

public class ColorFragment extends DialogFragment {

    @BindView(R.id.brush_color_image_view)
    BrushView imageView;
    @BindView(R.id.brush_color_edit_text)
    EditText editText;
    @BindViews({R.id.brush_alpha_seek_bar,
            R.id.brush_red_seek_bar, R.id.brush_green_seek_bar, R.id.brush_blue_seek_bar})
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
            onColorChangeListener.onColorChanged(Utils.invertColor(composeColor()));
            setBrush();
            setSeekBars(onColorChangeListener.getBrush().getPaint().getColor());
        });
        for (SeekBar seekBar : seekBars) {
            seekBar.setMax(0xFF);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar ignored, int progress, boolean fromUser) {
                    onColorChangeListener.onColorChanged(composeColor());
                    setBrush();
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

        final Brush brush = onColorChangeListener.getBrush();
        setBrush();
        setSeekBars(brush.getPaint().getColor());

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

    @OnEditorAction(R.id.brush_color_edit_text)
    boolean onEditorAction(EditText editText, int actionId,
                           @SuppressWarnings("UnusedParameters") KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            try {
                int color = Integer.parseInt(editText.getText().toString(), 16);
                setSeekBars(color);
                onColorChangeListener.onColorChanged(composeColor());
                setBrush();
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), R.string.incorrect_color, Toast.LENGTH_SHORT)
                        .show();
                setBrush();
            }
        }
        return false;
    }

    private int getColorComponent(int color, int i) {
        int mask = 0xFF000000 >>> (i * 8);
        return (color & mask) >>> ((3 - i) * 8);
    }

    private void setBrush() {
        Brush brush = onColorChangeListener.getBrush();
        imageView.setBrush(brush);

        int color = brush.getPaint().getColor();
        setEditText(color);
    }

    private void setEditText(int color) {
        editText.setText(String.format("%08X", color));
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

    public interface OnColorChangeListener extends BrushProvider {
        void onColorChanged(int newColor);

        void onEyeDropperRequested();
    }
}

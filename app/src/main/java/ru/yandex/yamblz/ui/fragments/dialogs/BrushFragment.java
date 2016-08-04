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
import android.widget.EditText;
import android.widget.SeekBar;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.ui.fragments.brush.Brush;
import ru.yandex.yamblz.ui.fragments.brush.CatBrush;
import ru.yandex.yamblz.ui.fragments.brush.DashLine;
import ru.yandex.yamblz.ui.fragments.brush.DrawableBrush;
import ru.yandex.yamblz.ui.fragments.brush.Line;
import ru.yandex.yamblz.ui.fragments.brush.Pencil;
import ru.yandex.yamblz.ui.fragments.brush.TextBrush;

public class BrushFragment extends DialogFragment {

    public static final String MIN_VALUE = "min value";
    public static final String MAX_VALUE = "max value";
    public static final String DEFAULT_VALUE = "default value";

    @BindView(R.id.brush_image_view)
    BrushView imageView;
    @BindView(R.id.brush_size_seek_bar)
    SeekBar seekBar;

    @BindViews({R.id.brush_pencil, R.id.brush_line, R.id.brush_dash,
            R.id.brush_cat, R.id.brush_android, R.id.brush_heart, R.id.brush_text})
    Button brushButtons[];
    @BindView(R.id.brush_text_edit_text)
    EditText editText;

    private Brush brushes[];
    private TextBrush textBrush;

    private OnBrushChangeListener onBrushChangeListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams")
        View view = layoutInflater.inflate(R.layout.dialog_brush, null);
        ButterKnife.bind(this, view);

        BitmapDrawable androidBitmap = (BitmapDrawable) getContext().getResources()
                .getDrawable(R.mipmap.android_robot, null);
        BitmapDrawable heartBitmap = (BitmapDrawable) getContext().getResources()
                .getDrawable(R.mipmap.icon_heart, null);

        textBrush = new TextBrush();
        brushes = new Brush[]{new Pencil(), new Line(), new DashLine(),
                new CatBrush(),
                new DrawableBrush(androidBitmap, R.id.brush_android),
                new DrawableBrush(heartBitmap, R.id.brush_heart),
                textBrush};

        onBrushChangeListener = (OnBrushChangeListener) getParentFragment();

        Bundle arguments = getArguments();
        final int minValue = arguments.getInt(MIN_VALUE);
        final int maxValue = arguments.getInt(MAX_VALUE);
        final int defaultValue = arguments.getInt(DEFAULT_VALUE);

        imageView.setBrush(onBrushChangeListener.getBrush());

        seekBar.setMax(maxValue - minValue);
        seekBar.setProgress(defaultValue - minValue);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                onBrushChangeListener.onSizeChanged(progress + minValue);
                imageView.setBrush(onBrushChangeListener.getBrush());
                imageView.invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        if (onBrushChangeListener.getBrush().getId() == textBrush.getId()) {
            textBrush = (TextBrush) onBrushChangeListener.getBrush().copy();
            editText.setText(textBrush.getText());
        }
        for (int i = 0; i < brushButtons.length; ++i) {
            brushButtons[i].setEnabled(
                    onBrushChangeListener.getBrush().getId() != brushes[i].getId());

            final int currentI = i;
            brushButtons[i].setOnClickListener(v -> {
                for (Button button : brushButtons) {
                    button.setEnabled(true);
                }
                brushButtons[currentI].setEnabled(false);
                onBrushChangeListener.onBrushChanged(brushes[currentI]);
                imageView.setBrush(onBrushChangeListener.getBrush());
                imageView.invalidate();
            });
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setPositiveButton(getString(R.string.button_ok), null);

        return builder.create();
    }

    @OnTextChanged(R.id.brush_text_edit_text)
    void onTextChanged(CharSequence text) {
        textBrush.setText(text.toString());
        imageView.invalidate();
    }

    public interface OnBrushChangeListener extends BrushProvider {
        void onSizeChanged(int newSize);

        void onBrushChanged(Brush newBrush);
    }
}

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
import ru.yandex.yamblz.ui.fragments.brush.DrawableBrush;
import ru.yandex.yamblz.ui.fragments.brush.Line;
import ru.yandex.yamblz.ui.fragments.brush.Pencil;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PaintFragment extends DialogFragment {

    public static final String MIN_VALUE = "min value";
    public static final String MAX_VALUE = "max value";
    public static final String DEFAULT_VALUE = "default value";

    @BindView(R.id.paint_size_image_view)
    BrushImageView imageView;
    @BindView(R.id.paint_size_seek_bar)
    SeekBar seekBar;

    @BindViews({R.id.paint_pencil, R.id.paint_line, R.id.paint_cat,
            R.id.paint_android, R.id.paint_heart})
    Button brushButtons[];

    private Brush brushes[];

    private OnBrushChangeListener onPaintChangeListener;

    private Subscription androidSubscription;
    private Subscription heartSubscription;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams")
        View view = layoutInflater.inflate(R.layout.dialog_paint, null);
        ButterKnife.bind(this, view);

        brushes = new Brush[]{new Pencil(), new Line(), new CatBrush(), null, null};

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
            if (brushes[i] == null) {
                brushButtons[i].setEnabled(false);
            } else {
                initBrush(i);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setPositiveButton(getString(R.string.button_ok), null);

        return builder.create();
    }

    private void initBrush(int i) {
        brushButtons[i].setEnabled(onPaintChangeListener.getBrush().getId() != brushes[i].getId());

        brushButtons[i].setOnClickListener(v -> {
            for (Button button : brushButtons) {
                button.setEnabled(true);
            }
            brushButtons[i].setEnabled(false);
            onPaintChangeListener.onBrushChanged(brushes[i]);
            imageView.setBrush(onPaintChangeListener.getBrush());
            imageView.invalidate();
        });
    }

    @Override
    public void onResume() {
        androidSubscription = loadDrawable(R.mipmap.android_robot);
        heartSubscription = loadDrawable(R.mipmap.icon_heart);
        super.onResume();
    }

    private Subscription loadDrawable(int id) {
        return Observable
                .fromCallable(() -> (BitmapDrawable)
                        getContext().getResources().getDrawable(id, null))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((drawable) -> {
                    this.onDrawableLoaded(drawable, id);
                });
    }

    private void onDrawableLoaded(BitmapDrawable drawable, int id) {
        if (id == R.mipmap.android_robot) {
            brushes[3] = new DrawableBrush(drawable, id);
            initBrush(3);
            androidSubscription = null;
        } else if (id == R.mipmap.icon_heart) {
            brushes[4] = new DrawableBrush(drawable, id);
            initBrush(4);
            heartSubscription = null;
        }
    }

    @Override
    public void onPause() {
        if (androidSubscription != null) {
            androidSubscription.unsubscribe();
        }
        if (heartSubscription != null) {
            heartSubscription.unsubscribe();
        }
        super.onPause();
    }

    public interface OnBrushChangeListener extends BrushProvider {
        void onSizeChanged(int newSize);

        void onBrushChanged(Brush newBrush);
    }
}

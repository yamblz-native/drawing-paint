package ru.yandex.yamblz.ui.views;


import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.SeekBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.yandex.yamblz.R;

public class PaintSizeDialog extends Dialog implements View.OnClickListener {
    private SizeChangedListener sizeChangedListener;
    @BindView(R.id.seekBar) SeekBar seekBar;

    public PaintSizeDialog(Context context) {
        super(context);
        setContentView(R.layout.paint_size_dialog);
        ButterKnife.bind(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(sizeChangedListener!=null){
                    sizeChangedListener.sizeChanged(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBar.setScaleY(3);
        seekBar.setScaleX(3);
        setTitle("Select Size");
    }

    @Override
    public void onClick(View v) {
        int color = ((ColorDrawable) v.getBackground()).getColor();
        if (sizeChangedListener != null) sizeChangedListener.sizeChanged(color);
        dismiss();
    }

    public void setSizeChangedListener(SizeChangedListener sizeChangedListener) {
        this.sizeChangedListener = sizeChangedListener;
    }

    public void show(float size) {
        seekBar.setProgress((int) size);
        show();
    }

    public interface SizeChangedListener {
        void sizeChanged(int color);
    }
}

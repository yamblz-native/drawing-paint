package ru.yandex.yamblz.ui.views;


import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;

import ru.yandex.yamblz.R;

public class ColorDialog extends Dialog implements View.OnClickListener {
    private ColorChangedListener colorChangedListener;

    public ColorDialog(Context context) {
        super(context);
        setContentView(R.layout.color_dialog);
        setTitle("Select Color");
        findViewById(R.id.color1).setOnClickListener(this);
        findViewById(R.id.color2).setOnClickListener(this);
        findViewById(R.id.color3).setOnClickListener(this);
        findViewById(R.id.color4).setOnClickListener(this);
        findViewById(R.id.color5).setOnClickListener(this);
        findViewById(R.id.color6).setOnClickListener(this);
        findViewById(R.id.color7).setOnClickListener(this);
        findViewById(R.id.color8).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int color = ((ColorDrawable) v.getBackground()).getColor();
        if (colorChangedListener != null) colorChangedListener.colorChanged(color);
        dismiss();
    }

    public void setColorChangedListener(ColorChangedListener colorChangedListener) {
        this.colorChangedListener = colorChangedListener;
    }

    public interface ColorChangedListener {
        void colorChanged(int color);
    }
}

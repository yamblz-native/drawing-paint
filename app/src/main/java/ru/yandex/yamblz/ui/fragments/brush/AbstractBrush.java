package ru.yandex.yamblz.ui.fragments.brush;

import android.graphics.Paint;

@SuppressWarnings("WeakerAccess")
public abstract class AbstractBrush implements Brush {
    protected Paint paint;

    @Override
    public void setPaint(Paint paint) {
        this.paint = paint;
    }
}

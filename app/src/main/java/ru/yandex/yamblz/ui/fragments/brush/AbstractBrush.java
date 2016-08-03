package ru.yandex.yamblz.ui.fragments.brush;

import android.graphics.Canvas;
import android.graphics.Paint;

@SuppressWarnings("WeakerAccess")
public abstract class AbstractBrush implements Brush {
    protected Paint paint;
    private boolean isDrawn = false;

    @Override
    public Paint getPaint() {
        return paint;
    }

    @Override
    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    @Override
    public void start(Point point) {
        isDrawn = true;
    }

    @Override
    public void move(Point point) {
    }

    @Override
    public void finish() {
        isDrawn = false;
    }

    @Override
    public void draw(Canvas canvas) {
        if (isDrawn) {
            onDraw(canvas);
        }
    }

    protected void onDraw(Canvas canvas) {
    }

    protected Brush copy(Brush brush) {
        brush.setPaint(paint);
        return brush;
    }
}

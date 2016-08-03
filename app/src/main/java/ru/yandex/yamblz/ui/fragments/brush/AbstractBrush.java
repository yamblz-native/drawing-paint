package ru.yandex.yamblz.ui.fragments.brush;

import android.graphics.Canvas;
import android.graphics.Paint;

@SuppressWarnings("WeakerAccess")
public abstract class AbstractBrush implements Brush {
    protected Paint paint;
    protected float startX, startY;
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
        startX = point.getX();
        startY = point.getY();
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

    protected abstract void onDraw(Canvas canvas);

    protected Brush copy(Brush brush) {
        brush.setPaint(paint);
        return brush;
    }
}

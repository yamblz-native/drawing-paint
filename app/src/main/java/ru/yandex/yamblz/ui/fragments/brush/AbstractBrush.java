package ru.yandex.yamblz.ui.fragments.brush;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Helper class for implementing {@link Brush}.
 */
@SuppressWarnings("WeakerAccess")
public abstract class AbstractBrush implements Brush {
    /**
     * Paint used for drawing.
     */
    protected Paint paint;
    /**
     * Coordinates of the beginning of current movement.
     */
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
    public final void draw(Canvas canvas) {
        if (isDrawn) {
            onDraw(canvas);
        }
    }

    /**
     * Subclasses should implement this method to draw.
     * It is called only between {@link #start} and {@link #finish} calls.
     *
     * @param canvas canvas to draw on.
     */
    protected abstract void onDraw(Canvas canvas);

    /**
     * Sets current paint to the brush and returns it.
     * @param brush to set current paint to.
     * @return same brush with current paint.
     */
    protected Brush copy(Brush brush) {
        brush.setPaint(paint);
        return brush;
    }
}

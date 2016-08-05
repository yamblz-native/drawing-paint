package ru.yandex.yamblz.ui.fragments.brush;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Brush for drawing.
 */
@SuppressWarnings("WeakerAccess")
public interface Brush {
    /**
     * @return paint this brush uses for drawing.
     */
    Paint getPaint();

    /**
     * Set paint to draw with.
     *
     * @param paint paint used to draw with.
     */
    void setPaint(Paint paint);

    /**
     * Start drawing with the brush from specified point.
     * @param point starting point.
     */
    void start(Point point);

    /**
     * Move brush to the next point.
     * @param point point where this brush is moved to.
     */
    void move(Point point);

    /**
     * Reset state of the brush.
     */
    void finish();

    /**
     * Draw current movement to the canvas.
     * @param canvas canvas to draw on.
     */
    void draw(Canvas canvas);

    /**
     * Called before showing preview of this brush.
     *
     * @param width  width of preview canvas.
     * @param height height of preview canvas.
     */
    void prepareForPreview(int width, int height);

    /**
     * Create new instance of the same brush.
     * New brush should use the same {@link Paint}.
     * @return new brush.
     */
    Brush copy();

    /**
     * @return unique id for this type of brush.
     */
    int getId();
}

package ru.yandex.yamblz.ui.fragments.drawing;

import android.graphics.Canvas;

/**
 * Something that can be drawn on a {@link Canvas}.
 */

@SuppressWarnings("WeakerAccess")
public interface CanvasDrawable {
    /**
     * Draw content on a canvas.
     *
     * @param canvas canvas to draw on.
     */
    void draw(Canvas canvas);
}

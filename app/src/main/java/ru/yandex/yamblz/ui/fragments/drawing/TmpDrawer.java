package ru.yandex.yamblz.ui.fragments.drawing;

import android.graphics.Canvas;

/**
 * Implementing class can provide drawing of some temporary objects.
 */
public interface TmpDrawer {
    /**
     * Draw some temporary object.
     *
     * @param canvas canvas to draw on.
     */
    void drawTmp(Canvas canvas);
}

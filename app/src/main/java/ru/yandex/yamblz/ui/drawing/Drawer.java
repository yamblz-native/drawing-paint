package ru.yandex.yamblz.ui.drawing;

import android.graphics.Bitmap;

public interface Drawer {

    /**
     * Sets stroke size
     * @param size size in px
     */
    void setSize(float size);

    /**
     * Selects brush_not_active
     */
    void brush();

    /**
     * Selects pencil;
     */
    void pencil();

    /**
     * Selects eraser
     */
    void eraser();

    /**
     * Disable drawing
     */
    void disable();

    /**
     * Sets color
     * @param color the color
     */
    void setColor(int color);

    /**
     * Sets source bitmap
     * @param bitmap the bitmap
     */
    void setBitmap(Bitmap bitmap);

    /**
     * Clears canvas
     */
    void clear();

}

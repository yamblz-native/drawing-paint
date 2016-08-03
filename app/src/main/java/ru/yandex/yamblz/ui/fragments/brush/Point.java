package ru.yandex.yamblz.ui.fragments.brush;

import android.view.MotionEvent;

@SuppressWarnings("WeakerAccess")
public class Point {
    private float x, y;

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Point(MotionEvent event) {
        this(event.getX(), event.getY());
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}

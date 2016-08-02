package ru.yandex.yamblz.ui.fragments.brush;

import android.graphics.Canvas;
import android.view.MotionEvent;

import ru.yandex.yamblz.ui.other.Cat;

public class CatBrush extends AbstractBrush {
    private boolean isDrawn;
    private float x, y;

    @Override
    public void start(MotionEvent event) {
        isDrawn = true;
        x = event.getX();
        y = event.getY();
    }

    @Override
    public void move(MotionEvent event) {
    }

    @Override
    public void finish(MotionEvent event) {
        isDrawn = false;
    }

    @Override
    public void draw(Canvas canvas) {
        if (isDrawn) {
            Cat.drawCat(canvas, x, y, paint.getStrokeWidth());
        }
    }

    @Override
    public int getId() {
        return 3;
    }
}

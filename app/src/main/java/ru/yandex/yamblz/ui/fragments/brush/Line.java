package ru.yandex.yamblz.ui.fragments.brush;

import android.graphics.Canvas;
import android.graphics.Path;
import android.view.MotionEvent;

public class Line extends AbstractBrush {
    private float startX, startY;
    private Path path = new Path();

    @Override
    public void start(MotionEvent event) {
        startX = event.getX();
        startY = event.getY();
    }

    @Override
    public void move(MotionEvent event) {
        path.reset();
        path.moveTo(startX, startY);
        path.lineTo(event.getX(), event.getY());
    }

    @Override
    public void finish(MotionEvent event) {
        path.reset();
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawPath(path, paint);
    }

    @Override
    public int getId() {
        return 1;
    }
}

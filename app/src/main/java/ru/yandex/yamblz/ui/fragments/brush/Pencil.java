package ru.yandex.yamblz.ui.fragments.brush;

import android.graphics.Canvas;
import android.graphics.Path;
import android.view.MotionEvent;

public class Pencil extends AbstractBrush {
    private Path path = new Path();

    @Override
    public void start(MotionEvent event) {
        path.moveTo(event.getX(), event.getY());
    }

    @Override
    public void move(MotionEvent event) {
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
        return 2;
    }
}

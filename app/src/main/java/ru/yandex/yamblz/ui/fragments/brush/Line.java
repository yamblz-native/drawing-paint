package ru.yandex.yamblz.ui.fragments.brush;

import android.graphics.Canvas;
import android.graphics.Path;

public class Line extends AbstractBrush {
    private Path path = new Path();

    @Override
    public void start(Point point) {
        super.start(point);
    }

    @Override
    public void move(Point point) {
        path.reset();
        path.moveTo(startX, startY);
        path.lineTo(point.getX(), point.getY());
    }

    @Override
    public void finish() {
        super.finish();
        path.reset();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (path.isEmpty()) {
            canvas.drawPoint(startX, startY, paint);
        } else {
            canvas.drawPath(path, paint);
        }
    }

    @Override
    public Brush copy() {
        return copy(new Line());
    }

    @Override
    public int getId() {
        return -1;
    }
}

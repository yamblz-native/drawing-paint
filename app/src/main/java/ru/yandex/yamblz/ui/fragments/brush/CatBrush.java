package ru.yandex.yamblz.ui.fragments.brush;

import android.graphics.Canvas;

import ru.yandex.yamblz.ui.other.Utils;

public class CatBrush extends AbstractBrush {
    private float x, y;

    @Override
    public void start(Point point) {
        super.start(point);
        x = point.getX();
        y = point.getY();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Utils.drawCat(canvas, paint.getColor(), x, y, paint.getStrokeWidth());
    }

    @Override
    public Brush copy() {
        return copy(new CatBrush());
    }

    @Override
    public int getId() {
        return 3;
    }
}

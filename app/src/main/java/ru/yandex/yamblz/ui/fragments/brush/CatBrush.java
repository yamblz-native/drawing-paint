package ru.yandex.yamblz.ui.fragments.brush;

import android.graphics.Canvas;

import ru.yandex.yamblz.ui.fragments.Utils;

public class CatBrush extends AbstractBrush {
    @Override
    protected void onDraw(Canvas canvas) {
        Utils.drawCat(canvas, paint.getColor(), startX, startY, paint.getStrokeWidth());
    }

    @Override
    public Brush copy() {
        return copy(new CatBrush());
    }

    @Override
    public int getId() {
        return -3;
    }
}

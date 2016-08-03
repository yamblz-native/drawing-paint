package ru.yandex.yamblz.ui.fragments.brush;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.PathEffect;

public class DashLine extends Pencil {

    @Override
    public Brush copy() {
        return copy(new DashLine());
    }

    @Override
    public int getId() {
        return -4;
    }

    @SuppressLint("DrawAllocation") // Sorry :(
    @Override
    protected void onDraw(Canvas canvas) {
        PathEffect paintEffect = paint.getPathEffect();
        paint.setPathEffect(new DashPathEffect(
                new float[]{paint.getStrokeWidth(), paint.getStrokeWidth() * 2}, 0));
        super.onDraw(canvas);
        paint.setPathEffect(paintEffect);
    }
}

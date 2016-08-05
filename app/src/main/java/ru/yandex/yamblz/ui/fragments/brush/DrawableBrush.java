package ru.yandex.yamblz.ui.fragments.brush;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;

public class DrawableBrush extends StampBrush {
    private BitmapDrawable drawable;
    private int id;
    private int width, height, radius;

    public DrawableBrush(BitmapDrawable drawable, int id) {
        this.drawable = drawable;
        this.id = id;

        init(drawable);
    }

    private void init(BitmapDrawable drawable) {
        drawable.setAntiAlias(true);

        width = drawable.getBitmap().getWidth();
        height = drawable.getBitmap().getHeight();
        radius = Math.max(width, height);
    }

    @SuppressLint("DrawAllocation") // Sorry :(
    @Override
    protected void onDraw(Canvas canvas) {
        if (drawable == null) {
            return;
        }

        int strokeWidth = (int) paint.getStrokeWidth();
        drawable.setBounds(
                ((int) startX) - width * strokeWidth / radius / 2,
                ((int) startY) - height * strokeWidth / radius / 2,
                ((int) startX) + width * strokeWidth / radius / 2,
                ((int) startY) + height * strokeWidth / radius / 2);

        drawable.setColorFilter(
                new PorterDuffColorFilter(paint.getColor(), PorterDuff.Mode.SRC_IN));
        drawable.draw(canvas);
    }

    @Override
    public void setPaint(Paint paint) {
        super.setPaint(paint);
    }

    @Override
    public Brush copy() {
        return copy(new DrawableBrush(drawable, id));
    }

    @Override
    public int getId() {
        return id;
    }
}

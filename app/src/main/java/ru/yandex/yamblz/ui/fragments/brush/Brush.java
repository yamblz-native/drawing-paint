package ru.yandex.yamblz.ui.fragments.brush;

import android.graphics.Canvas;
import android.graphics.Paint;

@SuppressWarnings("WeakerAccess")
public interface Brush {
    Paint getPaint();

    void setPaint(Paint paint);

    void start(Point point);

    void move(Point point);

    void finish();

    void draw(Canvas canvas);

    Brush copy();

    int getId();
}

package ru.yandex.yamblz.ui.fragments.brush;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

@SuppressWarnings("WeakerAccess")
public interface Brush {
    void setPaint(Paint paint);

    void start(MotionEvent event);

    void move(MotionEvent event);

    void finish(MotionEvent event);

    void draw(Canvas canvas);

    int getId();
}

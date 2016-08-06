package ru.yandex.yamblz.tools;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

/**
 * Created by Aleksandra on 03/08/16.
 */
public interface Tool {

    void setColor(int color);

    boolean onTouchEvent(MotionEvent event, Canvas canvas);

    void onDraw(Canvas canvas, Bitmap canvasBitmap, Paint canvasPaint);
}

package ru.yandex.yamblz.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * Created by kostya on 31.07.16.
 */

public class PaintView extends View {
    private final Path path = new Path();
    private final Paint paint;
    private Canvas canvas;
    private Bitmap bitmap;
    private float curX;
    private float curY;

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(15);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
//                path.moveTo(event.getX(), event.getY());
                curX = event.getX();
                curY = event.getY();
                return true;
            case MotionEvent.ACTION_MOVE:
//                path.lineTo(event.getX(), event.getY());
                canvas.drawLine(curX, curY, event.getX(), event.getY(), paint);
                curX = event.getX();
                curY = event.getY();
                canvas.save();
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.drawBitmap(bitmap, 0, 0, null);
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
            this.canvas = new Canvas(bitmap);
        }
        canvas.drawBitmap(bitmap, 0, 0, null);
    }

    public void setColor(int color) {
        paint.setColor(color);
    }
    public Bitmap getBitmap() {
        return bitmap;
    }
    public void setBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            this.bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            canvas = new Canvas(this.bitmap);
            invalidate();
        }
    }
}

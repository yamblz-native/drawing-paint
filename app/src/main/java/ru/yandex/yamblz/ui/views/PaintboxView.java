package ru.yandex.yamblz.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Volha on 05.08.2016.
 */

public class PaintboxView extends View {

    Paint paint;
    float startX, startY;
    float x = 1, y = 1;
    Path path;
    Bitmap background;

    public PaintboxView(Context context) {
        super(context);
        init();
    }

    public PaintboxView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PaintboxView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);
        path = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if ( background != null )
            canvas.drawBitmap(background, 0, 0, paint);
        canvas.drawPath(path, paint);
        Log.d("tag", "sx=" + startX + " sy=" + startY);
    }

    public void loadBitmap(Bitmap bitmap) {
        background = bitmap;
        invalidate();
    }

    public Bitmap getBitmap(){

        Bitmap  bitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        this.draw(canvas);
        return bitmap;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x = event.getX();
        y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                path.lineTo(x, y);
                invalidate();
                break;
        }
        return true;
    }

    static class PathWrapper {
        public Path path;
        public Paint paint;
        public PathWrapper() {

        }
    }
}

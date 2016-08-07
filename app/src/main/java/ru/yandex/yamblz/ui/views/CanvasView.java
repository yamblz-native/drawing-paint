package ru.yandex.yamblz.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import ru.yandex.yamblz.ui.other.Brush;
import ru.yandex.yamblz.ui.other.PaintMode;

/**
 * Created by dsukmanova on 07.08.16.
 */

public class CanvasView extends View {
    Paint paint;
    Path path;
    PaintMode mode;
    Brush brush;
    Bitmap currentBitmap;
    Canvas canvas;
    Paint canvasPaint;

    public CanvasView(Context context) {
        super(context);
        init();
    }

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CanvasView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setStrokeJoin(Paint.Join.ROUND);
        path = new Path();
        mode = PaintMode.BRUSH_MODE;
        brush = new Brush();
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(canvas);
        c.drawBitmap(currentBitmap, 0, 0, canvasPaint);
        c.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                actionDown(x, y);
                return true;
            case MotionEvent.ACTION_MOVE:
                actionMove(x, y);
                break;
            case MotionEvent.ACTION_UP:
                actionUp(x, y);
                break;
            default:
                return false;
        }
        postInvalidate();
        return true;
    }

    private void actionDown(float x, float y) {
        switch (mode) {
            case BRUSH_MODE:
                brush.actionDown(path, x, y);
                break;
        }
    }

    private void actionUp(float x, float y) {
        switch (mode) {
            case BRUSH_MODE:
                canvas.drawPath(path,paint);
                path.reset();
                break;
        }
    }

    private void actionMove(float x, float y) {
        switch (mode) {
            case BRUSH_MODE:
                brush.actionMove(path, x, y);
                break;
        }
    }

    public void setColor(int color) {
        invalidate();
        paint.setColor(color);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        clear(w,h);
    }

    public void clear(int w, int h){
        currentBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        currentBitmap.eraseColor(Color.WHITE);
        canvas = new Canvas(currentBitmap);
        invalidate();
    }

    public void setMode(PaintMode mode) {
        this.mode = mode;
    }
}

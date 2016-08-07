package ru.yandex.yamblz.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import ru.yandex.yamblz.R;
import ru.yandex.yamblz.ui.other.Brush;
import ru.yandex.yamblz.ui.other.PaintMode;

/**
 * Created by dsukmanova on 07.08.16.
 */

public class CanvasView extends View {
    Paint mpaint;
    Path mpath;
    PaintMode mode;
    Brush brush;

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
        mpaint = new Paint();
        mpaint.setColor(Color.BLACK);
        mpaint.setStyle(Paint.Style.STROKE);
        mpaint.setStrokeWidth(3);
        mpaint.setStrokeJoin(Paint.Join.ROUND);
        mpath = new Path();
        mode = PaintMode.BRUSH_MODE;
        brush = new Brush();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        canvas.drawPath(mpath, mpaint);
        //draw please
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                actionDown(x,y);
                return true;
            case MotionEvent.ACTION_MOVE:
                actionMove(x,y);
                break;
            case MotionEvent.ACTION_UP:
                actionUp(x,y);
                break;
            default:
                return false;
        }
        postInvalidate();
        return true;
    }

    private void actionDown(float x, float y){
        switch (mode){
            case BRUSH_MODE:
                brush.actionDown(mpath,x,y);
                break;
        }
    }

    private void actionUp(float x, float y){
        switch (mode){
            case BRUSH_MODE:
                brush.actionUp(mpath,x,y);
                break;
        }
    }

    private void actionMove(float x, float y){
        switch (mode){
            case BRUSH_MODE:
                brush.actionMove(mpath,x,y);
                break;
        }
    }



}

package ru.yandex.yamblz.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Aleksandra on 02/08/16.
 */
public class DrawingView extends View {
    public static final String DEBUG_TAG = DrawingView.class.getName();

    public static final String BITMAP_KEY = "BITMAP";

    private Path drawPath;

    private Paint drawPaint, canvasPaint;

    private int paintColor = 0xFF660000;

    private Canvas drawCanvas;

    private Bitmap canvasBitmap;


    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setupDrawing();
    }


    public void setupDrawing() {
        drawPath = new Path();

        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        super.onSaveInstanceState();
        Log.d(DEBUG_TAG, "onSaveInstanceState");
        Bundle state = new Bundle();

        drawCanvas.drawBitmap(canvasBitmap, 0, 0, null);
        drawCanvas.save();
        state.putParcelable(BITMAP_KEY, canvasBitmap);

        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);

        Log.d(DEBUG_TAG, "onRestoreInstanceState");
        Bundle bundle = (Bundle) state;
        canvasBitmap = bundle.getParcelable(BITMAP_KEY);
    }
}

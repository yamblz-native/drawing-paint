package ru.yandex.yamblz.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by vorona on 25.07.16.
 */

public class MyView extends View {
    private Path drawPath;
    private Paint drawPaint, canvasPaint;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private boolean filter;
    private int stampId;
    private Shader shader;

    private static int [] RAINBOW = {Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE, Color.MAGENTA};

    public MyView(Context context) {
        super(context);
        setup();
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    private void setup() {
        filter = false;
        stampId = 0;
        drawPath = new Path();
        drawPaint = new Paint();
        int paintColor = Color.BLACK;
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(30);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    private int cnt = 0;
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
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

    public void setColor(int newColor) {
        invalidate();
        drawPaint.setColor(newColor);
    }

    public void startNew() {
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    public void setImage(Bitmap bitmap) {
        drawCanvas.drawBitmap(bitmap, 0, 0, canvasPaint);
        invalidate();
    }

    public void setFilter(int id) {
        if (id > 0) {
            filter = true;
            shader = new LinearGradient(0, 0, canvasBitmap.getWidth(), canvasBitmap.getHeight(),
                    RAINBOW, null, Shader.TileMode.MIRROR);
            canvasPaint.setShader(shader);
        } else {
            filter = false;
            shader = new Shader();
        }
        drawPaint.setShader(shader);
//        requestLayout();
        //        stampId = id;
    }
}

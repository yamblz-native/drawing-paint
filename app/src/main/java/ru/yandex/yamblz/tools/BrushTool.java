package ru.yandex.yamblz.tools;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;

/**
 * Created by Aleksandra on 03/08/16.
 */
public class BrushTool implements Tool {
    private Path path;
    private Paint brush;
    private int color;


    public BrushTool() {
        path = new Path();

        brush = new Paint();
        brush.setColor(color);
        brush.setAntiAlias(true);
        brush.setStrokeWidth(20);
        brush.setStyle(Paint.Style.STROKE);
        brush.setStrokeJoin(Paint.Join.ROUND);
        brush.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    public void setColor(int color) {
        this.color = color;
        brush.setColor(color);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event, Canvas canvas) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                canvas.drawPath(path, brush);
                path.reset();
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public void onDraw(Canvas canvas, Bitmap canvasBitmap, Paint canvasPaint) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(path, brush);
    }
}

package ru.shmakova.painter.draw;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CanvasView extends View {
    private static final int DEFAULT_COLOR = Color.BLACK;
    private static final int BRUSH_TOOL = 0;
    private static final int TEXT_TOOL = 2;
    private static final float STROKE_WIDTH = 5f;
    private Paint drawPaint;
    private Paint fontPaint;
    private Paint canvasPaint;
    private Path path;
    private Canvas canvas;
    private Bitmap bitmap;
    private int currentTool;
    private String text;
    private float lastTouchX;
    private float lastTouchY;
    private final RectF dirtyRect = new RectF();
    private float halfStrokeWidth = STROKE_WIDTH / 2;

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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CanvasView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setFocusable(true);
        setFocusableInTouchMode(true);
        currentTool = BRUSH_TOOL;
        drawPaint = new Paint();
        drawPaint.setColor(DEFAULT_COLOR);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(STROKE_WIDTH);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
        path = new Path();
        fontPaint = new Paint();
        fontPaint.setColor(DEFAULT_COLOR);
        fontPaint.setStyle(Paint.Style.FILL);
        fontPaint.setTextSize(100);
    }

    public void setColor(int color) {
        invalidate();
        drawPaint.setColor(color);
        fontPaint.setColor(color);
    }

    public void setStrokeWidth(float strokeWidth) {
        invalidate();
        halfStrokeWidth = strokeWidth / 2;
        drawPaint.setStrokeWidth(strokeWidth);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float pointX = event.getX();
        float pointY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onActionDown(pointX, pointY);
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                onActionUp(event, pointX, pointY);
                break;
            default:
                return false;
        }

        invalidate(
                (int) (dirtyRect.left - halfStrokeWidth),
                (int) (dirtyRect.top - halfStrokeWidth),
                (int) (dirtyRect.right + halfStrokeWidth),
                (int) (dirtyRect.bottom + halfStrokeWidth));
        lastTouchX = pointX;
        lastTouchY = pointY;

        if (MotionEvent.ACTION_UP == event.getAction()) {
            canvas.drawPath(path, drawPaint);
            path.reset();
        }
        return true;
    }

    private void onActionDown(float pointX, float pointY) {
        switch (currentTool) {
            case TEXT_TOOL:
                drawText(pointX, pointY);
                break;
            case BRUSH_TOOL:
                path.moveTo(pointX, pointY);
                lastTouchX = pointX;
                lastTouchY = pointY;
                break;
            default:
                break;
        }
    }

    private void onActionUp(MotionEvent event, float pointX, float pointY) {
        if (currentTool == BRUSH_TOOL) {
            resetDirtyRect(pointX, pointY);

            int historySize = event.getHistorySize();

            for (int i = 0; i < historySize; i++) {
                float historicalX = event.getHistoricalX(i);
                float historicalY = event.getHistoricalY(i);
                expandDirtyRect(historicalX, historicalY);
                path.lineTo(historicalX, historicalY);
            }

            path.lineTo(pointX, pointY);
        }
    }

    private void expandDirtyRect(float historicalX, float historicalY) {
        if (historicalX < dirtyRect.left) {
            dirtyRect.left = historicalX;
        } else if (historicalX > dirtyRect.right) {
            dirtyRect.right = historicalX;
        }

        if (historicalY < dirtyRect.top) {
            dirtyRect.top = historicalY;
        } else if (historicalY > dirtyRect.bottom) {
            dirtyRect.bottom = historicalY;
        }
    }

    private void resetDirtyRect(float eventX, float eventY) {
        dirtyRect.left = Math.min(lastTouchX, eventX);
        dirtyRect.right = Math.max(lastTouchX, eventX);
        dirtyRect.top = Math.min(lastTouchY, eventY);
        dirtyRect.bottom = Math.max(lastTouchY, eventY);
    }

    @Override
    protected void onDraw(Canvas c) {
        c.drawBitmap(bitmap, 0, 0, canvasPaint);
        c.drawPath(path, drawPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        clearCanvas(w, h);
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

    private void clearCanvas(int width, int height) {
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.eraseColor(Color.WHITE);
            canvas = new Canvas(bitmap);
        } else {
            setBitmap(getResizedBitmap(bitmap, width, height));
        }
    }

    public void clear() {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        bitmap.recycle();
        bitmap = null;
        clearCanvas(width, height);
        invalidate();
    }

    public void setBrush() {
        currentTool = BRUSH_TOOL;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    public void setText(String text) {
        this.text = text;
        currentTool = TEXT_TOOL;
    }

    private void drawText(float pointX, float pointY) {
        canvas.drawText(text, pointX, pointY, fontPaint);
        invalidate();
    }
}

package ru.shmakova.painter.ui.android_views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CanvasView extends View {
    private final static int DEFAULT_COLOR = Color.BLACK;
    private final static int BRUSH_TOOL = 0;
    private final static int STAMP_TOOL = 1;
    private final static int TEXT_TOOL = 2;
    private final static int STROKE_WIDTH = 5;
    private Paint drawPaint;
    private Paint fontPaint;
    private Paint canvasPaint;
    private Path path;
    private Canvas canvas;
    private Bitmap bitmap;
    private int currentTool;
    private String text;
    private Bitmap stamp;

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

    /**
     * Initialization of paints and path
     */
    private void init() {
        setFocusable(true);
        setFocusableInTouchMode(true);
        setBrush();
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

    /**
     * Sets color
     * @param color of paint
     */
    public void setColor(int color) {
        invalidate();
        drawPaint.setColor(color);
        fontPaint.setColor(color);
    }

    public void setStrokeWidth(int strokeWidth) {
        invalidate();
        drawPaint.setStrokeWidth(strokeWidth);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float pointX = event.getX();
        float pointY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onActionDown(pointX, pointY);
                break;
            case MotionEvent.ACTION_MOVE:
                onActionMove(pointX, pointY);
                break;
            case MotionEvent.ACTION_UP:
                onActionUp();
                break;
            default:
                return false;
        }

        postInvalidate();
        return true;
    }

    /**
     * On action down
     * @param pointX - coordinate X
     * @param pointY - coordinate Y
     */
    private void onActionDown(float pointX, float pointY) {
        switch (currentTool) {
            case TEXT_TOOL:
                drawText(pointX, pointY);
                break;
            case BRUSH_TOOL:
                path.moveTo(pointX, pointY);
                break;
            case STAMP_TOOL:
                drawStamp(pointX, pointY);
                break;
        }
    }

    /**
     * On action move
     * @param pointX - coordinate X
     * @param pointY - coordinate Y
     */
    private void onActionMove(float pointX, float pointY) {
        switch (currentTool) {
            case BRUSH_TOOL:
                path.lineTo(pointX, pointY);
                break;
        }
    }

    /**
     * On action up
     */
    private void onActionUp() {
        switch (currentTool) {
            case BRUSH_TOOL:
                canvas.drawPath(path, drawPaint);
                path.reset();
                break;
        }
    }


    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(canvas);
        c.drawBitmap(bitmap, 0, 0, canvasPaint);
        c.drawPath(path, drawPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        clearCanvas(w, h);
    }

    /**
     * Applies gray-scale filter
     */
    public void applyGrayScaleFilter() {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        applyColorMatrix(colorMatrix);
    }

    /**
     * Applies negative filter
     */
    public void applyNegativeFilter() {
        ColorMatrix colorMatrix = new ColorMatrix();
        float[] negMat = {-1, 0, 0, 0, 255, 0, -1, 0, 0, 255, 0, 0, -1, 0, 255, 0, 0, 0, 1, 0};
        colorMatrix.set(negMat);
        applyColorMatrix(colorMatrix);
    }

    /**
     * Applies color matrix to current bitmap
     * @param colorMatrix - color matrix
     */
    private void applyColorMatrix(ColorMatrix colorMatrix) {
        int height = canvas.getHeight();
        int width = canvas.getWidth();
        Bitmap filteredBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(filteredBitmap);
        Paint paint = new Paint();
        ColorMatrixColorFilter colorMatrixColorFilter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(colorMatrixColorFilter);
        c.drawBitmap(bitmap, 0, 0, paint);
        setBitmap(filteredBitmap);
    }

    /**
     * Gets bitmap
     * @return bitmap
     */
    public Bitmap getBitmap() {
        return bitmap;
    }

    /**
     * Sets bitmap
     * @param bitmap new bitmap
     */
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        canvas = new Canvas(this.bitmap);
        invalidate();
    }

    /**
     * Clear canvas
     * @param width - width of bitmap
     * @param height - height
     */
    private void clearCanvas(int width, int height) {
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.WHITE);
        canvas = new Canvas(bitmap);
    }

    /**
     * Clear screen
     */
    public void clear() {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        clearCanvas(width, height);
        invalidate();
    }

    /**
     * Sets brush tool
     */
    public void setBrush() {
        currentTool = BRUSH_TOOL;
    }

    /**
     * Sets stamp tool
     * @param stamp for drawing
     */
    public void setStamp(Bitmap stamp) {
        this.stamp = stamp;
        currentTool = STAMP_TOOL;
    }

    /**
     * Sets text tool
     * @param text for drawing
     */
    public void setText(String text) {
        this.text = text;
        currentTool = TEXT_TOOL;
    }

    /**
     * Draws text in the point with coordinates
     * @param pointX - coordinate X
     * @param pointY - coordinate Y
     */
    private void drawText(float pointX, float pointY) {
        canvas.drawText(text, pointX, pointY, fontPaint);
        invalidate();
    }

    /**
     * Draws stamp in the point with coordinates
     * @param pointX - coordinate X
     * @param pointY - coordinate Y
     */
    private void drawStamp(float pointX, float pointY) {
        int height = canvas.getHeight();
        int width = canvas.getWidth();
        Bitmap overlayBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlayBitmap);
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.drawBitmap(stamp, pointX, pointY, null);
        setBitmap(overlayBitmap);
    }
}

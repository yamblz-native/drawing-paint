package ru.yandex.yamblz.ui.drawing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawerView extends View implements Drawer {

    private static final int BACKGROUND_COLOR = Color.WHITE;

    private Bitmap mBitmap;
    private Paint mPaint;
    private Paint mFilterPaint;
    private Canvas mCanvas;
    private Path mPath;
    private float mSize = 10;
    private int mColor;
    private Tool mTool = Tool.BRUSH;

    private float mPrevTouchX, mPrevTouchY;

    private ColorMatrixColorFilter mGrayscaleFilter, mSepiaFilter, mBinaryFilter, mInvertFilter;

    public DrawerView(Context context) {
        super(context);
        init();
    }

    public DrawerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mFilterPaint = new Paint();
        mPath = new Path();

        initFilters();
        brush();
    }

    private void initFilters() {
        mGrayscaleFilter = getGrayscaleFilter();
        mSepiaFilter = getSepiaFilter();
        mBinaryFilter = getBinaryFilter();
        mInvertFilter = getInvertFilter();
    }

    private ColorMatrixColorFilter getGrayscaleFilter() {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        return new ColorMatrixColorFilter(colorMatrix);
    }

    private ColorMatrixColorFilter getSepiaFilter() {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);

        ColorMatrix colorScale = new ColorMatrix();
        colorScale.setScale(1, 1, 0.8f, 1);
        colorMatrix.postConcat(colorScale);
        return new ColorMatrixColorFilter(colorMatrix);
    }

    private ColorMatrixColorFilter getBinaryFilter() {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);

        final float m = 255f;
        final float t = -255 * 128f;
        ColorMatrix threshold = new ColorMatrix(new float[] {
                m, 0, 0, 1, t,
                0, m, 0, 1, t,
                0, 0, m, 1, t,
                0, 0, 0, 1, 0
        });
        colorMatrix.postConcat(threshold);
        return new ColorMatrixColorFilter(colorMatrix);
    }

    private ColorMatrixColorFilter getInvertFilter() {
        return new ColorMatrixColorFilter(new ColorMatrix(new float[] {
                -1,  0,  0,  0, 255,
                0, -1,  0,  0, 255,
                0,  0, -1,  0, 255,
                0,  0,  0,  1,   0
        }));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        int curW = mBitmap != null ? mBitmap.getWidth() : 0;
        int curH = mBitmap != null ? mBitmap.getHeight() : 0;

        if(curW >= w && curH >= h) {
            return;
        }

        if(curW < w) {
            curW = w;
        }

        if(curH < h) {
            curH = h;
        }

        if(curW == 0 || curH == 0) {
            return;
        }

        Bitmap newBitmap = Bitmap.createBitmap(curW, curH, Bitmap.Config.ARGB_8888);
        newBitmap.eraseColor(BACKGROUND_COLOR);

        Canvas newCanvas = new Canvas();
        newCanvas.setBitmap(newBitmap);
        if(mBitmap != null) {
            newCanvas.drawBitmap(mBitmap, 0, 0, null);
        }
        mBitmap = newBitmap;
        mCanvas = newCanvas;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, 0, 0, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                writeTouchCoordinates(event);
                return true;
            case MotionEvent.ACTION_MOVE:
                handleTouch(event);
                writeTouchCoordinates(event);
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return super.onTouchEvent(event);
    }

    private void writeTouchCoordinates(MotionEvent event) {
        mPrevTouchX = event.getX();
        mPrevTouchY = event.getY();
    }

    private void handleTouch(MotionEvent event) {
        switch (mTool) {
            case BRUSH:
                drawBrush(event);
                break;
            case ERASER:
                drawEraser(event);
                break;
        }
    }

    private void drawBrush(MotionEvent event) {
        drawLine(mPrevTouchX, mPrevTouchY, event.getX(), event.getY());
    }

    private void drawEraser(MotionEvent event) {
        drawLine(mPrevTouchX, mPrevTouchY, event.getX(), event.getY());
    }

    private void drawCircle(float x, float y, float radius) {
        mCanvas.drawCircle(x, y, radius, mPaint);
        invalidate();
    }

    private void drawRect(float left, float top, float right, float bottom) {
        mCanvas.drawRect(left, top, right, bottom, mPaint);
        invalidate();
    }

    private void drawLine(float x1, float y1, float x2, float y2) {
        mPath.reset();
        mPath.moveTo(x1, y1);
        mPath.lineTo(x2, y2);
        mCanvas.drawPath(mPath, mPaint);
        invalidate();
    }



    @Override
    public void setSize(float size) {
        mSize = size;

        mPaint.setStrokeWidth(size / 2);
    }

    @Override
    public float getSize() {
        return mSize;
    }

    private void brush() {
        mTool = Tool.BRUSH;

        mPaint.reset();

        mPaint.setAntiAlias(true);
        mPaint.setColor(mColor);
        mPaint.setStrokeWidth(mSize / 2);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    private void eraser() {
        mTool = Tool.ERASER;

        mPaint.reset();

        mPaint.setAntiAlias(true);
        mPaint.setColor(BACKGROUND_COLOR);
        mPaint.setStrokeWidth(mSize / 2);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void disable() {
        mPaint.reset();
        mTool = Tool.NO;
    }

    @Override
    public void setColor(int color) {
        mColor = color;
        if(mColor == 0) {
            return;
        }
        if(mTool != Tool.ERASER) {
            mPaint.setColor(mColor);
        }
    }


    @Override
    public int getColor() {
        return mColor;
    }

    @Override
    public void setBitmap(Bitmap bitmap) {
        mCanvas.drawBitmap(bitmap, 0, 0, null);
    }

    @Override
    public Bitmap getBitmap() {
        return mBitmap;
    }

    @Override
    public void clean() {
        mBitmap.eraseColor(BACKGROUND_COLOR);

        invalidate();
    }

    @Override
    public Tool getTool() {
        return mTool;
    }

    @Override
    public void selectTool(Tool tool) {
        switch (tool) {
            case BRUSH:
                brush();
                break;
            case ERASER:
                eraser();
                break;
            case NO:
                disable();
                break;
        }
    }

    @Override
    public void filter(Filter filter) {
        ColorMatrixColorFilter colorFilter = null;
        switch (filter) {
            case GRAYSCALE:
                colorFilter = mGrayscaleFilter;
                break;
            case SEPIA:
                colorFilter = mSepiaFilter;
                break;
            case BINARY:
                colorFilter = mBinaryFilter;
                break;
            case INVERT:
                colorFilter = mInvertFilter;
                break;
        }

        mFilterPaint.reset();
        mFilterPaint.setColorFilter(colorFilter);

        Bitmap bitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        mCanvas.setBitmap(bitmap);
        mCanvas.drawBitmap(mBitmap, 0, 0, mFilterPaint);
        mBitmap = bitmap;
        invalidate();
    }
}

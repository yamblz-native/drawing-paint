package ru.yandex.yamblz.ui.drawing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawerView extends View implements Drawer {

    private static final int BACKGROUND_COLOR = Color.WHITE;

    private static final String SIZE_EXTRA = "size";
    private static final String COLOR_EXTRA = "color";
    private static final String TOOL_EXTRA = "tool";
    private static final String SUPER_EXTRA = "super";

    //Bitmap to draw at
    private Bitmap mBitmap;

    //Stamp to put on
    private Bitmap mStamp;

    private Paint mPaint;
    private Paint mFilterPaint;
    private Canvas mCanvas;
    private Path mPath;

    //Current line width
    private float mSize = 10;
    //Current color
    private int mColor;
    //Current selected tool, brush by default
    private Tool mTool = Tool.BRUSH;

    //Previous touch position
    private float mPrevTouchX, mPrevTouchY;

    //Color filters
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
        int curBitmapWidth = mBitmap != null ? mBitmap.getWidth() : 0;
        int curBitmapHeight = mBitmap != null ? mBitmap.getHeight() : 0;

        //if current bitmap is larger than view then do nothing
        if(curBitmapWidth >= w && curBitmapHeight >= h) {
            return;
        }

        //make bitmap wider if view is wider
        if(curBitmapWidth < w) {
            curBitmapWidth = w;
        }

        //make bitmap taller if view is taller
        if(curBitmapHeight < h) {
            curBitmapHeight = h;
        }

        if(curBitmapWidth == 0 || curBitmapHeight == 0) {
            return;
        }

        //redraw on new bitmap
        Bitmap newBitmap = Bitmap.createBitmap(curBitmapWidth, curBitmapHeight, Bitmap.Config.ARGB_8888);
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
                if(mTool == Tool.STAMP) {
                    //draw stamp only on down
                    handleTouch(event);
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if(mTool != Tool.STAMP) {
                    //we don't need drawing stamp on move
                    handleTouch(event);
                }
                writeTouchCoordinates(event);
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return super.onTouchEvent(event);
    }

    /**
     * Memorizes last touch event coordinates
     * @param event touch event
     */
    private void writeTouchCoordinates(MotionEvent event) {
        mPrevTouchX = event.getX();
        mPrevTouchY = event.getY();
    }

    /**
     * Navigates touch event to selected tool
     * @param event touch event
     */
    private void handleTouch(MotionEvent event) {
        switch (mTool) {
            case BRUSH:
                drawBrush(event);
                break;
            case ERASER:
                drawEraser(event);
                break;
            case STAMP:
                drawStamp(event);
                break;
        }
    }

    /**
     * Draws stamp at the given position
     * @param event the event to retrieve position from
     */
    private void drawStamp(MotionEvent event) {
        mCanvas.drawBitmap(mStamp, event.getX() - mStamp.getWidth() / 2, event.getY()
                - mStamp.getHeight() / 2, mPaint);
        invalidate();
    }

    /**
     * Draws using brush at the given position
     * @param event the event to retrieve position from
     */
    private void drawBrush(MotionEvent event) {
        drawLine(mPrevTouchX, mPrevTouchY, event.getX(), event.getY());
    }

    /**
     * Draws using eraser at the given position
     * @param event the event to retrieve position from
     */
    private void drawEraser(MotionEvent event) {
        drawLine(mPrevTouchX, mPrevTouchY, event.getX(), event.getY());
    }

    /**
     * Draws line from (x1, y1) to (x2, y2)
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
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

    /**
     * Turns on the brush tool
     */
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

    /**
     * Turns on the eraser tool
     */
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

    /**
     * Turns on the stamp tool
     */
    private void stamp() {
        mTool = Tool.STAMP;

        mPaint.reset();

        mPaint.setColor(mColor);
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
        if(mTool == Tool.STAMP) {
            changeColorOfStamp(mColor);
        } else if(mTool != Tool.ERASER) {
            mPaint.setColor(mColor);
        }
    }

    @Override
    public int getColor() {
        return mColor;
    }

    @Override
    public void setBitmap(Bitmap bitmap) {
        if(mBitmap == null) {
            mBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            mCanvas = new Canvas(mBitmap);
        } else {
            mBitmap.eraseColor(BACKGROUND_COLOR);
            mCanvas.drawBitmap(bitmap, 0, 0, null);
        }
        invalidate();
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
    public void selectStamp(Bitmap stamp) {
        mTool = Tool.STAMP;

        mPaint.reset();

        mStamp = stamp.copy(Bitmap.Config.ARGB_8888, true);
        changeColorOfStamp(mColor);
    }

    @Override
    public Bitmap getStamp() {
        return mStamp;
    }

    /**
     * Changes color of the stamp
     * Finds all {@code != 0} pixels and sets the to the color
     * @param needColor the needed color
     */
    private void changeColorOfStamp(int needColor) {
        final int width = mStamp.getWidth();
        final int height = mStamp.getHeight();
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                final int color = mStamp.getPixel(i, j);
                if(color != 0) {
                    mStamp.setPixel(i, j, needColor);
                }
            }
        }
    }

    @Override
    public void selectTool(Tool tool) {
        if(mTool == tool) {
            return;
        }
        if(mTool == Tool.STAMP) {
            mStamp = null;
        }
        switch (tool) {
            case BRUSH:
                brush();
                break;
            case ERASER:
                eraser();
                break;
            case STAMP:
                stamp();
                break;
            case NO:
                disable();
                break;
            default:
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

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SUPER_EXTRA, super.onSaveInstanceState());
        bundle.putFloat(SIZE_EXTRA, mSize);
        bundle.putInt(COLOR_EXTRA, mColor);
        bundle.putString(TOOL_EXTRA, mTool.getName());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            setSize(bundle.getFloat(SIZE_EXTRA));
            setColor(bundle.getInt(COLOR_EXTRA));
            selectTool(Tool.findByName(bundle.getString(TOOL_EXTRA)));
            state = bundle.getParcelable(SUPER_EXTRA);
        }
        super.onRestoreInstanceState(state);
    }
}

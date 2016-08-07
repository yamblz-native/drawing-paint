package ru.yandex.yamblz.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Volha on 05.08.2016.
 */

public class PaintboxView extends View {

    public enum PaintMode {
        BRUSH, STAMP, TEXT
    }
    public enum ColorFilterMode {
        NORMAL, INVERT, BLACK_AND_WHITE;
    }

    private PaintMode mode = PaintMode.BRUSH;
    private Paint paint;
    private ArrayList<PathWrapper> paths;
    private float oldX, oldY;
    private int color = Color.BLACK;
    private Bitmap stamp;
    private String text;
    private Bitmap background;

    float[] normalColors = new float[]{
            1, 0, 0, 0, 0,
            0, 1, 0, 0, 0,
            0, 0, 1, 0, 0,
            0, 0, 0, 1, 0};
    float[] invertColors = new float[]{
            -1, 0, 0, 0, 255,
            0, -1, 0, 0, 255,
            0, 0, -1, 0, 255,
            0, 0, 0, 1, 0,};
    float[] bwColors = new float[]{
            0.3f, 0.59f, 0.11f, 0, 0,
            0.3f, 0.59f, 0.11f, 0, 0,
            0.3f, 0.59f, 0.11f, 0, 0,
            0, 0, 0, 1, 0,};

    public PaintboxView(Context context) {
        super(context);
        init(context);
    }

    public PaintboxView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PaintboxView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    private void init(Context context) {
        paint = new Paint();
        ColorMatrix cm = new ColorMatrix(normalColors);
        ColorFilter filter = new ColorMatrixColorFilter(cm);
        paint.setColor(Color.BLACK);
        paint.setColorFilter(filter);
        paint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, context.getResources().getDisplayMetrics()));
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 26, context.getResources().getDisplayMetrics()));
        paths = new ArrayList<>();
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setText(String text) {
        this.text = text;
        mode = PaintMode.TEXT;
    }

    public void setStamp(Bitmap image) {
        this.stamp = image;
        mode = PaintMode.STAMP;
    }

    public void setBrushOn(){
        mode = PaintMode.BRUSH;
    }

    public void setColorFilter(ColorFilterMode mode) {
        ColorMatrix cm;
        switch (mode) {
            case INVERT: cm = new ColorMatrix(invertColors); break;
            case BLACK_AND_WHITE: cm = new ColorMatrix(bwColors); break;
            default: cm = new ColorMatrix(normalColors); break;
        }

        ColorFilter filter = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(filter);
        background = getBitmap();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if ( background != null )
            canvas.drawBitmap(background, 0, 0, paint);

        for (PathWrapper pathWrapper : paths) {
            paint.setColor(pathWrapper.color);
            switch (pathWrapper.mode) {
                case BRUSH:
                    canvas.drawPath(pathWrapper.path, paint);
                    break;
                case TEXT:
                    canvas.drawText(pathWrapper.text, pathWrapper.x, pathWrapper.y, paint);
                    break;
                case STAMP:
                    canvas.drawBitmap(pathWrapper.image, pathWrapper.x, pathWrapper.y, paint);
                    break;
            }
        }
    }

    public void loadBitmap(@Nullable Bitmap bitmap) {

        if ( bitmap == null )
            return;

        background = bitmap;
        paths.clear();
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
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                PathWrapper pathWrapper = new PathWrapper();
                pathWrapper.color = color;
                pathWrapper.path.moveTo(x, y);
                pathWrapper.mode = mode;
                pathWrapper.text = text;
                pathWrapper.x = x;
                pathWrapper.y = y;
                pathWrapper.image = stamp;
                paths.add(pathWrapper);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                paths.get(paths.size() - 1).path.quadTo(oldX, oldY, x, y);
                paths.get(paths.size() - 1).x = x;
                paths.get(paths.size() - 1).y = y;
                invalidate();
                break;
        }
        oldX = x;
        oldY = y;
        return true;
    }

    static class PathWrapper {

        public Path path;
        public int color;
        public String text;
        public Bitmap image;
        public PaintMode mode;
        public float x,y;

        PathWrapper() {
            path = new Path();
        }
    }
}

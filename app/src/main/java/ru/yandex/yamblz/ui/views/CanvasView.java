package ru.yandex.yamblz.ui.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.v4.util.Pair;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class CanvasView extends View {
    private Paint mPaint;
    private Path mPath;
    private List<Pair<Path, Paint>> mPathList = new ArrayList<>();
    private Bitmap mBitmap;

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

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, 0, 0, null);
        }
        drawPathOnCanvas(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPath = new Path();
                mPathList.add(new Pair<>(mPath, new Paint(mPaint)));
                mPath.moveTo(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                mPath.lineTo(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:

                break;
            case MotionEvent.ACTION_CANCEL:

                break;
        }
        return true;
    }

    private void init() {
        mPath = new Path();

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
    }

    public void setPaintSize(float size) {
        mPaint.setStrokeWidth(size);
    }

    public void setPaintColor(int color) {
        mPaint.setColor(color);
    }

    public void setPaths(List<Pair<Path, Paint>> pathList) {
        mPathList = pathList;
    }

    public List<Pair<Path, Paint>> getPaths() {
        return mPathList;
    }

    public void removeLastPath() {
        if (mPathList != null) {
            int lastIndex = mPathList.size() - 1;
            if (lastIndex >= 0) {
                mPathList.remove(lastIndex);
                invalidate();
            }
        }
    }

    public Bitmap getBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawPathOnCanvas(canvas);
        canvas.save();

        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        invalidate();
    }

    private void drawPathOnCanvas(Canvas canvas) {
        for (Pair<Path, Paint> pair : mPathList) {
            canvas.drawPath(pair.first, pair.second);
        }
    }
}
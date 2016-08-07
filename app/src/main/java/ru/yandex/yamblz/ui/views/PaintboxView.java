package ru.yandex.yamblz.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Volha on 05.08.2016.
 */

public class PaintboxView extends View {

    Paint paint;
    ArrayList<PathWrapper> paths;
    float startX, startY;
    float x = 1, y = 1;
    int color = Color.BLACK;
    Bitmap background;

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
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, context.getResources().getDisplayMetrics()));
        paint.setStyle(Paint.Style.STROKE);
        paths = new ArrayList<>();
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if ( background != null )
            canvas.drawBitmap(background, 0, 0, paint);
        for (PathWrapper pathWrapper : paths) {
            paint.setColor(pathWrapper.color);
            canvas.drawPath(pathWrapper.path, paint);
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
        x = event.getX();
        y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                paths.add(new PathWrapper());
                paths.get(paths.size() - 1).color = color;
                paths.get(paths.size() - 1).path.moveTo(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                paths.get(paths.size() - 1).path.quadTo(startX, startY, x, y);
                invalidate();
                break;
        }
        startX = x; startY = y;
        return true;
    }

    static class PathWrapper {

        Path path;
        public int color;

        PathWrapper() {
            path = new Path();
        }
    }
}

package ru.yandex.yamblz.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import icepick.Icepick;
import icepick.State;
import ru.yandex.yamblz.tools.BrushTool;
import ru.yandex.yamblz.tools.Tool;

/**
 * Created by Aleksandra on 02/08/16.
 */
public class DrawingView extends View {
    public static final String DEBUG_TAG = DrawingView.class.getName();

    private Paint backgroundPaint;

    @State
    int color = Color.BLACK;

    private Canvas drawCanvas;

    @State
    Bitmap bitmap;

    private Tool tool = new BrushTool();

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setupDrawing();
    }

    public void setupDrawing() {
        backgroundPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createScaledBitmap(bitmap, w, h, false);
        }
        drawCanvas = new Canvas(bitmap);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        tool.setColor(color);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        tool.onDraw(canvas, bitmap, backgroundPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        tool.onTouchEvent(event, drawCanvas);
        invalidate();
        return true;
    }

    public void setColorForTool(int color) {
        this.color = color;
        tool.setColor(this.color);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        return Icepick.saveInstanceState(this, super.onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));
        setupDrawing();
    }

    //Getters and setters

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        drawCanvas = new Canvas(bitmap);
    }


    public int getColor() {
        return color;
    }

}

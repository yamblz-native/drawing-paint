package ru.yandex.yamblz.ui.views;


import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

public class PaintView extends View {
    private Paint mPaint = new Paint();
    private RectF mRectF = new RectF(0, 0, 0, 0);

    public PaintView(Context context) {
        super(context);
    }

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PaintView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PaintView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawOval(mRectF, mPaint);
    }

    public void setColor(int color) {
        mPaint.setColor(color);
        invalidate();
    }

    public int getColor() {
        return mPaint.getColor();
    }

    public void setSize(float size) {
        int centerVertical = getHeight() / 2;
        int centerHorizontal = getWidth() / 2;
        int radius = (int) size / 2;
        mRectF = new RectF(centerHorizontal - radius, centerVertical - radius, centerHorizontal + radius, centerVertical + radius);
        invalidate();
    }
}

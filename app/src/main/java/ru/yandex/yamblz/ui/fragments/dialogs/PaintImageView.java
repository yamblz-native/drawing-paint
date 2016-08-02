package ru.yandex.yamblz.ui.fragments.dialogs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

public class PaintImageView extends ImageView {
    private Paint displayPaint;
    private Paint drawingPaint = new Paint();

    public PaintImageView(Context context) {
        super(context);
    }

    public PaintImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PaintImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Invert color but keep alpha.
     *
     * @param color color to invert.
     * @return inverted color.
     */
    public static int invertColor(int color) {
        return (0xFF000000 & color) | (0x00FFFFFF & ~color);
    }

    public void setPaint(Paint paint) {
        this.displayPaint = paint;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawingPaint.setColor(invertColor(displayPaint.getColor()));
        canvas.drawRect(0, 0, canvas.getWidth() / 2, canvas.getHeight(), drawingPaint);
        drawingPaint.setColor(displayPaint.getColor());
        canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2,
                displayPaint.getStrokeWidth() / 2, drawingPaint);
    }
}

package ru.yandex.yamblz.ui.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

public class PaintImageView extends ImageView {
    private Paint paint;

    public PaintImageView(Context context) {
        super(context);
    }

    public PaintImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PaintImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2,
                ((int) paint.getStrokeWidth()) / 2, paint);
    }
}

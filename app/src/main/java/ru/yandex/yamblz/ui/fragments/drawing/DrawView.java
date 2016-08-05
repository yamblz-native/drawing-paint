package ru.yandex.yamblz.ui.fragments.drawing;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * View which draws {@link CanvasDrawable}.
 */
public class DrawView extends View {

    private CanvasDrawable canvasDrawable;

    public DrawView(Context context) {
        super(context);
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canvasDrawable != null) {
            canvasDrawable.draw(canvas);
        }
    }

    /**
     * @param canvasDrawable {@link CanvasDrawable} to draw.
     */
    public void setCanvasDrawable(CanvasDrawable canvasDrawable) {
        this.canvasDrawable = canvasDrawable;
    }
}

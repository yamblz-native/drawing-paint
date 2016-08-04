package ru.yandex.yamblz.ui.fragments.dialogs;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import ru.yandex.yamblz.ui.fragments.brush.Brush;
import ru.yandex.yamblz.ui.fragments.brush.Point;

/**
 * Preview of a {@link Brush}.
 */
public class BrushView extends View {
    private Brush brush;
    private Point point = new Point(0, 0);

    public BrushView(Context context) {
        super(context);
    }

    public BrushView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BrushView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Set brush to preview.
     *
     * @param brush brush to preview.
     */
    public void setBrush(Brush brush) {
        this.brush = brush.copy();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        brush.finish();
        point.setX(canvas.getWidth() / 2);
        point.setY(canvas.getHeight() / 2);
        brush.start(point);
        brush.draw(canvas);
    }
}

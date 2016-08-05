package ru.yandex.yamblz.ui.fragments.brush;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class TextBrush extends StampBrush {
    private StringReference text = new StringReference();
    private Rect bounds = new Rect();

    public String getText() {
        return text.string;
    }

    public void setText(String text) {
        this.text.string = text;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float strokeWidth = paint.getStrokeWidth();
        Paint.Style style = paint.getStyle();

        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(strokeWidth);
        paint.setStrokeWidth(4);

        paint.getTextBounds(text.string, 0, text.string.length(), bounds);
        canvas.drawText(text.string,
                startX - bounds.width() / 2,
                startY + bounds.height() / 2,
                paint);

        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(style);
    }

    @Override
    public Brush copy() {
        TextBrush brush = new TextBrush();
        brush.text = text;
        return copy(brush);
    }

    @Override
    public int getId() {
        return -0xDEAD;
    }

    private static class StringReference {
        String string = "";
    }
}

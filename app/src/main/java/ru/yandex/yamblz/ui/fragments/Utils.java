package ru.yandex.yamblz.ui.fragments;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public class Utils {
    public static void drawCat(Canvas canvas, int color,
                               float centerX, float centerY, float radius) {
        final Paint paint = new Paint();
        final Path path = new Path();
        paint.setAntiAlias(true);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);
        paint.setColor(color);

        canvas.drawArc(centerX - radius, centerY - radius, centerX + radius, centerY + radius,
                -40, 180 + 40 + 40, false, paint);

        float ax = centerX + radius * ((float) Math.cos(Math.toRadians(40)));
        float ay = centerY - radius * ((float) Math.sin(Math.toRadians(40)));
        float bx = centerX + radius * ((float) Math.cos(Math.toRadians(60)));
        float by = centerY - radius * 0.9f * ((float) Math.sin(Math.toRadians(60)));

        canvas.drawLine(ax, ay, ax, ay - radius * 0.4f, paint);
        canvas.drawLine(bx, by, ax, ay - radius * 0.4f, paint);
        ax = centerX - radius * ((float) Math.cos(Math.toRadians(40)));
        float bx1 = centerX - radius * ((float) Math.cos(Math.toRadians(60)));
        canvas.drawLine(ax, ay, ax, ay - radius * 0.4f, paint);
        canvas.drawLine(bx1, by, ax, ay - radius * 0.4f, paint);
        canvas.drawLine(bx1, by, bx, by, paint);

        canvas.drawCircle(centerX - radius * 0.4f, centerY - radius * 0.3f, radius * 0.1f, paint);
        canvas.drawCircle(centerX + radius * 0.4f, centerY - radius * 0.3f, radius * 0.1f, paint);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawCircle(centerX - radius * 0.4f + radius * 0.03f, centerY - radius * 0.3f, radius * 0.03f, paint);
        canvas.drawCircle(centerX + radius * 0.4f + radius * 0.03f, centerY - radius * 0.3f, radius * 0.03f, paint);
        paint.setStyle(Paint.Style.STROKE);

        moustache(path, canvas, paint, centerX, centerY, radius, 0);
        moustache(path, canvas, paint, centerX, centerY, radius, 10);
        moustache(path, canvas, paint, centerX, centerY, radius, -10);

        canvas.drawArc(centerX - radius * 0.4f, centerY + radius * 0.2f,
                centerX, centerY + radius * 0.5f,
                0, 180, false, paint);
        canvas.drawArc(centerX, centerY + radius * 0.2f,
                centerX + radius * 0.4f, centerY + radius * 0.5f,
                0, 180, false, paint);

        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        path.reset();
        path.moveTo(centerX, centerY + radius * 0.35f);
        path.lineTo(centerX - radius * 0.1f, centerY + radius * 0.25f);
        path.lineTo(centerX + radius * 0.1f, centerY + radius * 0.25f);
        path.close();
        canvas.drawPath(path, paint);
        path.reset();
        path.moveTo(centerX, centerY + radius * 0.35f);
        path.addArc(centerX - radius * 0.1f, centerY + radius * 0.2f,
                centerX, centerY + radius * 0.3f, -180, 180);
        path.addArc(centerX, centerY + radius * 0.2f, centerX + radius * 0.1f,
                centerY + radius * 0.3f, -180, 180);
        path.close();
        canvas.drawPath(path, paint);
    }

    private static void moustache(Path path, Canvas canvas, Paint paint,
                                  float centerX, float centerY, float radius, float angle) {
        angle = ((float) Math.toRadians(angle));
        float ax = centerX + 0.8f * radius * ((float) Math.cos(angle));
        float ay = centerY - 0.8f * radius * ((float) Math.sin(angle));
        float bx = centerX + 1.2f * radius * ((float) Math.cos(angle));
        float by = centerY - 1.2f * radius * ((float) Math.sin(angle));
        path.reset();
        path.moveTo(ax, ay);
        path.lineTo(bx, by);
        canvas.drawPath(path, paint);

        ax = centerX - 0.8f * radius * ((float) Math.cos(angle));
        ay = centerY - 0.8f * radius * ((float) Math.sin(angle));
        bx = centerX - 1.2f * radius * ((float) Math.cos(angle));
        by = centerY - 1.2f * radius * ((float) Math.sin(angle));
        path.reset();
        path.moveTo(ax, ay);
        path.lineTo(bx, by);
        canvas.drawPath(path, paint);
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
}

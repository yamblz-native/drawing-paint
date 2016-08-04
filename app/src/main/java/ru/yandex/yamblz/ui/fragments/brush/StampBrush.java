package ru.yandex.yamblz.ui.fragments.brush;

/**
 * Helper class for implementing stamp-like brushes.
 * Overrides (startX, startY) on {@link #move(Point)}.
 */
@SuppressWarnings("WeakerAccess")
public abstract class StampBrush extends AbstractBrush {
    @Override
    public void move(Point point) {
        super.move(point);
        startX = point.getX();
        startY = point.getY();
    }
}

package ru.yandex.yamblz.ui.fragments.brush;

@SuppressWarnings("WeakerAccess")
public abstract class StampBrush extends AbstractBrush {
    @Override
    public void move(Point point) {
        super.move(point);
        startX = point.getX();
        startY = point.getY();
    }
}

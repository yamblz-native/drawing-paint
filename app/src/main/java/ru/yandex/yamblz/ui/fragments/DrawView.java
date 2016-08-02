package ru.yandex.yamblz.ui.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

public class DrawView extends ImageView {
    private Bitmap bitmap;
    private Canvas canvas;
    private TmpDrawer tmpDrawer;

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
    public void setImageBitmap(Bitmap bm) {
        bitmap = bm;
        canvas = new Canvas(bitmap);
        super.setImageBitmap(bm);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, 0, 0, null);
        tmpDrawer.drawTmp(canvas);
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void setTmpDrawer(TmpDrawer tmpDrawer) {
        this.tmpDrawer = tmpDrawer;
    }

    public interface TmpDrawer {
        void drawTmp(Canvas canvas);
    }
}

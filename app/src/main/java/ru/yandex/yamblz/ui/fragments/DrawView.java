package ru.yandex.yamblz.ui.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * View used for drawing on. Supports undo with fixed history size.
 */
public class DrawView extends ImageView {
    private Bitmap bitmap;
    private Canvas canvas;
    private TmpDrawer tmpDrawer;

    private Bitmap history[] = new Bitmap[0];
    private Canvas commitCanvas;
    private int historySize;

    public DrawView(Context context) {
        super(context);
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * @param history array of bitmaps which will be used for storing history.
     */
    public void setHistory(@NonNull Bitmap[] history) {
        this.history = history;
        historySize = 0;
    }

    /**
     * Creates array of bitmaps of the same size as this view.
     *
     * @param size size of output array.
     * @return array which can be passed to {@link #setHistory(Bitmap[])}.
     */
    public Bitmap[] createHistory(int size) {
        Bitmap history[] = new Bitmap[size];
        for (int i = 0; i < history.length; ++i) {
            history[i] = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        }
        return history;
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
        for (int i = 0; i < historySize; ++i) {
            canvas.drawBitmap(history[i], 0, 0, null);
        }
        tmpDrawer.drawTmp(canvas);
    }

    /**
     * Marks beginning of new action.
     * Affects history.
     * Erases oldest change if history is full.
     */
    public void beginNewAction() {
        if (history.length > 0) {
            if (historySize == history.length) {
                Bitmap bitmap0 = history[0];
                canvas.drawBitmap(bitmap0, 0, 0, null);
                //noinspection ManualArrayCopy
                for (int i = 1; i < history.length; ++i) {
                    history[i - 1] = history[i];
                }
                history[history.length - 1] = bitmap0;
                bitmap0.eraseColor(Color.TRANSPARENT);
            } else {
                ++historySize;
            }
            commitCanvas = new Canvas(history[historySize - 1]);
        }
    }

    /**
     * Get canvas to draw current action on.
     *
     * @return canvas.
     */
    public Canvas getActionCanvas() {
        if (history.length > 0) {
            return commitCanvas;
        } else {
            return canvas;
        }
    }

    /**
     * Creates a copy of drawn bitmap.
     * @return bitmap.
     */
    public Bitmap getDrawingCacheBitmap() {
        destroyDrawingCache();
        setDrawingCacheEnabled(true);
        buildDrawingCache();
        return getDrawingCache();
    }

    public boolean canUndo() {
        return historySize > 0;
    }

    public void undo() {
        history[--historySize].eraseColor(Color.TRANSPARENT);
    }

    public void setTmpDrawer(TmpDrawer tmpDrawer) {
        this.tmpDrawer = tmpDrawer;
    }

    /**
     * Implementing class can provide drawing of current action before it is finished.
     */
    public interface TmpDrawer {
        void drawTmp(Canvas canvas);
    }
}

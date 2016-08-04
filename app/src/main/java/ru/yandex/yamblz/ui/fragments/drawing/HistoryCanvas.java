package ru.yandex.yamblz.ui.fragments.drawing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.NonNull;

/**
 * Supports undo with fixed history size and drawing of temporary objects.
 */
@SuppressWarnings("WeakerAccess")
public class HistoryCanvas implements CanvasDrawable {
    private Bitmap bitmap;
    private Canvas canvas;
    private TmpDrawer tmpDrawer;

    private Bitmap history[] = new Bitmap[0];
    private Canvas commitCanvas;
    private int historySize;

    /**
     * Creates array of bitmaps of specified dimensions.
     *
     * @param size   size of output array.
     * @param width  width of output bitmaps.
     * @param height height of output bitmaps.
     * @return array which can be passed to {@link #setHistory(Bitmap[])}.
     */
    public static Bitmap[] createHistory(int size, int width, int height) {
        Bitmap history[] = new Bitmap[size];
        for (int i = 0; i < history.length; ++i) {
            history[i] = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }
        return history;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, 0, 0, null);
        for (int i = 0; i < historySize; ++i) {
            canvas.drawBitmap(history[i], 0, 0, null);
        }
        tmpDrawer.drawTmp(canvas);
    }

    /**
     * @param bitmap main bitmap to be drawn.
     */
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        canvas = new Canvas(this.bitmap);
    }

    /**
     * @param history array of bitmaps which will be used for storing history.
     */
    public void setHistory(@NonNull Bitmap[] history) {
        this.history = history;
        historySize = 0;
    }

    /**
     * Marks beginning of new object.
     * Affects history.
     * Erases oldest change if history is full.
     */
    public void beginNewObject() {
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
     * Get canvas to draw current object on.
     *
     * @return canvas.
     */
    public Canvas getObjectCanvas() {
        if (history.length > 0) {
            return commitCanvas;
        } else {
            return canvas;
        }
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
}

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
    private TmpDrawer tmpDrawer;

    private Bitmap bitmapStack[] = null;
    private Canvas commitCanvas;
    private int bitmapStackSize;

    /**
     * Creates array of bitmaps of specified dimensions.
     *
     * @param size   size of output array.
     * @param width  width of output bitmaps.
     * @param height height of output bitmaps.
     * @return array which can be passed to {@link #setBitmapStack(Bitmap[])}.
     */
    public static Bitmap[] createBitmapStack(int size, int width, int height) {
        Bitmap history[] = new Bitmap[size];
        for (int i = 0; i < history.length; ++i) {
            history[i] = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            history[i].eraseColor(Color.WHITE);
        }
        return history;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmapStack[bitmapStackSize - 1], 0, 0, null);
        tmpDrawer.drawTmp(canvas);
    }

    /**
     * @param bitmapStack array of bitmaps which will be used for drawing and storing bitmapStack.
     *                    Length should be at least 1.
     */
    public void setBitmapStack(@NonNull Bitmap[] bitmapStack) {
        this.bitmapStack = bitmapStack;
        bitmapStackSize = 1;
    }

    /**
     * Marks beginning of new object.
     * Affects bitmap stack.
     * Erases oldest change if bitmap stack is full.
     */
    public void beginNewObject() {
        if (bitmapStack.length == 1) {
            return;
        }

        if (bitmapStackSize == bitmapStack.length) {
            Bitmap bitmap0 = bitmapStack[0];
            //noinspection ManualArrayCopy
            for (int i = 1; i < bitmapStack.length; ++i) {
                bitmapStack[i - 1] = bitmapStack[i];
            }
            Canvas canvas = new Canvas(bitmap0);
            canvas.drawBitmap(bitmapStack[bitmapStack.length - 2], 0, 0, null);
            bitmapStack[bitmapStack.length - 1] = bitmap0;
        } else {
            ++bitmapStackSize;
            Canvas canvas = new Canvas(bitmapStack[bitmapStackSize - 1]);
            canvas.drawBitmap(bitmapStack[bitmapStackSize - 2], 0, 0, null);
        }
        commitCanvas = new Canvas(bitmapStack[bitmapStackSize - 1]);
    }

    /**
     * Get canvas to draw current object on.
     *
     * @return canvas.
     */
    public Canvas getObjectCanvas() {
        return commitCanvas;
    }

    public boolean canUndo() {
        return bitmapStackSize > 1;
    }

    public void undo() {
        bitmapStack[--bitmapStackSize].eraseColor(Color.TRANSPARENT);
    }

    public Bitmap getDrawnBitmap() {
        return bitmapStack[bitmapStackSize - 1];
    }

    public void setTmpDrawer(TmpDrawer tmpDrawer) {
        this.tmpDrawer = tmpDrawer;
    }
}

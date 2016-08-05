package ru.yandex.yamblz.ui.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import ru.yandex.yamblz.R;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by root on 8/5/16.
 */
public class CanvasView extends View {

    private Tool tool;
    private int choosenColor, printId;

    private Bitmap loadedBitmap;
    private Paint brushPaint, textPaint, printPaint;

    private List<Pair<Path, Integer>> paths;
    private List<Pair<String, Pair<PointF, Integer>>> texts;
    private List<Pair<Integer, PointF>> prints;

    private static final float BRUSH_STROKE = 2f;
    private static final float TEXT_SIZE = 70f;

    public CanvasView(Context context) {
        super(context);
        init();
    }

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CanvasView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CanvasView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {

        paths = new ArrayList<>();
        texts = new ArrayList<>();
        prints = new ArrayList<>();

        setTool(Tool.BRUSH);
        setColor(Color.BLACK);

        brushPaint = new Paint();
        brushPaint.setColor(Color.BLACK);
        brushPaint.setStrokeWidth(BRUSH_STROKE);
        brushPaint.setStyle(Paint.Style.STROKE);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setTextSize(TEXT_SIZE);

        printPaint = new Paint();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onActionDown(event);
                return true;
            case MotionEvent.ACTION_MOVE:
                onActionMove(event);
                return true;
        }
        return false;
    }

    private void onActionMove(MotionEvent event) {
        switch (tool) {
            case BRUSH:
                currentPath().lineTo(event.getX(), event.getY());
                invalidate();
                break;
        }
    }

    private Path currentPath() {
        return paths.get(paths.size() - 1).first;
    }

    private void onActionDown(MotionEvent event) {
        switch (tool) {
            case BRUSH:
                initPath(event.getX(), event.getY());
                break;
            case TEXT:
                drawText(event.getX(), event.getY());
                break;
            case PRINT:
                drawPrint(event.getX(), event.getY());
                break;
        }
    }

    private void drawPrint(float x, float y) {
        prints.add(new Pair<>(printId, new PointF(x, y)));
        invalidate();
    }

    private ColorFilter getColorFilter(Filter filter) {
        switch (filter) {
            case NO_FILTER:
                return originalColorFilter();
            case BLACK_AND_WHITE:
                return blackAndWhiteColorFilter();
            case SEPIA:
                return sepiaColorFilter();
        }
        return null;
    }

    private ColorFilter originalColorFilter() {
        return new ColorMatrixColorFilter(new ColorMatrix(new float[] {
                1, 0, 0, 0, 0,
                0, 1, 0, 0, 0,
                0, 0, 1, 0, 0,
                0, 0, 0, 1, 0
        }));
    }

    private ColorFilter blackAndWhiteColorFilter() {
        return new ColorMatrixColorFilter(new ColorMatrix(new float[] {
                0.213f, 0.715f, 0.072f, 0, 0,
                0.213f, 0.715f, 0.072f, 0, 0,
                0.213f, 0.715f, 0.072f, 0, 0,
                0,      0,      0,      1, 0
        }));
    }

    private ColorFilter sepiaColorFilter() {
        return new ColorMatrixColorFilter(new ColorMatrix(new float[] {
                1, 0, 0, 0, 0,
                0, 1, 0, 0, 0,
                0, 0, 0.8f, 0, 0,
                0, 0, 0, 1, 0
        }));
    }

    private void drawText(float x, float y) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.enter_text);

        EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE);
        builder.setView(input);

        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            texts.add(
                    new Pair<>(input.getText().toString(),
                            new Pair<>(new PointF(x, y), choosenColor)));
            invalidate();
        });

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void initPath(float x, float y) {
        createPath(choosenColor);
        currentPath().moveTo(x, y);
    }

    public void setTool(Tool tool) {
        this.tool = tool;
    }

    public void setColor(int color) {
        choosenColor = color;
    }

    private void createPath(int color) {
        paths.add(new Pair<>(new Path(), color));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(loadedBitmap != null) {
            canvas.drawBitmap(loadedBitmap, 0, 0, printPaint);
        }

        for (Pair<Integer, PointF> print : prints) {
            canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), print.first), print.second.x, print.second.y, printPaint);
        }

        for (Pair<String, Pair<PointF, Integer>> text : texts) {
            textPaint.setColor(text.second.second);
            canvas.drawText(text.first, text.second.first.x, text.second.first.y, textPaint);
        }

        for (Pair<Path, Integer> path : paths) {
            brushPaint.setColor(path.second);
            canvas.drawPath(path.first, brushPaint);
        }
    }

    public void applyFilter(Filter filter) {
        brushPaint.setColorFilter(getColorFilter(filter));
        textPaint.setColorFilter(getColorFilter(filter));
        printPaint.setColorFilter(getColorFilter(filter));
        invalidate();
    }

    public void setPrint(int drawableId) {
        setTool(Tool.PRINT);
        printId = drawableId;
    }

    public void saveBitmap() {

        Bitmap  bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        draw(canvas);

        File file = new File(Environment.getExternalStorageDirectory() + "/image.png");

        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file));
            Toast.makeText(getContext(), R.string.saved, Toast.LENGTH_LONG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadBitmap() {

        if(!new File(Environment.getExternalStorageDirectory() + "/image.png").exists()) {
            return;
        }

        Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/image.png");

        loadedBitmap = bitmap;
        invalidate();
        Toast.makeText(getContext(), R.string.loaded, Toast.LENGTH_LONG);
    }

    public enum Tool {
        BRUSH, TEXT, PRINT;
    }

    public enum Filter {
        NO_FILTER, BLACK_AND_WHITE, SEPIA;
    }

}

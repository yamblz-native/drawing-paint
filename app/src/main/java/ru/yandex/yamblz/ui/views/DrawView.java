package ru.yandex.yamblz.ui.views;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawView extends View {
    private final Path drawPath;
    private final Paint drawPaint;
    private Bitmap canvasBitmap;
    private Canvas drawCanvas;
    private Shader rainbowShader;
    private boolean enableRainbow;
    private ColorFilter curentColorFilter;

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }
    private static final int[] RAINBOW ={
            Color.RED,Color.YELLOW,Color.GREEN,Color.BLUE,Color.MAGENTA
    };

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPaint.setColorFilter(curentColorFilter);
        //все линии кроме текущих
        canvas.drawBitmap(canvasBitmap, 0, 0, drawPaint);
        //текущие линии
      //  drawPaint.setColorFilter(null);
        canvas.drawPath(drawPath, drawPaint);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
       // canvasBitmap.setHasAlpha(false);
        canvasBitmap.eraseColor(Color.WHITE);
        drawCanvas = new Canvas(canvasBitmap);
        rainbowShader = new LinearGradient(0, 0, w, h, RAINBOW,
                null, Shader.TileMode.MIRROR);
        setEnableRainbow(enableRainbow);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                //значения фильтра уже подсчитаны в drawPath
                drawPaint.setColorFilter(null);
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPaint.setColorFilter(curentColorFilter);
                drawPath.reset();
                break;
        }
        invalidate();
        return true;
    }

    public void setColor(int color){
        drawPaint.setColor(color);
    }

    public void setSize(int size) {
        drawPaint.setStrokeWidth(size);
    }

    public float getSize(){
        return drawPaint.getStrokeWidth();
    }

    public Bitmap getBitmap() {
        return canvasBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        drawCanvas.drawBitmap(bitmap,0,0,null);
    }

    public void setEnableRainbow(boolean enableRainbow) {
        this.enableRainbow = enableRainbow;
        if(enableRainbow){
            drawPaint.setShader(rainbowShader);
        }else{
            drawPaint.setShader(null);
        }
    }

    public void setColorFilter(ColorFilter colorFilter){
        this.curentColorFilter=colorFilter;
        drawPaint.setColorFilter(colorFilter);
        invalidate();
    }
}

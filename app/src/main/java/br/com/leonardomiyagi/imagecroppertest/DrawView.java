package br.com.leonardomiyagi.imagecroppertest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by SES\leonardom on 31/08/17.
 */

public class DrawView extends View {

    private Bitmap mbitmap;
    private Canvas mcanvas;
    private Path path;
    private Paint paint;
    private float mX, mY;
    Context context;
    private static final float Tolerance = 5;

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        path = new Path();
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(20f);
        setDrawingCacheEnabled(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mbitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mbitmap.setHasAlpha(true);
        mcanvas = new Canvas();
        mcanvas.setBitmap(mbitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, paint);
    }

    private void startTouch(float x, float y) {
        path.moveTo(x, y);
        mX = x;
        mY = y;
    }

    public void moveTouch(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= Tolerance || dy >= Tolerance) {
            path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;

        }
    }

    private void upTouch() {
        path.lineTo(mX, mY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                upTouch();
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                moveTouch(x, y);
                invalidate();
                break;

        }
        return true;
    }

    public Bitmap getBitmap() {
        return mbitmap;
    }
}

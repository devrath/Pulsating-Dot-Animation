package com.dotanimation;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {


    float SIZE_WHITE_DOT = 20;
    int RIPPLE_SPEED=80;
    int SIZE_OUTLINE_DIVISION_FACTOR=5;


    float SIZE_COLOURED_DOT = SIZE_WHITE_DOT;
    float SIZE_OUTLINE_DOT = SIZE_WHITE_DOT+(SIZE_WHITE_DOT/SIZE_OUTLINE_DIVISION_FACTOR);
    float MAX_SIZE_RIPPLE=SIZE_WHITE_DOT+SIZE_WHITE_DOT;

    Paint mPaint;
    Canvas mCanvas;
    Path mPath;
    MyView canvasView;

    int SIZE_PATH_STROKE = 15;
    int localTouchTolerance;
    int mTouchTolerance = 15;

    /*float SIZE_WHITE_DOT = 10;
    float SIZE_COLOURED_DOT = 10;
    float SIZE_OUTLINE_DOT = 16;*/


    
    float MAX_WHITE_DOT_SIZE=SIZE_WHITE_DOT;
    LinearLayout canvasLayoutId;
    int framesPerSecond = 5;
    long animationDuration = 10000; // 10 seconds
    int mAlpha=255; // Max
    int mPointX=0;
    int mPointY=0;
    enum Choice {DRAW_COLOURED, DRAW_OUTLINE, DRAW_RIPPLE}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findAbsoluteCentreOfScreen();
        canvasLayoutId=(LinearLayout) findViewById(R.id.canvasLayoutId);
        //Global paint object
        mPaint = new Paint();
        initilizePaintParams();
        //Initialize canvas view
        setTheCanvasView();
    }

    private void findAbsoluteCentreOfScreen() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mPointX=metrics.widthPixels/2;
        mPointY=metrics.heightPixels/2;
    }


    private void setTheCanvasView() {
        canvasView = new MyView(this);
        canvasView.setDrawingCacheEnabled(true);
        canvasLayoutId.addView(canvasView);
    }


    private void initilizePaintParams() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(SIZE_PATH_STROKE);
        localTouchTolerance = dp2px(mTouchTolerance);
    }


    private int dp2px(int dp) {
        Resources r = this.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return (int) px;
    }


    //---------------------------------------------------INNER-CLASS------------------------------------------------------------->

    public class MyView extends View {
        private Bitmap mBitmap;
        long startTime;

        public MyView(Context context) {
            super(context);
            mCanvas = new Canvas();
            mPath = new Path();
            // start the animation:
            this.startTime = System.currentTimeMillis();
            this.postInvalidate();
        }

        @Override
        protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
            super.onSizeChanged(width, height, oldWidth, oldHeight);
            clear();
        }


        @Override
        protected void onDraw(final Canvas canvas) {

            long elapsedTime = System.currentTimeMillis() - startTime;

            if(elapsedTime < animationDuration){
                this.postInvalidateDelayed( 1000 / framesPerSecond);
            }

            drawCircleOnCanvas(Choice.DRAW_COLOURED,canvas);
            drawCircleOnCanvas(Choice.DRAW_OUTLINE,canvas);
            drawCircleOnCanvas(Choice.DRAW_RIPPLE,canvas);
        }



        private void drawCircleOnCanvas(Choice choice,Canvas mCanvas) {

            switch(choice){
                case DRAW_COLOURED :
                    mPaint.setColor(ContextCompat.getColor(MainActivity.this, R.color.pattern_color));
                    mPaint.setStrokeWidth(SIZE_OUTLINE_DOT);
                   mCanvas.drawPoint(mPointX, mPointY, mPaint);
                    //mCanvas.drawCircle(mPointX, mPointY, SIZE_OUTLINE_DOT / 2, mPaint);
                    break;
                case DRAW_OUTLINE :
                    mPaint.setColor(Color.WHITE);
                    mPaint.setStrokeWidth(SIZE_COLOURED_DOT);
                    mCanvas.drawPoint(mPointX, mPointY, mPaint);

                    break;

                case DRAW_RIPPLE :
                    if(SIZE_WHITE_DOT<MAX_SIZE_RIPPLE){
                        mPaint.setColor(Color.WHITE);
                        mPaint.setAlpha(mAlpha);
                        mPaint.setStrokeWidth(SIZE_WHITE_DOT);
                        Display display = getWindowManager().getDefaultDisplay();
                        Point size = new Point();
                        display.getSize(size);
                        mCanvas.drawPoint(mPointX, mPointY, mPaint);
                        SIZE_WHITE_DOT = (float) (SIZE_WHITE_DOT + (SIZE_WHITE_DOT/RIPPLE_SPEED));//1000 milliseconds is one second.
                        mAlpha=mAlpha-2;
                    }else{
                        clear();
                        SIZE_WHITE_DOT=MAX_WHITE_DOT_SIZE;
                        mAlpha=255;
                    }
                    break;


            }
            invalidate();
        }



        @Override
        public boolean onTouchEvent(MotionEvent event) {
            return true;
        }

        public void clear() {
            if (getWidth() != 0 && getHeight() != 0) {
                mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
                mCanvas.setBitmap(mBitmap);
            }
            invalidate();
        }

    }
}

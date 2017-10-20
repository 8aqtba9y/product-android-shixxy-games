package com.syun.and.whiteoutmaze.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by qijsb on 2017/10/21.
 */

public class GameView extends SurfaceView implements SurfaceHolder.Callback2, View.OnTouchListener, Runnable {
    private static final String TAG = GameView.class.getSimpleName();

    private ExecutorService mExecutor;

    private SurfaceHolder mSurfaceHolder;
    private int mSurfaceWidth;
    private int mSurfaceHeight;

    private float preX, preY, postX, postY, deltaX, deltaY;
    private float distance;
    private static final float THRESHOLD = 10.0f;

    private Path path;
    private Paint paint;

    private float cX, cY;
    private boolean shouldFollow;
    private Paint cPaint;

    public GameView(Context context) {
        super(context);
        init();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mExecutor = Executors.newSingleThreadExecutor();

        path = new Path();

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(10.0f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);

        cPaint = new Paint();
        cPaint.setColor(Color.MAGENTA);
        cPaint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        getHolder().addCallback(this);
    }



    /*
        onResume:
        surfaceCreated:
        surfaceChanged:

        onPause:
        surfaceDestroyed:
     */

    @Override
    public void surfaceRedrawNeeded(SurfaceHolder surfaceHolder) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.d(TAG, "surfaceCreated: ");
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged: ");
        // TODO : should start drawing
        startDrawing(holder, width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.d(TAG, "surfaceDestroyed: ");
        stopDrawing();
    }

    public void startDrawing(SurfaceHolder holder, int width, int height) {
        this.mSurfaceHolder = holder;
        this.mSurfaceWidth = width;
        this.mSurfaceHeight = height;
        setOnTouchListener(this);

        cX = (float) (Math.random() * mSurfaceWidth);
        cY = (float) (Math.random() * mSurfaceHeight);

        if(mExecutor.isShutdown()){
            mExecutor = Executors.newSingleThreadExecutor();
        }
        mExecutor.submit(this);
    }

    public void stopDrawing() {
        if(!mExecutor.isShutdown()) {
            mExecutor.shutdownNow();
        }
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN :
                shouldFollow = true;
//                path.reset();

                preX = motionEvent.getX();
                preY = motionEvent.getY();

//                path.moveTo(preX, preY);

                break;

            case MotionEvent.ACTION_MOVE :
                postX = motionEvent.getX();
                postY = motionEvent.getY();

//                path.lineTo(postX, postY);

                deltaX = postX - preX;
                deltaY = postY - preY;
                distance = (float) Math.sqrt(
                        Math.pow(deltaX, 2) + Math.pow(deltaY, 2)
                );
                if(distance > THRESHOLD) {
                    // TODO : do not add path
                } else {
                    // TODO : add path
                }

                preX = postX;
                preY = postY;
                break;

            case MotionEvent.ACTION_UP :
                shouldFollow = false;
                break;
        }
        return true;
    }


    float dx, dy;
    private void draw() {
        Canvas canvas = mSurfaceHolder.lockCanvas();
        canvas.drawColor(Color.BLACK);
//        canvas.drawPath(path, paint);

        if(shouldFollow) {
            dx = preX - cX;
            dy = preY - cY;

            if(dx > 0) {
                cX++;
            } else {
                cX--;
            }

            if(dy > 0) {
                cY++;
            } else {
                cY--;
            }
        }
        canvas.drawCircle(cX, cY, 10.0f, cPaint);

        // TODO : blinking

        mSurfaceHolder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void run() {
        while(!mExecutor.isShutdown()) {
            draw();

            try {
                Thread.sleep(25);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
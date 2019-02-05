package com.thdoteo.inaudible;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.View;

import java.util.LinkedList;
import java.util.Queue;

class Point {
    public float y;
    public int color;
    public int radius;

    public Point(float y, int color, int radius)
    {
        this.y = y;
        this.color = color;
        this.radius = radius;
    }
}

public class WavesView extends View {

    int framesPerSecond = 60;

    Paint paint = new Paint();

    Queue<Point> points = new LinkedList<>();

    int color;

    float i = 0;

    int numberOfPointsPerFrame = 12;

    float currentAmplitude = 60;
    float targetAmplitude = 60;

    float currentFrequency = 1;
    float targetFrequency = 1;

    public WavesView(Context context, AttributeSet attrs) {
        super(context, attrs);

        color = ResourcesCompat.getColor(getResources(), R.color.colorAccent, null);

        addPoints();
        this.postInvalidate();
    }

    public void setFrequency(float frequency)
    {
        targetFrequency = frequency;
    }

    public void setAmplitude(float amplitude)
    {
        targetAmplitude = amplitude;
    }

    private void updateCurrentFrequency()
    {
        float difference = Math.abs(currentFrequency - targetFrequency);
        if (difference < 0.01f)
        {
            return;
        }

        if (currentFrequency > targetFrequency)
        {
            currentFrequency -= 0.001f;
        }
        else if (currentFrequency < targetFrequency)
        {
            currentFrequency += 0.001f;
        }
    }

    private void updateCurrentAmplitude()
    {
        if (currentAmplitude > targetAmplitude)
        {
            currentAmplitude -= 0.1;
        }
        else if (currentAmplitude < targetAmplitude)
        {
            currentAmplitude += 0.1;
        }
    }

    private void addPoints()
    {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                for (int repeat = 0; repeat < numberOfPointsPerFrame; repeat++)
                {
                    Point point1 = new Point((float) Math.sin(i * currentFrequency) * currentAmplitude, color, 3);
                    points.add(point1);
                    i += 0.01;

                    updateCurrentAmplitude();
                    updateCurrentFrequency();
                }

                while (!points.isEmpty() && points.size() >= getWidth())
                {
                    points.remove();
                }

                addPoints();
            }
        }, 1000 / framesPerSecond);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        int x = 0;
        for (Point p: points) {
            paint.setColor(p.color);
            canvas.drawCircle(x, 250 + p.y, p.radius, paint);
            x += 1;
        }

        this.postInvalidateDelayed(1000 / framesPerSecond);
    }
}

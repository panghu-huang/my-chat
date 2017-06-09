package com.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.mychat.R;

/**
 * Created by Logan on 2017/1/19.
 */

public class MyTabView extends View {

    private int width;
    private int tabWidth;
    private float left = 0;
    private int height;
    private Paint mPaint;
    private Paint mBottomPaint;
    private int mMainColor = Color.BLACK;
    private int mBottomColor = Color.GRAY;


    public MyTabView(Context context) {
        this(context, null);
    }

    public MyTabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MyTabView);
        mMainColor = typedArray.getColor(R.styleable.MyTabView_color, Color.BLACK);
        mBottomColor = typedArray.getColor(R.styleable.MyTabView_bottomColor, Color.GRAY);
        typedArray.recycle();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mMainColor);
        mBottomPaint = new Paint();
        mBottomPaint.setAntiAlias(true);
        mBottomPaint.setColor(mBottomColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (MeasureSpec.EXACTLY == widthMode && MeasureSpec.EXACTLY == heightMode) {
            setMeasuredDimension(widthSize, heightSize);
        }
        width = widthSize;
        height = heightSize;
        tabWidth = width / 4;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0, 0, width, height, mPaint);
        canvas.drawRect(left, 0, left + tabWidth, height, mBottomPaint);
    }

    public void setClickTab(int position, float offset) {
        left = (position + offset) * tabWidth;
        invalidate();
    }
}

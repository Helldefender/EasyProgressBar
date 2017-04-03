package com.helldefender.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Helldefender on 2017/4/3.
 */

public class RoundProgressBar extends View {

    private static final String TAG = RoundProgressBar.class.getSimpleName();

    private static final int DEFAULT_RADIUS = 80;

    private static final int DEFAULT_TEXT_COLOR = Color.parseColor("#000000");

    private static final int DEFAULT_TEXT_SIZE = 20;

    private static final int DEFAULT_UnREACH_COLOR = Color.parseColor("#FF6600");

    private static final int DEFAULT_UnREACH_HEIGHT = 6;

    private static final int DEFAULT_REACH_COLOR = Color.parseColor("#00CC00");

    private static final int DEFAULT_REACH_HEIGHT = 10;

    private static final int DEFAULT_FINISH_COLOR = Color.parseColor("#FF0000");

    private static final float DEFAULT_PROGRESS = 0f;

    private static final float DEFAULT_MAX_PROGRESS = 100f;

    private int currentState = CURRENT_STATE_INITIAL;

    private static final int CURRENT_STATE_INITIAL = 0;

    private static final int CURRENT_STATE_DOWNLOADING = 1;

    private static final int CURRENT_STATE_PAUSE = 2;

    private static final int CURRENT_STATE_FINISH = 3;

    private Context mContext;

    private int mRadius;

    private int mMaxPaintWidth;

    private int mTextColor;

    private int mTextSize;

    private int mReachColor;

    private int mReachHeight;

    private int mUnReachColor;

    private int mUnReachHeight;

    private int finishColor;

    private Paint reachPaint;

    private Paint unReachPaint;

    private Paint textPaint = new TextPaint();

    private RectF rectF;

    private float progress;

    private float maxProgress;

    private String progressText;

    private boolean isFinish = false;

    private boolean isStop = false;

    public RoundProgressBar(Context context) {
        this(context, null);
        mContext = context;
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
    }

    public RoundProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        getAttribute(attrs);
    }

    private void getAttribute(AttributeSet attributeSet) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attributeSet, R.styleable.RoundProgressBar);
        if (typedArray != null) {
            try {
                mRadius = (int) typedArray.getDimension(R.styleable.RoundProgressBar_round_radius, DisplayUtil.dip2px(mContext, DEFAULT_RADIUS));
                mTextColor = typedArray.getColor(R.styleable.RoundProgressBar_round_textColor, DEFAULT_TEXT_COLOR);
                mTextSize = (int) typedArray.getDimension(R.styleable.RoundProgressBar_round_textSize, DisplayUtil.sp2px(mContext, DEFAULT_TEXT_SIZE));
                mUnReachColor = typedArray.getColor(R.styleable.RoundProgressBar_round_progress_unReachColor, DEFAULT_UnREACH_COLOR);
                mUnReachHeight = (int) typedArray.getDimension(R.styleable.RoundProgressBar_round_progress_unReachHeight, DisplayUtil.dip2px(mContext, DEFAULT_UnREACH_HEIGHT));
                mReachColor = typedArray.getColor(R.styleable.RoundProgressBar_round_progress_reachColor, DEFAULT_REACH_COLOR);
                mReachHeight = (int) typedArray.getDimension(R.styleable.RoundProgressBar_round_progress_reachHeight, DisplayUtil.dip2px(mContext, DEFAULT_REACH_HEIGHT));
                finishColor = typedArray.getColor(R.styleable.RoundProgressBar_round_finishColor, DEFAULT_FINISH_COLOR);
                progress = typedArray.getDimension(R.styleable.RoundProgressBar_round_progress, DEFAULT_PROGRESS);
                maxProgress = typedArray.getDimension(R.styleable.RoundProgressBar_round_maxProgress, DEFAULT_MAX_PROGRESS);
            } catch (Exception e) {
                Log.e(TAG, "创建View失败", e);
            } finally {
                typedArray.recycle();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int width;
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int height;

        mMaxPaintWidth = Math.max(mReachHeight, mUnReachHeight);

        if (widthSpecSize < mRadius * 2 | heightSpecSize < mRadius * 2) {
            mRadius = (Math.min(widthSpecSize, heightSpecSize) - getPaddingLeft() - getPaddingRight() - mMaxPaintWidth) / 2;
        } else if (widthSpecSize < mRadius * 2 + mMaxPaintWidth | heightSpecSize < mRadius * 2 + mMaxPaintWidth) {
            mRadius = (Math.min(widthSpecSize, heightSpecSize) - -getPaddingLeft() - getPaddingRight()) / 2;
        }

        width = mRadius * 2 + mMaxPaintWidth + getPaddingLeft() + getPaddingRight();
        height = mRadius * 2 + mMaxPaintWidth + getPaddingLeft() + getPaddingRight();

        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(width, height);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(width, heightSpecSize);
        } else if (heightMeasureSpec == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, height);
        }
        initPainters();

//        int expect = mMaxPaintWidth + mRadius * 2 + getPaddingLeft() + getPaddingRight();
//        int width = resolveSize(expect, widthMeasureSpec);
//        int height = resolveSize(expect, heightMeasureSpec);
//        int readWidth = Math.min(width, height);
//        mRadius = (readWidth - getPaddingLeft() - getPaddingRight() - mMaxPaintWidth) / 2;
//        setMeasuredDimension(width, height);
    }

    private void initPainters() {
        reachPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        reachPaint.setStyle(Paint.Style.STROKE);
        reachPaint.setColor(mReachColor);
        reachPaint.setStrokeWidth(mReachHeight);

        unReachPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        unReachPaint.setStyle(Paint.Style.STROKE);
        unReachPaint.setColor(mUnReachColor);
        unReachPaint.setStrokeWidth(mUnReachHeight);

        textPaint.setColor(mTextColor);
        textPaint.setTextSize(mTextSize);

        rectF = new RectF(0, 0, mRadius * 2, mRadius * 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        String text = getProgressText();
        int textWidth = (int) textPaint.measureText(text);
        int textHeight = (int) ((textPaint.descent() + textPaint.ascent()) / 2);

        canvas.save();
        canvas.translate(getPaddingLeft() + mMaxPaintWidth / 2, getPaddingTop() + mMaxPaintWidth / 2);

        canvas.drawCircle(mRadius, mRadius, mRadius, unReachPaint);

        float sweepAngle = getProgress() * 1.0f / getMaxProgress() * 360;
        canvas.drawArc(rectF, 270, sweepAngle, false, reachPaint);

        canvas.drawText(text, mRadius - textWidth / 2, mRadius - textHeight, textPaint);

        canvas.restore();
    }

    private String getProgressText() {
        if (!isFinish) {
            if (!isStop) {
                progressText = progress + "%";
            } else {
                progressText = "暂停中";
            }
        } else {
            progressText = "下载完成";
        }
        return progressText;
    }

    public void setProgress(float progress) {
        if (!isStop) {
            if (progress < maxProgress) {
                this.progress = progress;
                setCurrentState(CURRENT_STATE_DOWNLOADING);
            } else {
                this.progress = maxProgress;
                setCurrentState(CURRENT_STATE_FINISH);
            }
        }
    }

    public void setStop(boolean isStop) {
        this.isStop = isStop;
        if (isStop) {
            setCurrentState(CURRENT_STATE_PAUSE);
        } else {
            setCurrentState(CURRENT_STATE_DOWNLOADING);
        }
    }

    private void setCurrentState(int currentState) {
        switch (this.currentState) {
            case CURRENT_STATE_INITIAL:
                break;
            case CURRENT_STATE_DOWNLOADING:
                if (currentState == CURRENT_STATE_PAUSE) {
                    reachPaint.setColor(Color.blue(mReachColor));
                    unReachPaint.setColor(Color.blue(mUnReachColor));
                } else if (currentState == CURRENT_STATE_FINISH) {
                    reachPaint.setColor(finishColor);
                }
                break;
            case CURRENT_STATE_PAUSE:
                reachPaint.setColor(mReachColor);
                unReachPaint.setColor(mUnReachColor);
        }
        this.currentState = currentState;
        invalidate();
    }

    public float getProgress() {
        return progress;
    }

    public float getMaxProgress() {
        return maxProgress;
    }

    public boolean isStop() {
        return isStop;
    }

    public boolean isFinish() {
        return isFinish;
    }
}

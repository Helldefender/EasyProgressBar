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
 * Created by Helldefender on 2017/4/2.
 */

public class HorizontalProgressBar extends View {

    private static final String TAG = HorizontalProgressBar.class.getSimpleName();

    private static final int DEFAULT_BACKGROUND_COLOR = Color.RED;

    private static final int DEFAULT_BACKGROUND_RADIUS = 1;

    private static final int DEFAULT_TEXT_COLOR = Color.parseColor("#000000");

    private static final int DEFAULT_TEXT_SIZE = 13;

    private static final int DEFAULT_TEXT_OFFSET = 5;

    private static final int DEFAULT_UnREACH_COLOR = Color.parseColor("#FF6600");

    private static final int DEFAULT_UnREACH_HEIGHT = 5;

    private static final int DEFAULT_REACH_COLOR = Color.parseColor("#00CC00");

    private static final int DEFAULT_REACH_HEIGHT = 6;

    private static final int DEFAULT_FINISH_COLOR = Color.parseColor("#FF0000");

    private static final int DEFAULT_BORDER_WIDTH = 2;

    private static final float DEFAULT_PROGRESS = 0f;

    private static final float DEFAULT_MAX_PROGRESS = 100f;

    private int currentState = CURRENT_STATE_INITIAL;

    private static final int CURRENT_STATE_INITIAL = 0;

    private static final int CURRENT_STATE_DOWNLOADING = 1;

    private static final int CURRENT_STATE_PAUSE = 2;

    private static final int CURRENT_STATE_FINISH = 3;

    private Context mContext;

    private int mTextColor;

    private int mTextSize;

    private int mTextOffset;

    private int mReachColor;

    private int mReachHeight;

    private int mUnReachColor;

    private int mUnReachHeight;

    private int finishColor;

    private Paint reachPaint;

    private Paint unReachPaint;

    private Paint bgPaint;

    private Paint textPaint = new TextPaint();

    private RectF bgRectF;

    private int borderWidth;

    private int bgRadius;

    private int backgroundColor;

    private int mWidth = 0;

    private float progress;

    private float maxProgress;

    private String progressText;

    private boolean isFinish = false;

    private boolean isStop = false;

    public HorizontalProgressBar(Context context) {
        this(context, null);
        mContext = context;
    }

    public HorizontalProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
    }

    public HorizontalProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        getAttribute(attrs);
    }

    private void getAttribute(AttributeSet attributeSet) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attributeSet, R.styleable.HorizontalProgressBar);
        if (typedArray != null) {
            try {
                backgroundColor = typedArray.getColor(R.styleable.HorizontalProgressBar_horizontal_backgroundColor, DEFAULT_BACKGROUND_COLOR);
                bgRadius = (int) typedArray.getDimension(R.styleable.HorizontalProgressBar_horizontal_backgroundRadius, DisplayUtil.dip2px(mContext, DEFAULT_BACKGROUND_RADIUS));
                mTextColor = typedArray.getColor(R.styleable.HorizontalProgressBar_horizontal_textColor, DEFAULT_TEXT_COLOR);
                mTextSize = (int) typedArray.getDimension(R.styleable.HorizontalProgressBar_horizontal_textSize, DisplayUtil.sp2px(mContext, DEFAULT_TEXT_SIZE));
                mTextOffset = (int) typedArray.getDimension(R.styleable.HorizontalProgressBar_horizontal_textOffset, DisplayUtil.dip2px(mContext, DEFAULT_TEXT_OFFSET));
                mUnReachColor = typedArray.getColor(R.styleable.HorizontalProgressBar_horizontal_progress_unReachColor, DEFAULT_UnREACH_COLOR);
                mUnReachHeight = (int) typedArray.getDimension(R.styleable.HorizontalProgressBar_horizontal_progress_unReachHeight, DisplayUtil.dip2px(mContext, DEFAULT_UnREACH_HEIGHT));
                mReachColor = typedArray.getColor(R.styleable.HorizontalProgressBar_horizontal_progress_reachColor, DEFAULT_REACH_COLOR);
                mReachHeight = (int) typedArray.getDimension(R.styleable.HorizontalProgressBar_horizontal_progress_reachHeight, DisplayUtil.dip2px(mContext, DEFAULT_REACH_HEIGHT));
                finishColor = typedArray.getColor(R.styleable.HorizontalProgressBar_horizontal_finishColor, DEFAULT_FINISH_COLOR);
                progress = typedArray.getDimension(R.styleable.HorizontalProgressBar_horizontal_progress, DEFAULT_PROGRESS);
                maxProgress = typedArray.getDimension(R.styleable.HorizontalProgressBar_horizontal_maxProgress, DEFAULT_MAX_PROGRESS);
                borderWidth = (int) typedArray.getDimension(R.styleable.HorizontalProgressBar_horizontal_borderWidth, DisplayUtil.dip2px(mContext, DEFAULT_BORDER_WIDTH));
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
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = 0;
        switch (heightSpecMode) {
            case MeasureSpec.AT_MOST:
                height = measureHeight(heightSpecSize);
                break;
            case MeasureSpec.EXACTLY:
            case MeasureSpec.UNSPECIFIED:
                height = heightSpecSize;
                break;
        }
        setMeasuredDimension(widthSpecSize, height);

        mWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight() - borderWidth * 4;
        initPainters();
    }

    private int measureHeight(int heightSpecSize) {
        int heightSize = heightSpecSize;
        int result = 0;
        int textHeight = (int) (textPaint.descent() - textPaint.ascent());
        int height = Math.max(Math.max(mReachHeight * 2, mUnReachHeight * 2), Math.abs(textHeight)) + getPaddingTop() + getPaddingBottom() + borderWidth * 4;
        result = Math.min(height, heightSize);
        return result;
    }

    private void initPainters() {
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setStrokeWidth(borderWidth);

        int textHeight = (int) (textPaint.descent() - textPaint.ascent());
        int height = Math.max(Math.max(mReachHeight * 2, mUnReachHeight * 2), Math.abs(textHeight));
        bgRectF = new RectF(borderWidth + getPaddingLeft(), getMeasuredHeight() / 2 - height / 2 - borderWidth, getMeasuredWidth() - borderWidth - getPaddingRight(), getMeasuredHeight() / 2 + height / 2 + borderWidth);

        reachPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        reachPaint.setStyle(Paint.Style.FILL);
        reachPaint.setColor(mReachColor);

        unReachPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        unReachPaint.setStyle(Paint.Style.FILL);
        unReachPaint.setColor(mUnReachColor);

        textPaint.setTextSize(mTextSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBackground(canvas);
        drawProgress(canvas);
    }

    private void drawBackground(Canvas canvas) {
        bgPaint.setColor(backgroundColor);
        canvas.drawRoundRect(bgRectF, bgRadius, bgRadius, bgPaint);
    }

    private void drawProgress(Canvas canvas) {
        canvas.save();
        canvas.translate(getPaddingLeft() + borderWidth * 2, getHeight() / 2);

        String progressText = getProgressText();
        int textWidth = (int) textPaint.measureText(progressText);

        float radio = getProgress() * 1.0f / getMaxProgress();
        int progressX = (int) (radio * mWidth);

        boolean noNeedUnReach = false;

        if (progressX + textWidth > mWidth) {
            noNeedUnReach = true;
            progressX = mWidth - textWidth;
        }

        float endX = progressX - mTextOffset / 2;
        if (endX > 0) {
            canvas.drawRoundRect(0, -mReachHeight, endX, mReachHeight, bgRadius, bgRadius, reachPaint);
        }

        int y = (int) (-(textPaint.descent() + textPaint.ascent()) / 2);
        textPaint.setColor(mTextColor);
        canvas.drawText(progressText, progressX, y, textPaint);

        if (!noNeedUnReach) {
            float start = progressX + textWidth + mTextOffset / 2;
            canvas.drawRoundRect(start, -mUnReachHeight, mWidth, mUnReachHeight, bgRadius, bgRadius, unReachPaint);
        }

        if (noNeedUnReach && getProgress() <= getMaxProgress()) {
            if (getProgress() == getMaxProgress())
                textPaint.setColor(finishColor);
            else
                textPaint.setColor(mReachColor);
            canvas.save(Canvas.CLIP_SAVE_FLAG);
            int right = (int) (radio * mWidth);
            canvas.clipRect(progressX, -getMeasuredHeight(), right, getMeasuredHeight());
            canvas.drawText(progressText, progressX, y, textPaint);
        }
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

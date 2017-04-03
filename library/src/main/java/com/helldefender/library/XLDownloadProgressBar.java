package com.helldefender.library;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by Helldefender on 2017/3/24.
 */

public class XLDownloadProgressBar extends View implements View.OnClickListener {

    private static final String TAG = XLDownloadProgressBar.class.getSimpleName();

//    private static final String INSTANCE_STATE = "instanceState";
//
//    private static final String INSTANCE_BACKGROUND_COLOR = "backgroundColor";
//
//    private static final String INSTANCE_FRONT_COLOR = "frontColor";
//
//    private static final String INSTANCE_BEHIND_COLOR = "behindColor";
//
//    private static final String INSTANCE_RADIUS = "radius";
//
//    private static final String INSTANCE_WAVE_LENGTH = "waveLength";
//
//    private static final String INSTANCE_WAVE_HEIGHT = "waveHeight";
//
//    private static final String INSTANCE_WAVE_INITIAL_HEIGNTH = "waveInitialHeight";
//
//    private static final String INSTANCE_TEXT_COLOR = "textColor";
//
//    private static final String INSTANCE_TEXT_SIZE = "textSize";
//
//    private static final String INSTANCE_PROGRESS = "progress";
//
//    private static final String INSTANCE_MAX_PROGRESS = "maxProgress";

    private int DEFAULT_BACKGROUND_COLOR = Color.YELLOW;

    private int DEFAULT_FRONT_COLOR = Color.RED;

    private int DEFAULT_BEHIND_COLOR = Color.BLUE;

    private int DEFAULT_WAVE_INITIAL_HEIGHT = 30;

    private int DEFAULT_WAVE_LENGTH = 180;

    private int DEFAULT_WAVE_HEIGHT = 15;

    private int DEFAULT_RADIUS = 80;

    private int DEFAULT_BORDER_WIDTH = 10;

    private int DEFAULT_TEXT_SIZE = 18;

    private int DEFAULT_TEXT_COLOR = Color.RED;

    private float DEFAULT_PROGRESS = 0f;

    private float DEFAULT_MAX_PROGRESS = 100f;

    private int currentState = CURRENT_STATE_INITIAL;

    private static final int CURRENT_STATE_INITIAL = 0;

    private static final int CURRENT_STATE_DOWNLOADING = 1;

    private static final int CURRENT_STATE_PAUSE = 2;

    private static final int CURRENT_STATE_FINISH = 3;

    private Context mContext;

    private int mScreenHeight;

    private int mScreenWidth;

    private Paint backgroundPaint;

    private int backgroundColor;

    private Paint circlePaint;

    private int radius;

    private int borderWidth;

    private Path frontPath;

    private int frontColor;

    private Path behindPath;

    private int behindColor;

    private int waveLength;

    private int waveHeight;

    private float waveProgressHeight;

    private int waveInitialHeight;

    private int mWaveCount;

    private int mOffset;

    private float progress;

    private float maxProgress;

    private Paint textPaint;

    private Rect textRect;

    private int textSize;

    private int textColor;

    private String progressText;

    private Shader sweepGradient;

    private Paint sweepPaint;

    private Matrix matrix;

    private int rotateDegree = 0;

    private RectF arcRect;

    private Canvas waveCanvas;

    private Paint waveCanvasPaint;

    private Bitmap waveCanvasBitmap;

    private BitmapShader bitmapShader;

    private PorterDuffXfermode porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP);

    private int bubbleHeight;

    private Bitmap bubbleBitmap;

    private Shader frontShader;

    private ValueAnimator animator;

    private boolean isFinish = false;

    private boolean isStop = false;

    public XLDownloadProgressBar(Context context) {
        this(context, null, 0);
        mContext = context;
    }

    public XLDownloadProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
    }

    public XLDownloadProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        getAttribute(attrs);
    }

    private void getAttribute(AttributeSet attributeSet) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attributeSet, R.styleable.XLDownloadProgressBar);
        if (typedArray != null) {
            try {
                backgroundColor = typedArray.getColor(R.styleable.XLDownloadProgressBar_xlDownload_backgroundColor, DEFAULT_BACKGROUND_COLOR);
                frontColor = typedArray.getColor(R.styleable.XLDownloadProgressBar_xlDownload_frontColor, DEFAULT_FRONT_COLOR);
                behindColor = typedArray.getColor(R.styleable.XLDownloadProgressBar_xlDownload_behindColor, DEFAULT_BEHIND_COLOR);
                waveLength = (int) typedArray.getDimension(R.styleable.XLDownloadProgressBar_xlDownload_waveLength, DisplayUtil.dip2px(mContext, DEFAULT_WAVE_LENGTH));
                waveHeight = (int) typedArray.getDimension(R.styleable.XLDownloadProgressBar_xlDownload_waveHeight, DisplayUtil.dip2px(mContext, DEFAULT_WAVE_HEIGHT));
                radius = (int) typedArray.getDimension(R.styleable.XLDownloadProgressBar_xlDownload_radius, DisplayUtil.dip2px(mContext, DEFAULT_RADIUS));
                borderWidth = (int) typedArray.getDimension(R.styleable.XLDownloadProgressBar_xlDownload_borderWidth, DisplayUtil.dip2px(mContext, DEFAULT_BORDER_WIDTH));
                waveInitialHeight = (int) typedArray.getDimension(R.styleable.XLDownloadProgressBar_xlDownload_waveHeight, DisplayUtil.dip2px(mContext, DEFAULT_WAVE_INITIAL_HEIGHT));
                textSize = (int) typedArray.getDimension(R.styleable.XLDownloadProgressBar_xlDownload_textSize, DisplayUtil.sp2px(mContext, DEFAULT_TEXT_SIZE));
                textColor = typedArray.getColor(R.styleable.XLDownloadProgressBar_xlDownload_textColor, DEFAULT_TEXT_COLOR);
                progress = typedArray.getDimension(R.styleable.XLDownloadProgressBar_xlDownload_progress, DEFAULT_PROGRESS);
                maxProgress = typedArray.getDimension(R.styleable.XLDownloadProgressBar_xlDownload_maxProgress, DEFAULT_MAX_PROGRESS);
            } catch (Exception e) {
                Log.e(TAG, "创建View失败", e);
            } finally {
                typedArray.recycle();
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mScreenHeight = h;
        mScreenWidth = w;
        mWaveCount = (int) Math.round(mScreenHeight / waveLength + 1.5);
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

        if (widthSpecSize < radius * 2 | heightSpecSize < radius * 2) {
            radius = (Math.min(widthSpecSize, heightSpecSize) - getPaddingLeft() - getPaddingRight() - borderWidth * 7 / 2) / 2;
        } else if (widthSpecSize < radius * 2 + borderWidth * 3 | heightSpecSize < radius * 2 + borderWidth * 7 / 2) {
            radius = (Math.min(widthSpecSize, heightSpecSize) - borderWidth * 7 / 2 - getPaddingLeft() - getPaddingRight()) / 2;
        }
        width = radius * 2 + borderWidth * 7 / 2 + getPaddingLeft() + getPaddingRight();
        height = radius * 2 + borderWidth * 7 / 2 + getPaddingLeft() + getPaddingRight();

        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(width, height);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(width, heightSpecSize);
        } else if (heightMeasureSpec == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, height);
        }

        initPainters();
    }

    private void initPainters() {
        waveCanvasBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        waveCanvas = new Canvas(waveCanvasBitmap);

        backgroundPaint = new Paint();
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setColor(backgroundColor);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(borderWidth);
        circlePaint.setColor(Color.WHITE);
        circlePaint.setAntiAlias(true);

        frontPath = new Path();
        frontShader = new LinearGradient(getMeasuredWidth() / 2, getMeasuredHeight() / 2 - radius, getMeasuredWidth() / 2, getMeasuredHeight() / 2 + radius, frontColor, Color.WHITE, Shader.TileMode.REPEAT);

        behindPath = new Path();

        textPaint = new Paint();
        textRect = new Rect();
        textPaint.setTextSize(textSize);

        int[] changeColors = new int[]{Color.argb(30, 230, 0, 0),
                Color.argb(30, 230, 0, 0),
                Color.argb(255, 230, 0, 0)};

        sweepPaint = new Paint();
        sweepGradient = new SweepGradient(getMeasuredWidth() / 2, getMeasuredHeight() / 2, changeColors, null);

        matrix = new Matrix();
        arcRect = new RectF(getMeasuredWidth() / 2 - radius - borderWidth, getMeasuredHeight() / 2 - radius - borderWidth, getMeasuredWidth() / 2 + radius + borderWidth, getMeasuredHeight() / 2 + radius + borderWidth);

        waveCanvasPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        waveCanvasPaint.setStyle(Paint.Style.FILL);

        bubbleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bubble);
        bubbleHeight = getMeasuredHeight() / 2 + radius;

        setOnClickListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
        drawWaveCanvas(canvas);
        drawRotateProgress(canvas);
    }

    private void drawBackground(Canvas canvas) {
        canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, radius, backgroundPaint);
    }

    private void drawWaveCanvas(Canvas canvas) {
        waveCanvas.save(Canvas.CLIP_SAVE_FLAG);
        Path path = new Path();
        path.addCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, radius, Path.Direction.CCW);
        waveCanvas.clipPath(path, Region.Op.REPLACE);
        waveCanvas.drawColor(backgroundColor);
        waveCanvas.save();

        drawProgressText();

        drawWave();

        drawTransformProgressText();

        if (!isStop & !isFinish) {
            waveCanvasPaint.setXfermode(porterDuffXfermode);
            waveCanvas.drawBitmap(bubbleBitmap, (getMeasuredWidth() - bubbleBitmap.getWidth()) / 2, bubbleHeight, waveCanvasPaint);
            waveCanvasPaint.setXfermode(null);
        }

        bitmapShader = new BitmapShader(waveCanvasBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        waveCanvasPaint.setShader(bitmapShader);
        canvas.drawPaint(waveCanvasPaint);
    }

    private void drawWave() {
        frontPath.reset();
        behindPath.reset();

        waveProgressHeight = (int) (getMeasuredHeight() / 2 + radius - waveInitialHeight - (progress / maxProgress) * (2 * radius - waveInitialHeight + waveHeight / 2));

        frontPath.moveTo(-waveLength + mOffset, waveProgressHeight);
        behindPath.moveTo((waveLength * mWaveCount) - mOffset, waveProgressHeight);

        for (int i = 0; i < mWaveCount; i++) {
            behindPath.quadTo((-waveLength / 4) + ((mWaveCount - i) * waveLength) - mOffset, waveProgressHeight - waveHeight, (-waveLength / 2) + (mWaveCount - i) * waveLength - mOffset, waveProgressHeight);
            behindPath.quadTo((-waveLength * 3 / 4) + ((mWaveCount - i) * waveLength) - mOffset, waveProgressHeight + waveHeight, (-waveLength) + ((mWaveCount - i) * waveLength) - mOffset, waveProgressHeight);

            frontPath.quadTo((-waveLength * 3 / 4) + (i * waveLength) + mOffset, waveProgressHeight + waveHeight, (-waveLength / 2) + (i * waveLength) + mOffset, waveProgressHeight);
            frontPath.quadTo((-waveLength / 4) + (i * waveLength) + mOffset, waveProgressHeight - waveHeight, i * waveLength + mOffset, waveProgressHeight);
        }

        frontPath.lineTo(mScreenWidth, mScreenHeight);
        frontPath.lineTo(0, mScreenHeight);
        frontPath.close();

        behindPath.lineTo(0, mScreenHeight);
        behindPath.lineTo(mScreenWidth, mScreenHeight);
        behindPath.close();

        waveCanvasPaint.reset();
        waveCanvasPaint.setStyle(Paint.Style.FILL);
        waveCanvasPaint.setColor(behindColor);
        waveCanvas.drawPath(behindPath, waveCanvasPaint);

        waveCanvasPaint.reset();
        waveCanvasPaint.setStyle(Paint.Style.FILL);
        waveCanvasPaint.setColor(frontColor);
        waveCanvasPaint.setShader(frontShader);
        waveCanvas.drawPath(frontPath, waveCanvasPaint);

        frontPath.op(behindPath, Path.Op.UNION);
        waveCanvas.clipPath(frontPath);
    }

    private void drawProgressText() {
        textPaint.setColor(textColor);
        textPaint.getTextBounds(getProgressText(), 0, progressText.length(), textRect);
        int textWidth = textRect.width();
        int textHeight = textRect.height();
        float xCoordinate = (getMeasuredWidth() - textWidth) / 2;
        float yCoordinate = (getMeasuredHeight() + textHeight) / 2;
        waveCanvas.drawText(progressText, xCoordinate, yCoordinate, textPaint);
        waveCanvas.save();
    }

    private void drawTransformProgressText() {
        textPaint.setColor(Color.WHITE);
        int textWidth = textRect.width();
        int textHeight = textRect.height();
        float xCoordinate = (getMeasuredWidth() - textWidth) / 2;
        float yCoordinate = (getMeasuredHeight() + textHeight) / 2;
        waveCanvas.drawText(progressText, xCoordinate, yCoordinate, textPaint);
    }

    private void drawRotateProgress(Canvas canvas) {
        canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, borderWidth + radius, circlePaint);

        sweepPaint.reset();
        sweepPaint.setAntiAlias(true);
        sweepPaint.setStrokeWidth(borderWidth);
        sweepPaint.setStyle(Paint.Style.STROKE);

        matrix.setRotate(rotateDegree, getMeasuredWidth() / 2, getMeasuredHeight() / 2);
        sweepGradient.setLocalMatrix(matrix);
        sweepPaint.setShader(sweepGradient);

        canvas.drawArc(arcRect, rotateDegree + 90, 270, false, sweepPaint);

        int headBallX = (int) (getMeasuredWidth() / 2 + ((radius + borderWidth) * Math.cos((double) rotateDegree / 180 * Math.PI)));
        int headBallY = (int) (getMeasuredHeight() / 2 + ((radius + borderWidth) * Math.sin((double) rotateDegree / 180 * Math.PI)));

        sweepPaint.reset();
        sweepPaint.setAntiAlias(true);
        sweepPaint.setStyle(Paint.Style.FILL);
        sweepPaint.setColor(Color.argb(255, 230, 0, 0));

        canvas.drawCircle(headBallX, headBallY, (int) (borderWidth / 2 * 1.5), sweepPaint);
    }

    @Override
    public void onClick(View view) {
        switch (currentState) {
            case CURRENT_STATE_INITIAL:
                setCurrentState(CURRENT_STATE_DOWNLOADING);
                break;
            case CURRENT_STATE_DOWNLOADING:
                setCurrentState(CURRENT_STATE_PAUSE);
                break;
            case CURRENT_STATE_PAUSE:
                setCurrentState(CURRENT_STATE_DOWNLOADING);
                break;
            case CURRENT_STATE_FINISH:
                break;
        }
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

    private void setBubbleHeightAndRotate() {
        bubbleHeight -= DisplayUtil.dip2px(mContext, 20);
        rotateDegree += 30;
        float waveHorizontalLineHeight = getMeasuredHeight() / 2 + radius - waveInitialHeight - (progress / maxProgress) * (2 * radius - waveInitialHeight - waveHeight / 2);
        float height = waveHorizontalLineHeight - waveHeight / 2;

        if (bubbleHeight + waveCanvasBitmap.getHeight() < height) {
            bubbleHeight = getMeasuredHeight() / 2 + radius;
        }

        if (rotateDegree == 360) {
            rotateDegree = 0;
        }
    }

    public void finishDownload() {
        isFinish = true;
        animator.end();
        //waveProgressHeight = getMeasuredHeight() / 2 - radius - waveHeight * 2;
    }

    public void setStop(boolean isStop) {
        this.isStop = isStop;
        if (isStop) {
            setCurrentState(CURRENT_STATE_PAUSE);
        } else {
            setCurrentState(CURRENT_STATE_DOWNLOADING);
        }
    }

    public float getProgress() {
        return progress;
    }

    public void setCurrentState(int currentState) {
        switch (this.currentState) {
            case CURRENT_STATE_INITIAL:
                setOffset();
                break;
            case CURRENT_STATE_DOWNLOADING:
                if (currentState == CURRENT_STATE_DOWNLOADING) {
                    setBubbleHeightAndRotate();
                } else if (currentState == CURRENT_STATE_PAUSE) {
                    animator.pause();
                } else if (currentState == CURRENT_STATE_FINISH) {
                    finishDownload();
                }
                break;
            case CURRENT_STATE_PAUSE:
                animator.resume();
        }
        this.currentState = currentState;
        invalidate();
    }

    private void setOffset() {
        animator = ValueAnimator.ofInt(0, waveLength);
        animator.setDuration(1500);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOffset = (int) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        animator.start();
    }

    public int getCurrentState() {
        return currentState;
    }

    public boolean isStop() {
        return isStop;
    }

    public boolean isFinish() {
        return isFinish;
    }

//    @Override
//    protected Parcelable onSaveInstanceState() {
//        final Bundle bundle = new Bundle();
//        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
//        bundle.putInt(INSTANCE_BACKGROUND_COLOR, backgroundColor);
//        bundle.putInt(INSTANCE_FRONT_COLOR, frontColor);
//        bundle.putInt(INSTANCE_BEHIND_COLOR, behindColor);
//        bundle.putInt(INSTANCE_WAVE_HEIGHT, waveHeight);
//        bundle.putInt(INSTANCE_WAVE_LENGTH, waveLength);
//        bundle.putInt(INSTANCE_WAVE_INITIAL_HEIGNTH, waveInitialHeight);
//        bundle.putInt(INSTANCE_RADIUS, radius);
//        bundle.putInt(INSTANCE_TEXT_SIZE, textSize);
//        bundle.putInt(INSTANCE_TEXT_COLOR, textColor);
//        bundle.putFloat(INSTANCE_PROGRESS, progress);
//        bundle.putFloat(INSTANCE_MAX_PROGRESS, maxProgress);
//        return bundle;
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Parcelable state) {
//        if (state instanceof Bundle) {
//            final Bundle bundle = (Bundle) state;
//            backgroundColor = bundle.getInt(INSTANCE_BACKGROUND_COLOR);
//            frontColor = bundle.getInt(INSTANCE_FRONT_COLOR);
//            behindColor = bundle.getInt(INSTANCE_BEHIND_COLOR);
//            waveLength = bundle.getInt(INSTANCE_WAVE_LENGTH);
//            waveHeight = bundle.getInt(INSTANCE_WAVE_HEIGHT);
//            waveInitialHeight = bundle.getInt(INSTANCE_WAVE_INITIAL_HEIGNTH);
//            textColor = bundle.getInt(INSTANCE_TEXT_COLOR);
//            textSize = bundle.getInt(INSTANCE_TEXT_SIZE);
//            progress = bundle.getFloat(INSTANCE_PROGRESS);
//            maxProgress = bundle.getFloat(INSTANCE_MAX_PROGRESS);
//        }
//        super.onRestoreInstanceState(state);
//    }
}

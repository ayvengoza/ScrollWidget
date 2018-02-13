package com.zastupailo.widget.scroll.scrollwidget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.widget.EdgeEffectCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.EdgeEffect;
import android.widget.OverScroller;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ang on 13.02.18.
 */

public class HorizontalIconView extends View {

    private static final String TAG = "HorizontalIconView";
    private static final int INVALID_POINTER = MotionEvent.INVALID_POINTER_ID;

    // id of seelable pointer
    private int mActivePointerId = INVALID_POINTER;
    private List<Drawable> mDrawables;
    // lightning effect
    private EdgeEffectCompat mEdgeEffectLeft;
    private EdgeEffectCompat mEdgeEffectRight;
    private final List<Rect> mIconPosition = new ArrayList<>();
    private int mIconSize;
    private int mIconSpacing;
    private boolean mIsBeingDragged;
    private int mMaximumVelocity;
    private int mMinimumVelocity;
    private int mOverflingDistance;
    private int mOverscrollDistance;
    private float mPreviousX = 0;
    private int mScrollRange;
    private int mTouchSlop;
    private VelocityTracker mVelocityTracker;
    private OverScroller mScroller;
    private int mSkippedIconCount = 0;


    public HorizontalIconView(Context context) {
        super(context);
    }

    public HorizontalIconView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalIconView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public HorizontalIconView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context){
        final Resources res = context.getResources();
        mIconSize = res.getDimensionPixelSize(R.dimen.icon_size);
        mIconSpacing = res.getDimensionPixelSize(R.dimen.icon_spacing);

        final ViewConfiguration config = ViewConfiguration.get(context);
        mTouchSlop = config.getScaledTouchSlop();
        mMinimumVelocity = config.getScaledMinimumFlingVelocity();
        mMaximumVelocity = config.getScaledMaximumFlingVelocity();
        mOverflingDistance = config.getScaledOverflingDistance();
        mOverscrollDistance = config.getScaledOverscrollDistance();

        setWillNotDraw(false);

        mEdgeEffectLeft = new EdgeEffectCompat(context);
        mEdgeEffectRight = new EdgeEffectCompat(context);
        mScroller = new OverScroller(context);
        setFocusable(true);
    }

    public void setDrawables(List<Drawable> drawables){
        if(mDrawables == null){
            if(drawables == null){
                return;
            }
            requestLayout();
        } else if(drawables == null){
            requestLayout();
            mDrawables = null;
            return;
        } else if(mDrawables.size() == drawables.size()){
            invalidate();
        } else {
            requestLayout();
        }
        mDrawables = new ArrayList<>(drawables);
        mIconPosition.clear();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureHeight(int measureSpec){
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        int result;
        if(specMode == MeasureSpec.EXACTLY){
            result = specSize;
        } else {
            result = mIconSize + getPaddingTop() + getPaddingBottom();
            if(specMode == MeasureSpec.AT_MOST){
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private int measureWidth(int measureSpec){
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        final int icons = (mDrawables == null) ? 0 : mDrawables.size();
        final int iconSpace = mIconSize * icons;
        final int dividerSpace;
        if(icons <= 1){
            dividerSpace = 0;
        } else {
            dividerSpace = (icons - 1) * mIconSpacing;
        }
        final int maxSize = dividerSpace + iconSpace + getPaddingStart() + getPaddingEnd();

        int result;
        if(specMode == MeasureSpec.EXACTLY){
            result = specSize;
        } else {
            if(specMode == MeasureSpec.AT_MOST){
                result = Math.min(maxSize, specSize);
            } else {
                result = maxSize;
            }
        }

        if(maxSize > result){
            mScrollRange = maxSize - result;
        } else {
            mScrollRange = 0;
        }
        return result;
    }
}

package com.yugang.tcg.stickylayoutlibrary;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * https://github.com/DennisAu/Android-StickyLayout/tree/dev
 * <p>
 * the Sticky Layout(sl for shot) has 3 parts, which are the main layout and 2 children layouts.
 * <p>
 * Scroll up the screen after the lay1 reach its bottom, the lay2 will show. And if finger up when lay2's top reach the half screen, lay2 will auto move to the top of the screen.
 */
public class StickyLayout extends ViewGroup {
    public static final String TAG = "StickyLayout";

    private View mLay1;
    private View mLay2;

    private StickyLayoutChildLay1 mSl1;
    private StickyLayoutChildLay2 mSl2;


    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    private float mLastMotionY;

    public StickyLayout(Context context) {
        super(context);
        init(null, 0);
    }

    public StickyLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public StickyLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
//        final TypedArray a = getContext().obtainStyledAttributes(
//                attrs, R.styleable.StickyLayout, defStyle, 0);
//
//        mExampleString = a.getString(
//                R.styleable.StickyLayout_exampleString);
//        mExampleColor = a.getColor(
//                R.styleable.StickyLayout_exampleColor,
//                mExampleColor);
//        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
//        // values that should fall on pixel boundaries.
//        mExampleDimension = a.getDimension(
//                R.styleable.StickyLayout_exampleDimension,
//                mExampleDimension);
//
//        if (a.hasValue(R.styleable.StickyLayout_exampleDrawable)) {
//            mExampleDrawable = a.getDrawable(
//                    R.styleable.StickyLayout_exampleDrawable);
//            mExampleDrawable.setCallback(this);
//        }
//
//        a.recycle();


        mScroller = new Scroller(getContext());
    }

    @Override
    protected void onFinishInflate() {

        mLay1 = findViewById(R.id.sticky_lay1);
        mLay2 = findViewById(R.id.sticky_lay2);

        super.onFinishInflate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mLay1.measure(widthMeasureSpec, heightMeasureSpec);
        mLay2.measure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = getMeasuredWidth();
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = getPaddingRight();
        int bottom = getPaddingBottom();

        mLay1.layout(left, top, left + width - right, top + mLay1.getMeasuredHeight() - bottom);
        mLay2.layout(left, mLay1.getBottom(), left + width - right, mLay1.getBottom() + mLay2.getMeasuredHeight() - bottom);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN
                && event.getEdgeFlags() != 0) {
            return false;
        }

        final int action = event.getAction();

//        final float x = event.getX();
        final float y = event.getY();

        switch (action) {

            case MotionEvent.ACTION_DOWN:

                Log.e(TAG, "ACTION_DOWN#currentScrollY:" + getScrollY() + ", mLastMotionY:" + mLastMotionY);

                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }

                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                }
                mVelocityTracker.clear();
                mVelocityTracker.addMovement(event);

                mLastMotionY = y;
                break;


            case MotionEvent.ACTION_MOVE:

                final int deltaY = (int) (mLastMotionY - y);
                mLastMotionY = y;

                if (deltaY < 0) {
                    if (getScrollY() > 0) {
                        scrollBy(0, deltaY);
                    }
                } else if (deltaY > 0) {

                    mIsInEdge = getScrollY() <= childTotalHeight - height;

                    if (mIsInEdge) {

                        scrollBy(0, deltaY);

                    }

                }

                break;


            case MotionEvent.ACTION_UP:

                final VelocityTracker velocityTracker = mVelocityTracker;

                velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);

                int initialVelocity = (int) velocityTracker.getYVelocity();


                if ((Math.abs(initialVelocity) > mMinimumVelocity)

                        && getChildCount() > 0) {

                    fling(-initialVelocity);

                }


                releaseVelocityTracker();

                break;

        }


        return true;
    }

    @Override
    public void computeScroll() {

    }

    public void fling(int velocityY) {

        if (getChildCount() > 0) {

            mScroller.fling(getScrollX(), getScrollY(), 0, velocityY, 0, 0, 0,

                    maxScrollEdge);

            final boolean movingDown = velocityY > 0;

            awakenScrollBars(mScroller.getDuration());

            invalidate();

        }

    }

    private void obtainVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    private void releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }
}
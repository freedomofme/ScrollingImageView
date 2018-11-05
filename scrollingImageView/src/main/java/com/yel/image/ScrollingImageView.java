package com.yel.image;
/**
 * Created by yel.huang on 2018/1/29.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class ScrollingImageView extends FrameLayout {
    private ScrollType mScrollType;

    private View child;

    public enum ScrollType {
        /**
         * View从开始到结束，一直在滑动
         */
        SCROLL_WHOLE(0),

        /**
         * View只当在完整显示时才开始滚动
         */
        SCROLL_MIDDLE(1);

        ScrollType(int ni) {
            nativeInt = ni;
        }

        final int nativeInt;
    }

    private static final ScrollType[] mScrollTypeArray = {
            ScrollType.SCROLL_WHOLE,
            ScrollType.SCROLL_MIDDLE,
    };

    public ScrollingImageView(Context context) {
        super(context);
    }

    public ScrollingImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollingImageView(Context context, AttributeSet attrs,
                              int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ScrollingImageView,
                0, 0);

        try {
            int index = a.getInteger(R.styleable.ScrollingImageView_scrollType, 0);
            setScrollType(mScrollTypeArray[index]);
        } finally {
            a.recycle();
        }

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (child == null) {
            RecyclerView temp = getRecyclerView((ViewGroup) this.getParent());
            temp.addOnScrollListener(new ScrollListener(this));
            child = getChildAt(0);
        }
    }

    private RecyclerView getRecyclerView(ViewGroup v) {
        if (v == null) {
            return null;
        }
        if (v instanceof RecyclerView) {
            return (RecyclerView) v;
        }
        return getRecyclerView((ViewGroup) v.getParent());
    }


    public void setScrollType(ScrollType scrollType) {
        if (mScrollType != scrollType) {
            mScrollType = scrollType;
        }
    }

    public ScrollType getScrollType() {
        return mScrollType;
    }

    public void setPercent(float yPercent) {
        matrix = child.getMatrix();
        float scale;
        float dx = 0, dy = 0;

        int viewWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int viewHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        int drawableWidth = child.getMeasuredWidth();
        int drawableHeight = child.getMeasuredHeight();
        // Get the scale
        if (drawableWidth * viewHeight > drawableHeight * viewWidth) {
            scale = (float) viewHeight / (float) drawableHeight;
            dx = (viewWidth - drawableWidth * scale) * 0.5f;
        } else {
            scale = (float) viewWidth / (float) drawableWidth;

            // 两种不同的滚动方式
            if (mScrollType == ScrollType.SCROLL_WHOLE) {
                dy = viewHeight - (viewHeight + drawableHeight * scale) * yPercent;
            } else if (mScrollType == ScrollType.SCROLL_MIDDLE) {
                dy = (viewHeight - drawableHeight * scale) * yPercent;
            }
        }
        Log.i("ScrollListener", "dy:" + dy);
        matrix.setScale(scale, scale);
        matrix.postTranslate(Math.round(dx), Math.round(dy));
        invalidate();
    }

    private int getViewHeight() {
        return getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
    }

    Matrix matrix;


    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.concat(matrix);
        super.dispatchDraw(canvas);
        canvas.restore();
    }


    private class ScrollListener extends RecyclerView.OnScrollListener {
        int ydy = 0;
        private ScrollingImageView child;

        ScrollListener(ScrollingImageView child) {
            this.child = child;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            ydy += dy;
            if (child != null) {
                float Offset = child.getTop();
                int viewHeight = child.getViewHeight();

                int recyclerViewHeight = recyclerView.getHeight();
                final double bottomDelta = recyclerViewHeight - viewHeight;
                //    Log.i("ScrollListener", "offset:" + Offset);
                //  Log.i("ScrollListener", "bottomDelta:" + bottomDelta);


                if (child.getScrollType() == ScrollType.SCROLL_WHOLE) {
                    float ratio = (Offset + viewHeight) / 1.0f / (recyclerViewHeight + viewHeight);

                    child.setPercent(ratio);
                } else if (child.getScrollType() == ScrollType.SCROLL_MIDDLE) {
                    float percent = (float) (Offset / bottomDelta);
                    //    Log.i("ScrollListener", "percent:" + percent);
                    if (Offset > bottomDelta) {
                        child.setPercent(1);
                    } else if (Offset <= 0) {
                        child.setPercent(0);
                    } else {
                        child.setPercent(percent);
                    }
                }
            }
        }
    }

}

package com.yel.image;
/**
 * Created by yel.huang on 2018/1/29.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class ScrollingImageView extends android.support.v7.widget.AppCompatImageView {
    private float yPercent = 0.5f;
    private ScrollType mScrollType;

    public enum ScrollType {
        /**
         * View从开始到结束，一直在滑动
         */
        SCROLL_WHOLE (0),

        /**
         * View只当在完整显示时才开始滚动
         */
        SCROLL_MIDDLE (1);

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
        setup();
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

        RecyclerView temp = getRecyclerView((ViewGroup) this.getParent());
        System.out.println("temp: " + temp);

        setup();
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
    private void setup() {
        // 设置ImageView的必须ScaleType
        setScaleType(ScaleType.MATRIX);
    }

    public void setScrollType(ScrollType scrollType) {
        if (mScrollType != scrollType) {
            mScrollType = scrollType;
            // todo 好像要做点什么。。
        }
    }

    public ScrollType getScrollType() {
        return mScrollType;
    }

    public void setyPercent(float yPercent) {
        this.yPercent = yPercent;
        requestLayout();
        // 由于在setFrame中的setImageMatrix中调用了invalidate。所以这里不必再调用了
        // invalidate();
    }

   private int getViewHeight() {
       return getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        if (getDrawable() == null)
            return super.setFrame(l, t, r, b);

        Matrix matrix = getImageMatrix();

        float scale;
        float dx = 0, dy = 0;

        int viewWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int viewHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        int drawableWidth = getDrawable().getIntrinsicWidth();
        int drawableHeight = getDrawable().getIntrinsicHeight();
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

        matrix.setScale(scale, scale);
        matrix.postTranslate(Math.round(dx), Math.round(dy));

        setImageMatrix(matrix);

        return super.setFrame(l, t, r, b);
    }

    public static class ScrollListener extends RecyclerView.OnScrollListener {
        int ydy = 0;
        private RecyclerView.LayoutManager layoutManager;
        private int id;
        private int index;
        public ScrollListener(RecyclerView.LayoutManager layoutManager, int id, int index) {
            this.layoutManager = layoutManager;
            this.id = id;
            this.index = index;
        }
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            ydy += dy;
            System.out.println(ydy);

            View item = layoutManager.findViewByPosition(index);
            if (item != null) {
                float Offset = item.getTop();
                ScrollingImageView image = item.findViewById(id);
                int viewHeight = image.getViewHeight();

                int recyclerViewHeight = recyclerView.getHeight();

//                System.out.println("Height: " + recyclerViewHeight);
//                System.out.println("Offset: " + Offset);

                final double bottomDelta = recyclerViewHeight - viewHeight;

                if (image.getScrollType() == ScrollType.SCROLL_WHOLE) {
                    float ratio = (Offset + viewHeight) / 1.0f / (recyclerViewHeight + viewHeight);
                    image.setyPercent(ratio);
                } else if (image.getScrollType() == ScrollType.SCROLL_MIDDLE) {
                    if (Offset > bottomDelta) {
                        image.setyPercent(1);
                    } else if (Offset <= 0) {
                        image.setyPercent(0);
                    } else {
                        image.setyPercent((float) (Offset / bottomDelta));
                    }
                }
            }
        }
    }

}

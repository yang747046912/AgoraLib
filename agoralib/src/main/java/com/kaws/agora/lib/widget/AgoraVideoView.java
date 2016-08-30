package com.kaws.agora.lib.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by yangcai on 16/8/29.
 */
public class AgoraVideoView extends ViewGroup {

    private boolean defaultStatus = true;

    public AgoraVideoView(Context context) {
        super(context);
    }

    public AgoraVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AgoraVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int rw = MeasureSpec.getSize(widthMeasureSpec);
        int rh = MeasureSpec.getSize(heightMeasureSpec);
        int childCount = getChildCount();
        if (childCount == 1) {
            int childSpecW = MeasureSpec.makeMeasureSpec(rw, MeasureSpec.EXACTLY);
            int childSpecH = MeasureSpec.makeMeasureSpec(rh, MeasureSpec.EXACTLY);
            getChildAt(0).measure(childSpecW, childSpecH);
        } else if (childCount == 2) {
            if (defaultStatus) {
                int childSpecW = MeasureSpec.makeMeasureSpec(rw, MeasureSpec.EXACTLY);
                int childSpecH = MeasureSpec.makeMeasureSpec(rh, MeasureSpec.EXACTLY);
                getChildAt(0).measure(childSpecW, childSpecH);
                childSpecW = MeasureSpec.makeMeasureSpec(rw / 4, MeasureSpec.EXACTLY);
                childSpecH = MeasureSpec.makeMeasureSpec(rh / 4, MeasureSpec.EXACTLY);
                getChildAt(1).measure(childSpecW, childSpecH);
            } else {
                int childSpecW = MeasureSpec.makeMeasureSpec(rw, MeasureSpec.EXACTLY);
                int childSpecH = MeasureSpec.makeMeasureSpec(rh, MeasureSpec.EXACTLY);
                getChildAt(1).measure(childSpecW, childSpecH);
                childSpecW = MeasureSpec.makeMeasureSpec(rw / 4, MeasureSpec.EXACTLY);
                childSpecH = MeasureSpec.makeMeasureSpec(rh / 4, MeasureSpec.EXACTLY);
                getChildAt(0).measure(childSpecW, childSpecH);
            }
        } else if (childCount == 3 || childCount == 4) {
            int childSpecW = MeasureSpec.makeMeasureSpec(rw / 2, MeasureSpec.EXACTLY);
            int childSpecH = MeasureSpec.makeMeasureSpec(rh / 2, MeasureSpec.EXACTLY);
            for (int i = 0; i < childCount; i++) {
                getChildAt(i).measure(childSpecW, childSpecH);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int childCount = getChildCount();
        if (childCount == 1) {
            View child = getChildAt(0);
            child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
        } else if (childCount == 2) {
            if (defaultStatus) {
                View child = getChildAt(0);
                child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
                child = getChildAt(1);
                int left = getMeasuredWidth() - child.getMeasuredWidth() - 20;
                child.layout(left, 20, child.getMeasuredWidth() + left, child.getMeasuredHeight() + 20);
            } else {

                View child = getChildAt(0);
                int left = getMeasuredWidth() - child.getMeasuredWidth() - 20;
                child.layout(left, 20, child.getMeasuredWidth() + left, child.getMeasuredHeight() + 20);
                child = getChildAt(1);
                child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
            }
        } else if (childCount == 3 || childCount == 4) {
            int childH = getChildAt(0).getMeasuredHeight();
            int childw = getChildAt(0).getMeasuredWidth();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                int j = i % 2;
                int lLetf = j * childw;
                int lRight = lLetf + childw;
                int hx = i / 2; //元素所在行数
                int lTop = hx * childH;
                int lBottom = lTop + childH;
                child.layout(lLetf, lTop, lRight, lBottom);
            }
        }
    }

    public void switchPosition() {
        defaultStatus = !defaultStatus;
        requestLayout();
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (!defaultStatus) {
            if (child == getChildAt(0)) {
                child = getChildAt(1);
            } else if (child == getChildAt(1)) {
                child = getChildAt(0);
            }
        }
        return super.drawChild(canvas, child, drawingTime);
    }


}

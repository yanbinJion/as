package com.wisdomrouter.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public class ViewPaperListView extends PullRefreshListView {
    private GestureDetector mGestureDetector;
    
    OnTouchListener mGestureListener;
    
    public ViewPaperListView(Context context) {
        super(context);
    }
    
	@SuppressWarnings("deprecation")
	public ViewPaperListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGestureDetector = new GestureDetector(new YScrollDetector());
        setFadingEdgeLength(0);
    }
    
    public ViewPaperListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
        
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev) && mGestureDetector.onTouchEvent(ev);
    }
    
    class YScrollDetector extends SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (distanceY != 0 && distanceX != 0) {
                
            }
            if (Math.abs(distanceY) >= Math.abs(distanceX)) {
                return true;
            }
            return false;
        }
    }
}

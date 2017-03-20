package com.wisdomrouter.app.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.ServiceItem;
import com.wisdomrouter.app.view.autoscrollviewpager.AutoScrollFactorScroller;
import com.wisdomrouter.app.view.autoscrollviewpager.AutoScrollPagerAdapter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ServiceScrollViewPager extends ViewPager {

    public interface OnPageClickListener {
        void onPageClick(ServiceScrollViewPager pager, int position);
    }

    private static final int MSG_AUTO_SCROLL = 0;
    private static final int DEFAULT_INTERNAL_IM_MILLIS = 2000;

    private PagerAdapter wrappedPagerAdapter;
    private PagerAdapter wrapperPagerAdapter;
    private InnerOnPageChangeListener listener;
    //滑动
    private AutoScrollFactorScroller scroller;
    private H handler;

    private boolean autoScroll = false;
    //自动轮播的时间
    private int intervalInMillis;

    private float mInitialMotionX;
    private float mInitialMotionY;
    private float mLastMotionX;
    private float mLastMotionY;
    private int touchSlop;
    private OnPageClickListener onPageClickListener;

    private List<BaseGridView> mGridViewList = null;

    private static final int DEFAULT_COLUMN_NUMBER = 2;
    private static final int DEFAULT_ROW_NUMBER = 3;

    private int mRowNumber = 0;
    private int mColumnNumber = 0;

    private BaseAdapter mAdapter;

    private View mEmptyView = null;

    private int mPaddingLeft = 0;
    private int mPaddingRight = 0;

    private int mSelection = -1;
    private Context context;

    public interface OnServicePageListenser {
        void click(int position);
    }

    private OnServicePageListenser gridItemClickListenser;

    public void setOnServicePageListenser(OnServicePageListenser gridItemClickListenser) {
        this.gridItemClickListenser = gridItemClickListenser;
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        mPaddingLeft = left;
        mPaddingRight = right;
        super.setPadding(0, top, 0, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight();
            if (h > height)
                height = h;
        }

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    public int getPageCount() {
        return mGridViewList.size();
    }

    public int getPageSize() {
        return mColumnNumber * mRowNumber;
    }

    public void setSelection(int position) {
        final int pageSize = getPageSize();
        if (mAdapter == null || pageSize <= 0) {
            mSelection = position;
            return;
        }
        mSelection = -1;
        setCurrentItem(position / pageSize, true);
    }

    public int getSelection() {
        return getCurrentItem() * getPageSize();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putInt("selection", getSelection());
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            this.mSelection = bundle.getInt("selection");
            state = bundle.getParcelable("instanceState");
        }
        super.onRestoreInstanceState(state);
    }

    public void setEmptyView(TextView emptyView) {
        mEmptyView = emptyView;
    }

    public void setPageAdapter(BaseAdapter adapter) {
        mAdapter = adapter;
        resetAdapter();
    }

    public void notifyDataSetChanged() {
        resetAdapter();
    }

    private void resetAdapter() {
        int pageSize = mColumnNumber * mRowNumber;
        if (pageSize <= 0)
            return;

        if (mAdapter.getCount() == 0) {
            mGridViewList.removeAll(mGridViewList);
            if (mEmptyView != null)
                mEmptyView.setVisibility(View.VISIBLE);
        } else {
            if (mEmptyView != null)
                mEmptyView.setVisibility(View.GONE);
        }
        int pageCount = mAdapter.getCount() / pageSize;
        if (mAdapter.getCount() % pageSize == 0) {
            pageCount--;
        }
        int listSize = mGridViewList.size() - 1;
        BaseGridView gridview;
        GridAdapter gridAdapter;
        for (int i = 0; i <= Math.max(listSize, pageCount); i++) {
            if (i <= listSize && i <= pageCount) {
                gridview = mGridViewList.get(i);
                gridAdapter = new GridAdapter(i, pageSize, mAdapter);
                gridview.setAdapter(gridAdapter);
                mGridViewList.set(i, gridview);
                continue;
            }
            if (i > listSize && i <= pageCount) {
                gridview = new BaseGridView(context);
//                View view = LayoutInflater.from(context).inflate(R.layout.find_server_view, null);
//                gridview = (BaseGridView) view
//                            .findViewById(R.id.gv_server);
                gridview.setPadding(10, 0, 10, 0);
//                ViewGroup.LayoutParams params = gridview.getLayoutParams();

                gridview.setNumColumns(4);
                gridAdapter = new GridAdapter(i, pageSize, mAdapter);
                final GridAdapter finalGridAdapter = gridAdapter;
                gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ServiceItem item = (ServiceItem) finalGridAdapter.getItem(position);
//                        WarnUtils.toast(context,item.getName());
                        openBrowser(item.getUrl());
                    }
                });
                gridview.setAdapter(gridAdapter);
                mGridViewList.add(gridview);
                continue;
            }
            if (i > pageCount && i <= listSize) {
                mGridViewList.remove(pageCount + 1);
                continue;
            }
        }
        super.setAdapter(new GridPagerAdapter());
        if (mSelection >= 0)
            setSelection(mSelection);
    }

    private void openBrowser(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    private class GridPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mGridViewList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mGridViewList.get(position), new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            return mGridViewList.get(position);
        }
    }

    private class GridAdapter extends BaseAdapter {
        int mPage;
        int mSize;
        BaseAdapter mAdapter;

        public GridAdapter(int page, int size, BaseAdapter adapter) {
            mPage = page;
            mSize = size;
            mAdapter = adapter;
        }

        @Override
        public int getCount() {
            if (mAdapter.getCount() % mSize == 0)
                return mSize;
            else if (mPage < mAdapter.getCount() / mSize) {
                return mSize;
            } else {
                return mAdapter.getCount() % mSize;
            }
        }

        @Override
        public Object getItem(int position) {
            return mAdapter.getItem(mPage * mSize + position);
        }

        @Override
        public long getItemId(int position) {
            return mAdapter.getItemId(mPage * mSize + position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return mAdapter.getView(mPage * mSize + position, convertView, parent);
        }

    }

    private class H extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AUTO_SCROLL:
                    setCurrentItem(getCurrentItem() + 1);
                    sendEmptyMessageDelayed(MSG_AUTO_SCROLL, intervalInMillis);
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    public ServiceScrollViewPager(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public ServiceScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.GridViewPager);
            final int N = a.getIndexCount();
            for (int i = 0; i < N; i++) {
                int attr = a.getIndex(i);
                if (attr == R.styleable.GridViewPager_gvpColumnNumber) {
                    mColumnNumber = a.getInt(attr, -1);
                } else if (attr == R.styleable.GridViewPager_gvpRowNumber) {
                    mRowNumber = a.getInt(attr, -1);
                } else if (attr == R.styleable.GridViewPager_android_padding) {
                    int padding = a.getDimensionPixelSize(attr, 0);
                    setPadding(padding, padding, padding, padding);
                } else if (attr == R.styleable.GridViewPager_android_paddingLeft) {
                    mPaddingLeft = a.getDimensionPixelSize(attr, 0);
                } else if (attr == R.styleable.GridViewPager_android_paddingRight) {
                    mPaddingRight = a.getDimensionPixelSize(attr, 0);
                }
            }

            a.recycle();
        }
        init();
    }

    private void init() {

        mGridViewList = new ArrayList<>();
        listener = new InnerOnPageChangeListener();
        super.setOnPageChangeListener(listener);

        handler = new H();
        //getScaledTouchSlop是一个距离，表示滑动的时候，手的移动要大于这个距离才开始移动控件。如果小于这个距离就不触发移动控件
        touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//
//        int height = 0;
//        for (int i = 0; i < getChildCount(); i++) {
//            View child = getChildAt(i);
//            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
//            int h = child.getMeasuredHeight();
//            if (h > height)
//                height = h;
//        }
//
//        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
//
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //移除发送的轮播的消息
        handler.removeMessages(MSG_AUTO_SCROLL);
    }

    public void startAutoScroll() {
        startAutoScroll(intervalInMillis != 0 ? intervalInMillis : DEFAULT_INTERNAL_IM_MILLIS, true);
    }

    //开启自动轮播
    public void startAutoScroll(int intervalInMillis, boolean autoScroll) {
        if (autoScroll) {
            if (getCount() > 1) {
                this.intervalInMillis = intervalInMillis;
                this.autoScroll = autoScroll;
                handler.removeMessages(MSG_AUTO_SCROLL);
                handler.sendEmptyMessageDelayed(MSG_AUTO_SCROLL, intervalInMillis);
            }
        }
    }

    //开启自动轮播
    public void startAutoScroll(int intervalInMillis) {
        if (intervalInMillis > 0) {
            startAutoScroll(intervalInMillis, true);
        }
    }

    public void stopAutoScroll() {
        autoScroll = false;
        handler.removeMessages(MSG_AUTO_SCROLL);
    }

    public void setInterval(int intervalInMillis) {
        this.intervalInMillis = intervalInMillis;
    }

    public void setScrollFactgor(double factor) {
        setScrollerIfNeeded();
        scroller.setFactor(factor);
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.listener.setOnPageChangeListener(listener);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        wrappedPagerAdapter = adapter;
        wrapperPagerAdapter = (wrappedPagerAdapter == null) ? null : new AutoScrollPagerAdapter(adapter);
        super.setAdapter(wrapperPagerAdapter);

        if (adapter != null && adapter.getCount() != 0) {
            post(new Runnable() {
                @Override
                public void run() {
                    setCurrentItem(0, false);
                }
            });
        }
    }

    @Override
    public PagerAdapter getAdapter() {
        // In order to be compatible with ViewPagerIndicator
        return wrappedPagerAdapter;
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item + 1);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item + 1, smoothScroll);
    }

    @Override
    public int getCurrentItem() {
        int curr = super.getCurrentItem();
        if (wrappedPagerAdapter != null && wrappedPagerAdapter.getCount() > 1) {
            if (curr == 0) {
                curr = wrappedPagerAdapter.getCount() - 1;
            } else if (curr == wrapperPagerAdapter.getCount() - 1) {
                curr = 0;
            } else {
                curr = curr - 1;
            }
        }
        return curr;
    }

    public OnPageClickListener getOnPageClickListener() {
        return onPageClickListener;
    }

    public void setOnPageClickListener(OnPageClickListener onPageClickListener) {
        this.onPageClickListener = onPageClickListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (MotionEventCompat.getActionMasked(ev)) {
            case MotionEvent.ACTION_DOWN:
                if (getCurrentItemOfWrapper() + 1 == getCountOfWrapper()) {
                    setCurrentItem(0, false);
                } else if (getCurrentItemOfWrapper() == 0) {
                    setCurrentItem(getCount() - 1, false);
                }
                //handler.removeMessages(MSG_AUTO_SCROLL);
                mInitialMotionX = ev.getX();
                mInitialMotionY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                mLastMotionX = ev.getX();
                mLastMotionY = ev.getY();
                if ((int) Math.abs(mLastMotionX - mInitialMotionX) > touchSlop || (int) Math.abs(mLastMotionY - mInitialMotionY) > touchSlop) {
                    mInitialMotionX = 0.0f;
                    mInitialMotionY = 0.0f;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (autoScroll) {
                    startAutoScroll();
                }

                // Manually swipe not affected by scroll factor.
                if (scroller != null) {
                    final double lastFactor = scroller.getFactor();
                    scroller.setFactor(1);
                    post(new Runnable() {
                        @Override
                        public void run() {
                            scroller.setFactor(lastFactor);
                        }
                    });
                }

                mLastMotionX = ev.getX();
                mLastMotionY = ev.getY();
                if ((int) mInitialMotionX != 0 && (int) mInitialMotionY != 0) {
                    if ((int) Math.abs(mLastMotionX - mInitialMotionX) < touchSlop
                            && (int) Math.abs(mLastMotionY - mInitialMotionY) < touchSlop) {
                        mInitialMotionX = 0.0f;
                        mInitialMotionY = 0.0f;
                        mLastMotionX = 0.0f;
                        mLastMotionY = 0.0f;
                        if (onPageClickListener != null) {
                            onPageClickListener.onPageClick(this, getCurrentItem());
                        }
                    }
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * Get current item of the outer wrapper adapter.
     */
    private int getCurrentItemOfWrapper() {
        return super.getCurrentItem();
    }

    /**
     * Get item count of the outer wrapper adapter.
     */
    private int getCountOfWrapper() {
        if (wrapperPagerAdapter != null) {
            return wrapperPagerAdapter.getCount();
        }
        return 0;
    }

    /**
     * Get item count of the adapter which is set by user
     */
    private int getCount() {
        if (wrappedPagerAdapter != null) {
            return wrappedPagerAdapter.getCount();
        }
        return 0;
    }

    private void setScrollerIfNeeded() {
        if (scroller != null) {
            return;
        }
        try {
            Field scrollerField = ViewPager.class.getDeclaredField("mScroller");
            scrollerField.setAccessible(true);
            Field interpolatorField = ViewPager.class.getDeclaredField("sInterpolator");
            interpolatorField.setAccessible(true);
            scroller = new AutoScrollFactorScroller(getContext(), (Interpolator) interpolatorField.get(null));
            scrollerField.set(this, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class InnerOnPageChangeListener implements OnPageChangeListener {
        private OnPageChangeListener listener;
        private int lastSelectedPage = -1;

        public InnerOnPageChangeListener() {
        }

        public InnerOnPageChangeListener(OnPageChangeListener listener) {
            setOnPageChangeListener(listener);
        }

        public void setOnPageChangeListener(OnPageChangeListener listener) {
            this.listener = listener;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == SCROLL_STATE_IDLE && getCount() > 1) {
                if (getCurrentItemOfWrapper() == 0) {
                    // scroll to the last page
                    setCurrentItem(getCount() - 1, false);
                } else if (getCurrentItemOfWrapper() == getCountOfWrapper() - 1) {
                    // scroll to the first page
                    setCurrentItem(0, false);
                }
            }
            if (listener != null) {
                listener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (listener != null && position > 0 && position < getCount()) {
                listener.onPageScrolled(position - 1, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageSelected(final int position) {
            if (gridItemClickListenser != null) {
                gridItemClickListenser.click(position);
            }
//            if (listener != null && position != 0 && position < wrappedPagerAdapter.getCount() + 1) {
            if (listener != null) {
                final int pos;
                // Fix position
                if (position == 0) {
                    pos = getCount() - 1;
                } else if (position == getCountOfWrapper() - 1) {
                    pos = 0;
                } else {
                    pos = position - 1;
                }

                // Comment this, onPageSelected will be triggered twice for position 0 and getCount -1.
                // Uncomment this, PageIndicator will have trouble.
//                if (lastSelectedPage != pos) {
                lastSelectedPage = pos;
                // Post a Runnable in order to be compatible with ViewPagerIndicator because
                // onPageSelected is invoked before onPageScrollStateChanged.
                ServiceScrollViewPager.this.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onPageSelected(pos);
                    }
                });
//                }
            }
        }
    }

}

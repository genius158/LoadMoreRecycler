package com.yan.loadmorerecycler;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hawk.funs.beta.R;

/**
 * Created by yan on 2017/5/31.
 */

public class MoreRecycler extends LoadMoreRecycler {
    View loadMore;
    TextView tvLoadMore;
    private boolean isVerticleScrollingEnabled = true;
    private OnLoadMoreListener onLoadMoreListener;

    @Override
    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public MoreRecycler(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        loadMore = LayoutInflater.from(getContext()).inflate(R.layout.view_load_more, null);
        loadMore.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.WRAP_CONTENT));
        tvLoadMore = (TextView) loadMore.findViewById(R.id.tv_load_more);
        setLoadMoreView(loadMore);
        setCurrentLoadMoreTrigger(LOAD_MORE_TRIGGER_CENTER);
        super.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoading() {
                if (onLoadMoreListener != null) {
                    if (tvLoadMore != null) {
                        tvLoadMore.setText("正在加载...");
                    }
                    onLoadMoreListener.onLoading();
                }
            }
        });
    }

    public void noMore() {
        tvLoadMore.setText("没有更多了");
    }

    public MoreRecycler(Context context) {
        super(context, null);
    }

    private DispatchTouchEvent dispatchTouchEvent;

    public void setDispatchTouchEvent(DispatchTouchEvent dispatchTouchEvent) {
        this.dispatchTouchEvent = dispatchTouchEvent;
    }

    public interface DispatchTouchEvent {
        void dispatchTouchEvent(MotionEvent ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getX() < paddingLeft && dispatchTouchEvent != null && getScrollState() != 0) {
            dispatchTouchEvent.dispatchTouchEvent(ev);
            isVerticleScrollingEnabled = false;
        } else {
            isVerticleScrollingEnabled = true;
        }
        return super.dispatchTouchEvent(ev);
    }

    public void hide() {
        super.loadMoreComplete(0);
    }

    public void hide(int dismissDuring) {
        super.loadMoreComplete(dismissDuring);
    }

    @Override
    public void loadMoreComplete(int dismissDuring) {
        loadMoreComplete();
    }

    private int paddingLeft = 0;

    public void setPaddingLeft(int dp) {
        paddingLeft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp
                , getContext().getResources().getDisplayMetrics());
        loadMore.setPadding(paddingLeft, 0, 0, 0);
    }


    @Override
    public int computeVerticalScrollRange() {
        if (isVerticleScrollingEnabled)
            return super.computeVerticalScrollRange();
        return 0;
    }

    @Override
    public void resetLoadMore() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                MoreRecycler.super.resetLoadMore();
                MoreRecycler.super.doLoadMore();
            }
        }, 150);
    }
}

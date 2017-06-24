package com.yan.loadmorerecycler;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by yan on 2017/5/26.
 */

public class LoadMoreRecycler extends RecyclerView {
    private static final String TAG = "MoreRecycler";
    public static int LOAD_MORE_TRIGGER_START = 1;
    public static int LOAD_MORE_TRIGGER_CENTER = 2;
    public static int LOAD_MORE_TRIGGER_END = 3;
    private int currentLoadMoreTrigger = LOAD_MORE_TRIGGER_START;

    private LoadWrapper loadMoreAdapter;
    private OnLoadMoreListener onLoadMoreListener;
    private volatile boolean isLoadMoreComplete;
    private volatile boolean isLoading;

    private boolean isLoadMoreDissmiss;

    private View loadMoreView;
    private Adapter dataAdapter;

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setLoadMoreView(View LoadMore) {
        this.loadMoreView = LoadMore;
        setLoadMoreAdapter();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        registerObserver();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unRegisterObserver();
    }

    public View getLoadMoreView() {
        return loadMoreView;
    }

    private void setLoadMoreAdapter() {
        if (loadMoreView != null && dataAdapter != null) {
            loadMoreAdapter = new LoadWrapper(dataAdapter);
            loadMoreAdapter.setLoadMoreView(loadMoreView);
            super.setAdapter(loadMoreAdapter);
            if (isLoadMoreDissmiss) {
                loadMoreAdapter.setLoadViewVisible(false);
            }
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        dataAdapter = adapter;
        registerObserver();
        setLoadMoreAdapter();
    }

    private void registerObserver() {
        if (dataAdapter != null) {
            try {
                dataAdapter.registerAdapterDataObserver(adapterDataObserver);
            } catch (Exception e) {
                Log.d(TAG, "registerObserver: " + e.getMessage());
            }
        }
    }

    private void unRegisterObserver() {
        try {
            dataAdapter.unregisterAdapterDataObserver(adapterDataObserver);
        } catch (Exception e) {
            Log.d(TAG, "registerObserver: " + e.getMessage());
        }
    }

    private void initMoreRecycler(Context context) {
        addOnScrollListener(onScrollListener);
    }

    public void resetLoadMore() {
        loadMoreAdapter.setLoadViewVisible(true);
        isLoadMoreComplete = false;
    }

    public LoadWrapper getLoadMoreAdapter() {
        return loadMoreAdapter;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public boolean isLoadMoreComplete() {
        return isLoadMoreComplete;
    }

    public void loadMoreComplete() {
        isLoadMoreComplete = true;
        isLoading = false;
    }

    public void loadMoreComplete(int dismissDuring) {
        loadMoreComplete();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                isLoadMoreDissmiss = true;
                loadMoreAdapter.setLoadViewVisible(false);
            }
        }, dismissDuring);
    }

    public void doLoadMore() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (loadMoreView != null && needRefreshData() && !isLoadMoreComplete && !isLoading) {
                    isLoading = true;
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoading();
                    }
                }
            }
        }, 150);
    }

    private boolean needRefreshData() {
        int[] loadPositions = new int[2];
        int[] selfPositions = new int[2];
        getLocationInWindow(selfPositions);
        loadMoreView.getLocationInWindow(loadPositions);
        if (loadPositions[1] > selfPositions[1] && loadPositions[1] < selfPositions[1] + getHeight()) {
            return true;
        }
        return false;
    }

    /**
     * load more logic
     */
    private OnScrollListener onScrollListener = new OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int loadTriggerY = recyclerView.getHeight() - loadMoreView.getHeight() + 1;

            if (currentLoadMoreTrigger == LOAD_MORE_TRIGGER_START) {
                loadTriggerY = recyclerView.getHeight() - 1;
            } else if (currentLoadMoreTrigger == LOAD_MORE_TRIGGER_CENTER) {
                loadTriggerY = recyclerView.getHeight() - loadMoreView.getHeight() / 2;
            }
            View tempLoadMore = recyclerView.findChildViewUnder(recyclerView.getWidth() / 2, loadTriggerY);
            if (!isLoadMoreComplete && !isLoading
                    && (tempLoadMore == loadMoreView || tempLoadMore == null)) {
                isLoading = true;
                if (onLoadMoreListener != null) {
                    onLoadMoreListener.onLoading();
                }
            }
        }
    };


    public void setCurrentLoadMoreTrigger(int currentLoadMoreTrigger) {
        this.currentLoadMoreTrigger = currentLoadMoreTrigger;
    }

    public interface OnLoadMoreListener {
        void onLoading();
    }

    public void notifyItemChanged(int position) {
        loadMoreAdapter.notifyItemChanged(position);
        isLoading = false;
    }

    public void notifyItemInserted(int position) {
        loadMoreAdapter.notifyItemInserted(position);
        isLoading = false;
    }

    public void notifyItemRemoved(int position) {
        loadMoreAdapter.notifyItemRemoved(position);
        isLoading = false;
    }

    public void notifyItemRangeChanged(int position, int range) {
        loadMoreAdapter.notifyItemRangeChanged(position, range);
        isLoading = false;
    }

    public void notifyItemRangeInserted(int position, int range) {
        loadMoreAdapter.notifyItemRangeInserted(position, range);
        isLoading = false;
    }

    public void notifyItemRangeRemoved(int position, int range) {
        loadMoreAdapter.notifyItemRangeRemoved(position, range);
        isLoading = false;
    }

    public void notifyDataSetChanged() {
        loadMoreAdapter.notifyDataSetChanged();
        isLoading = false;
    }

    private AdapterDataObserver adapterDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            notifyDataSetChanged();
            doLoadMore();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            if (itemCount == 1) {
                notifyItemChanged(positionStart);
                return;
            }
            notifyItemRangeChanged(positionStart, itemCount);
            doLoadMore();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            super.onItemRangeChanged(positionStart, itemCount, payload);
            doLoadMore();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            if (itemCount == 1) {
                notifyItemInserted(positionStart);
                doLoadMore();
                return;
            }
            notifyItemRangeInserted(positionStart, itemCount);
            doLoadMore();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            if (itemCount == 1) {
                notifyItemRemoved(positionStart);
                doLoadMore();
                return;
            }
            notifyItemRangeRemoved(positionStart, itemCount);
            doLoadMore();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            isLoading = false;
            doLoadMore();
        }
    };

    public LoadMoreRecycler(Context context) {
        super(context);
        initMoreRecycler(context);
    }

    public LoadMoreRecycler(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initMoreRecycler(context);
    }

    public LoadMoreRecycler(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initMoreRecycler(context);
    }
}

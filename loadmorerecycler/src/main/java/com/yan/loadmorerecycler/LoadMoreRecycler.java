package com.yan.loadmorerecycler;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by yan on 2017/5/26.
 */

public class LoadMoreRecycler extends RecyclerView {
    private static final String TAG = "MoreRecycler";

    private LoadWrapper loadMoreAdapter;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean isLoadMoreComplete;
    private boolean isLoading;

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
        if (dataAdapter != null) {
            dataAdapter.registerAdapterDataObserver(adapterDataObserver);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        dataAdapter.unregisterAdapterDataObserver(adapterDataObserver);
        super.onDetachedFromWindow();
    }

    public View getLoadMoreView() {
        return loadMoreView;
    }

    private void setLoadMoreAdapter() {
        if (loadMoreView != null && dataAdapter != null) {
            loadMoreAdapter = new LoadWrapper(dataAdapter);
            loadMoreAdapter.setLoadMoreView(loadMoreView);
            super.setAdapter(loadMoreAdapter);
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        dataAdapter = adapter;
        dataAdapter.registerAdapterDataObserver(adapterDataObserver);
        setLoadMoreAdapter();
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
    }

    public void loadMoreComplete(int dismissDuring) {
        isLoadMoreComplete = true;
        postDelayed(new Runnable() {
            @Override
            public void run() {
                loadMoreAdapter.setLoadViewVisible(false);
            }
        }, dismissDuring);
    }

    /**
     * load more logic
     */
    private OnScrollListener onScrollListener = new OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (!canChildScrollDown() && !isLoadMoreComplete && !isLoading) {
                isLoading = true;
                if (onLoadMoreListener != null) {
                    onLoadMoreListener.onLoading();
                }
            }
        }
    };

    /**
     * @return Whether it is possible for the child view of this layout to
     * scroll down. Override this if the child view is a custom view.
     */
    private boolean canChildScrollDown() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            return ViewCompat.canScrollVertically(this, 1) || this.getScrollY() > 0;
        } else {
            return ViewCompat.canScrollVertically(this, 1);
        }
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
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            if (itemCount == 1) {
                notifyItemChanged(positionStart);
                return;
            }
            notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            super.onItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            if (itemCount == 1) {
                notifyItemInserted(positionStart);
                return;
            }
            notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            if (itemCount == 1) {
                notifyItemRemoved(positionStart);
                return;
            }
            notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            isLoading = false;
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

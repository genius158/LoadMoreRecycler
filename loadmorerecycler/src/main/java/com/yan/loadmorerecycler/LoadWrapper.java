package com.yan.loadmorerecycler;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by yan on 2017/5/26.
 */

class LoadWrapper extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM_TYPE_LOAD_MORE = Integer.MAX_VALUE - 2;

    private RecyclerView.Adapter innerAdapter;
    private View loadMoreView;

    private boolean loadViewVisible = true;

    LoadWrapper(RecyclerView.Adapter adapter) {
        innerAdapter = adapter;
    }

    private boolean hasLoadMore() {
        return loadMoreView != null;
    }

    private boolean isShowLoadMore(int position) {
        return hasLoadMore() && (position >= innerAdapter.getItemCount());
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowLoadMore(position)) {
            return ITEM_TYPE_LOAD_MORE;
        }
        return innerAdapter.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_LOAD_MORE) {
            RecyclerView.ViewHolder holder = null;
            if (loadMoreView != null) {
                holder = new RecyclerView.ViewHolder(loadMoreView) {
                };
            }
            return holder;
        }
        return innerAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (hasLoadMore() && loadViewVisible) {
            if (position == innerAdapter.getItemCount()) {
                return;
            }
        }
        innerAdapter.onBindViewHolder(holder, position);
    }

    void setLoadViewVisible(boolean loadViewVisible) {
        if (loadViewVisible == this.loadViewVisible) {
            return;
        }
        if (hasLoadMore()) {
            this.loadViewVisible = loadViewVisible;
            if (this.loadViewVisible) {
                notifyItemInserted(getItemCount());
            } else {
                notifyItemRemoved(innerAdapter.getItemCount());
            }
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        WrapperUtils.onAttachedToRecyclerView(innerAdapter, recyclerView, new WrapperUtils.SpanSizeCallback() {
            @Override
            public int getSpanSize(GridLayoutManager layoutManager, GridLayoutManager.SpanSizeLookup oldLookup, int position) {
                if (isShowLoadMore(position)) {
                    return layoutManager.getSpanCount();
                }
                if (oldLookup != null) {
                    return oldLookup.getSpanSize(position);
                }
                return 1;
            }
        });
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        innerAdapter.onViewAttachedToWindow(holder);

        if (isShowLoadMore(holder.getLayoutPosition())) {
            setFullSpan(holder);
        }
    }

    private void setFullSpan(RecyclerView.ViewHolder holder) {
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
            p.setFullSpan(true);
        }
    }

    @Override
    public int getItemCount() {
        return innerAdapter.getItemCount() + (hasLoadMore() && loadViewVisible ? 1 : 0);
    }

    LoadWrapper setLoadMoreView(View loadMoreView) {
        this.loadMoreView = loadMoreView;
        return this;
    }
}

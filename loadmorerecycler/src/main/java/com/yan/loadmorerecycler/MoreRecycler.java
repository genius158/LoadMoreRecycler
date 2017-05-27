package com.hawk.funs.beta.widget.morerecycler;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hawk.funs.beta.R;

import java.lang.ref.WeakReference;

/**
 * Created by yan on 2017/5/26.
 */

public class MoreRecycler extends RecyclerView {
    private static final String TAG = "MoreRecycler";

    private LoadWrapper loadMoreAdapter;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean loadMoreComplete;
    private boolean isLoading;
    private MoreHandler handler;

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public void setAdapter(Adapter adapter ) {
        loadMoreAdapter = new LoadWrapper(adapter);
        View loadMore = LayoutInflater.from(getContext()).inflate(R.layout.view_load_more, null);
        loadMore.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.WRAP_CONTENT));
        loadMoreAdapter.setLoadMoreView(loadMore);
        super.setAdapter(loadMoreAdapter);
    }

    public void setAdapter(Adapter adapter,Adapter scoreAdapter) {
        loadMoreAdapter = new LoadWrapper(scoreAdapter);
        View loadMore = LayoutInflater.from(getContext()).inflate(R.layout.view_load_more, null);
        loadMore.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.WRAP_CONTENT));
        loadMoreAdapter.setLoadMoreView(loadMore);
        super.setAdapter(adapter);
    }

    private void initMoreRecycler(Context context) {
        addOnScrollListener(onScrollListener);
        checkLoadMoreAble();
    }

    public void resetLoadMore() {
        if (handler == null) {
            loadMoreAdapter.clearLoadMore(false);
            loadMoreComplete = false;
            return;
        }
        checkLoadMoreAble();

    }

    public LoadWrapper getLoadAdapter() {
        return loadMoreAdapter;
    }

    public void loadingComplete() {
        loadMoreComplete = true;
        loadMoreAdapter.clearLoadMore(true);
    }

    /**
     * load more logic
     */
    private OnScrollListener onScrollListener = new OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (!canChildScrollDown() && !loadMoreComplete && !isLoading) {
                isLoading = true;
                if (onLoadMoreListener != null) {
                    onLoadMoreListener.onLoading();
                }
            }
        }
    };

    private void checkLoadMoreAble() {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(MoreHandler.MESSAGE_CHECK, 150);
        }
    }

    /**
     * check the recycler can load more
     *
     * @return
     */
    private boolean canLoadMore() {
        if (getRvLastItemToTop() > this.getMeasuredHeight()) {
            loadMoreAdapter.clearLoadMore(false);
            loadMoreComplete = false;
            return true;
        }
        loadMoreAdapter.clearLoadMore(true);
        loadMoreComplete = true;
        return false;
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        if (this.getLayoutManager() instanceof LinearLayoutManager) {
            handler = new MoreHandler(this);
        }
        super.setLayoutManager(layout);
    }

    /**
     * get the last item distance form the top
     * the method just can use for LinearLayoutManager
     *
     * @return
     */
    private int getRvLastItemToTop() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) this.getLayoutManager();
        View view = this.getChildAt(layoutManager.findLastVisibleItemPosition());
        if (view != null) {
            return view.getTop() + view.getMeasuredHeight();

        }
        return 0;
    }

    /**
     * @return Whether it is possible for the child view of this layout to
     * scroll down. Override this if the child view is a custom view.
     */
    public boolean canChildScrollDown() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            return ViewCompat.canScrollVertically(this, 1) || this.getScrollY() > 0;
        } else {
            return ViewCompat.canScrollVertically(this, 1);
        }
    }

    public interface OnLoadMoreListener {
        void onLoading();
    }

    /**
     * the handler is for checking loadMore able
     */
    private static class MoreHandler extends Handler {
        static final int MESSAGE_CHECK = 1;
        private WeakReference<MoreRecycler> innerObject;

        MoreHandler(MoreRecycler context) {
            this.innerObject = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            if (MESSAGE_CHECK == msg.what) {
                MoreRecycler moreRecycler = innerObject.get();
                if (moreRecycler == null) {
                    return;
                }
                moreRecycler.canLoadMore();
                return;
            }
            super.handleMessage(msg);
        }
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

    public void loadEnd(){
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

    public void notifyDataSetChanged( ) {
        loadMoreAdapter.notifyDataSetChanged();
        isLoading = false;
    }

    public MoreRecycler(Context context) {
        super(context);
        initMoreRecycler(context);
    }

    public MoreRecycler(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initMoreRecycler(context);
    }

    public MoreRecycler(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initMoreRecycler(context);
    }

}

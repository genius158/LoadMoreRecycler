# LoadMoreRecycler

## 1.概述
recycler view with load more , recyclerView 实现加载更多的小栗子（ps: demo 是用的kotlin）
<br/>
<br/>
在实际开发中，如果你用到了recyclerView 而且需要加载更多
，绝大多数的情况下上拉加载都是放在recyclerView 中的，
使得加载的数据可以从加载更多布局的位置开始更新数据，而刷新一般是通过刷新组件来实现，
统一刷新风格，
如果在适配其中实现上拉加载的功能，在没有滑出加载更多的布局的时候，就已经触发了加载更多的动作
，这个是我个人很不喜欢的效果
所以根据这些情况，避免功能重复、不必要，在这里只实现了上拉加载
（ps：recyclerView 下拉刷新，通常做法，通过设置头部，根据滑动设置头部高度来实现）

## 2.说明  
```
//-控件设置-
    // 设置加载更多的监听
    moreRecycler.setOnLoadMoreListener(new MoreRecycler.OnLoadMoreListener...
    moreRecycler.loadingComplete(isHideLoad) //isHideLoad 是否取消加载更多的显示
    moreRecycler.resetLoadMore() // 重置加载更多
    moreRecycler.setLoadMoreView(view) // 设置加载更多的布局
    moreRecycler.getLoadView() // 得到加载更多布局
    loadMoreAdapter= moreRecycler.getLoadAdapter() // 得到装饰过的加载更多适配器

    // 数据更新可以用moreRecycler来实现
    moreRecycler.notifyItemChanged(int)
    moreRecycler.notifyItemInserted(int)
    moreRecycler.notifyItemRemoved(int)
    moreRecycler.notifyItemRangeChanged(int, int)
    moreRecycler.notifyItemRangeInserted(int, int)
    moreRecycler.notifyItemRangeRemoved(int, int)
    //更新数据也可以用
    loadMoreAdapter.notifyItemChanged(int)
    moreRecycler.loadEnd()// 更改正在加载标志位为加载结束
    ...
    
```

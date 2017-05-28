package com.yan.loadmorerecyclertest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_data_main.view.*
import kotlinx.android.synthetic.main.view_load.view.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var dataList: ArrayList<String>
    private var index: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    private fun init() {
        dataList = ArrayList<String>()
        dataInit()
        initSWL()
        initMR()
    }

    private fun initSWL() {
        srlRefresh.setOnRefreshListener({
            dataInit()
            mrData.notifyDataSetChanged()
            mrData.resetLoadMore()
            mrData.loadMoreView.tvLoad.text = getString(R.string.loading)
            srlRefresh.setRefreshing(false)
        })
    }

    private fun dataInit() {
        index = 0
        dataList.clear()
        for (i in 0 until 8) {
            dataList.add(index++.toString())
        }
    }

    private fun initMR() {
        mrData.layoutManager = LinearLayoutManager(baseContext)
        mrData.adapter = getAdapter()

        val loadMoreView = layoutInflater.inflate(R.layout.view_load, null)
        loadMoreView.layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT
                , RecyclerView.LayoutParams.WRAP_CONTENT)

        mrData.setLoadMoreView(loadMoreView)
        mrData.setOnLoadMoreListener({
            mrData.postDelayed({
                if (dataList.size > 32) {
                    mrData.loadMoreView.tvLoad.text = getString(R.string.no_more)
                    mrData.loadMoreComplete(true)
                } else {
                    val tempSize = dataList.size
                    for (i in 0 until 8) {
                        dataList.add(index++.toString())
                    }
                    mrData.notifyItemRangeInserted(tempSize, dataList.size - tempSize)
                }
            }, 2000)
        })
    }

    private fun getAdapter(): RecyclerView.Adapter<Holder> {
        return object : RecyclerView.Adapter<Holder>() {
            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): Holder
                    = Holder(layoutInflater.inflate(R.layout.item_data_main, parent, false))

            override fun getItemCount(): Int = dataList.size

            override fun onBindViewHolder(holder: Holder, position: Int) {
                holder.itemView.tvData.text = dataList[position]
            }
        }
    }

    private class Holder(item: View) : RecyclerView.ViewHolder(item)

}

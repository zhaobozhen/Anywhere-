package com.absinthe.anywhere_.adapter

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import timber.log.Timber

class ItemTouchCallBack : ItemTouchHelper.Callback() {

    private var mListener: OnItemTouchListener? = null

    fun setOnItemTouchListener(onItemTouchListener: OnItemTouchListener) {
        mListener = onItemTouchListener
    }

    /**
     * 根据 RecyclerView 不同的布局管理器，设置不同的滑动、拖动方向
     * 该方法使用 makeMovementFlags(int dragFlags, int swipeFlags) 方法返回
     * 参数: dragFlags:拖动的方向
     * swipeFlags:滑动的方向
     *
     * @param recyclerView recyclerView
     * @param viewHolder   viewHolder
     * @return int
     */
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        Timber.i("getMovementFlags")
        return if (recyclerView.layoutManager is GridLayoutManager ||
                recyclerView.layoutManager is StaggeredGridLayoutManager) {
            //此处不需要进行滑动操作，可设置为除4和8之外的整数，这里设为0
            //不支持滑动
            makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN or
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, 0)
        } else {
            //如果是LinearLayoutManager则只能向上向下滑动,不支持滑动
            makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0)
        }
    }

    /**
     * 当 ItemTouchHelper 拖动一个Item时该方法将会被回调，Item将从旧的位置移动到新的位置
     * 如果不拖动这个方法将从来不会调用,返回true表示已经被移动到新的位置
     *
     * @param recyclerView recyclerView
     * @param viewHolder   viewHolder
     * @param target       target
     * @return boolean
     */
    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        Timber.i("onMove")
        val fromPosition = viewHolder.adapterPosition
        val toPosition = target.adapterPosition
        mListener!!.onMove(fromPosition, toPosition)
        return true
    }

    /**
     * 当Item被滑动的时候被调用
     * 如果你不滑动这个方法将不会被调用
     *
     * @param viewHolder viewHolder
     * @param direction  direction
     */
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        Timber.i("onSwiped")
        //此处是侧滑删除的主要代码
        val position = viewHolder.adapterPosition
        mListener!!.onSwiped(position)
    }

    /**
     * 当Item被滑动、拖动的时候被调用
     *
     * @param viewHolder  viewHolder
     * @param actionState actionState
     */
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        Timber.i("onSelectedChanged")
        //...
        super.onSelectedChanged(viewHolder, actionState)
    }

    /**
     * 当与用户交互结束或相关动画完成之后被调用
     *
     * @param recyclerView recyclerView
     * @param viewHolder   viewHolder
     */
    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        Timber.i("clearView")
        //...
        super.clearView(recyclerView, viewHolder)
    }

    /**
     * 移动交换数据的更新监听
     */
    interface OnItemTouchListener {
        //拖动Item时调用
        fun onMove(fromPosition: Int, toPosition: Int)

        //滑动Item时调用
        fun onSwiped(position: Int)
    }
}
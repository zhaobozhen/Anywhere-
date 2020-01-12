package com.absinthe.anywhere_.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.absinthe.anywhere_.utils.manager.Logger;

public class ItemTouchCallBack extends ItemTouchHelper.Callback {
    private OnItemTouchListener mListener;

    public void setOnItemTouchListener(OnItemTouchListener onItemTouchListener) {
        this.mListener = onItemTouchListener;
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
    @Override
    public int getMovementFlags(RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        Logger.i("getMovementFlags");
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager ||
                recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            //此处不需要进行滑动操作，可设置为除4和8之外的整数，这里设为0
            //不支持滑动
            return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                    ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, 0);
        } else {
            //如果是LinearLayoutManager则只能向上向下滑动,不支持滑动
            return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
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
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        Logger.i("onMove");
        int fromPosition = viewHolder.getAdapterPosition();
        int toPosition = target.getAdapterPosition();
        mListener.onMove(fromPosition, toPosition);
        return true;
    }

    /**
     * 当Item被滑动的时候被调用
     * 如果你不滑动这个方法将不会被调用
     *
     * @param viewHolder viewHolder
     * @param direction  direction
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        Logger.i("onSwiped");
        //此处是侧滑删除的主要代码
        int position = viewHolder.getAdapterPosition();
        mListener.onSwiped(position);
    }

    /**
     * 当Item被滑动、拖动的时候被调用
     *
     * @param viewHolder  viewHolder
     * @param actionState actionState
     */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        Logger.i("onSelectedChanged");
        //...
        super.onSelectedChanged(viewHolder, actionState);
    }

    /**
     * 当与用户交互结束或相关动画完成之后被调用
     *
     * @param recyclerView recyclerView
     * @param viewHolder   viewHolder
     */
    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        Logger.i("clearView");
        //...
        super.clearView(recyclerView, viewHolder);
    }

    /**
     * 移动交换数据的更新监听
     */
    public interface OnItemTouchListener {
        //拖动Item时调用
        void onMove(int fromPosition, int toPosition);

        //滑动Item时调用
        void onSwiped(int position);
    }
}
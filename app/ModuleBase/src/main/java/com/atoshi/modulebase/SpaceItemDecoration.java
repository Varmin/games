package com.atoshi.modulebase;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author : Peppa
 * date   : 2019/3/12 18:11
 * desc   :  RecyclerView 的间距
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    /**
     * 位移间距
     */
    private int space;

    public SpaceItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) % 2 == 0) {
            //第一列左边贴边
            outRect.left = space / 4;
            outRect.right = space / 2;
        } else if (parent.getChildAdapterPosition(view) % 2 == 1) {
            //第二列移动一个位移间距
            outRect.left = space / 2;
            outRect.right = space / 4;
        }
        outRect.top = space / 2;
        outRect.bottom = space / 2;
    }

}

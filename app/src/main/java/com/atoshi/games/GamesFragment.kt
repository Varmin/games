package com.atoshi.games

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.atoshi.modulebase.SpaceItemDecoration
import com.atoshi.modulebase.base.BaseFragment
import com.atoshi.modulebase.net.model.GameItem
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import kotlinx.android.synthetic.main.fragment_games.*

class GamesFragment : BaseFragment() {
    companion object {
        @JvmStatic
        fun newInstance() = GamesFragment()
    }

    override fun getLayoutId(): Int = R.layout.fragment_games

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var data = mutableListOf<GameItem>().apply {
            for(index in 1..9){
                add(GameItem("game_$index", "front", "description"))
            }
        }

        rvGames.apply {
            addItemDecoration(SpaceItemDecoration(40))
            adapter = object : BaseQuickAdapter<GameItem, BaseViewHolder>(R.layout.item_games, data) {
                override fun convert(holder: BaseViewHolder, item: GameItem) {
                    holder.setText(R.id.tvGameName, item.name)
                        .setText(R.id.tvGameDes, item.description)
                    Glide.with(this@GamesFragment.activity as Context)
                        .load("https://gitee.com/varmin/noteSource/raw/master/typora/2238400422.jpg")
                        .centerCrop()
//                        .apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
                        .into(holder.getView(R.id.ivGameFrontCover))
                }
            }.apply {
                setFooterView(View(rvGames.context).apply {
                    layoutParams = ViewGroup.LayoutParams(-1, 100)
                })
                setOnItemClickListener { _, _, position ->
                    Toast.makeText(this@GamesFragment.activity, "$position", Toast.LENGTH_SHORT).show()
                }
            }
        }

        srLayout.setOnRefreshListener {
            srLayout.postDelayed({
                srLayout.isRefreshing = false
            }, 1000)
        }

    }
}
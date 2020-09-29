package com.atoshi.modulegame

/**
 * author：yang
 * created on：2020/9/9 10:49
 * description:
 */
interface IGameView {
    fun shareConfig()
    /**
     * 是否在其它游戏界面：0不在，1在
     */
    fun othersGames(status: Int)
}
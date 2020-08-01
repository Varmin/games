package com.atoshi.moduleads

import com.atoshi.modulebase.base.BaseActivity
import com.atoshi.modulebase.utils.click
import kotlinx.android.synthetic.main.activity_top_on_test.*

class TopOnTestActivity : BaseActivity() {
    override fun getLayoutId(): Int = R.layout.activity_top_on_test

    override fun initData() {}

    override fun initView() {
        btnSplash.click {
            TopOnHelper.splashAds(flAdsContainer, TopOnHelper.SPLASH_ID_GDT, null)
        }


        btnLoad.click {
            TopOnHelper.interstitialLoad(this, TopOnHelper.INTER_ID_GDT, null)
        }
        btnShow.click {
            TopOnHelper.interstitialShow()
        }

        btnLoadReward.click {
            TopOnHelper.rewardLoad(this, TopOnHelper.REWARD_ID_GDT, null)
        }
        btnShowReward.click {
             TopOnHelper.rewardShow()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        TopOnHelper.onDestroy()
    }


}
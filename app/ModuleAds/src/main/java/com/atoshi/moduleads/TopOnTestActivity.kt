package com.atoshi.moduleads

import com.anythink.interstitial.api.ATInterstitial
import com.anythink.rewardvideo.api.ATRewardVideoAd
import com.atoshi.modulebase.base.BaseActivity
import com.atoshi.modulebase.utils.click
import kotlinx.android.synthetic.main.activity_top_on_test.*

class TopOnTestActivity : BaseActivity() {
    private lateinit var mRewardAdForce: TopOnHelper.TopOnRewardAd
    private lateinit var mIntersAdForce: TopOnHelper.TopOnIntersAd
    private lateinit var mRewardAdListener: TopOnHelper.TopOnRewardListener
    private lateinit var mRewardAd: ATRewardVideoAd
    private lateinit var mIntersAdListener: TopOnHelper.TopOnInterstitialListener
    private lateinit var mIntersAd: ATInterstitial

    override fun getLayoutId(): Int = R.layout.activity_top_on_test

    override fun initData() {
        mIntersAdListener = TopOnHelper.TopOnInterstitialListener(TopOnHelper.INTER_ID_GDT, null)
        mIntersAd = ATInterstitial(this, TopOnHelper.INTER_ID_GDT).apply {
          setAdListener(mIntersAdListener)
        }
        mIntersAdForce = TopOnHelper.TopOnIntersAd(this, TopOnHelper.INTER_ID_GDT, null)


        mRewardAdListener = TopOnHelper.TopOnRewardListener(TopOnHelper.REWARD_ID_GDT, null)
        mRewardAd = ATRewardVideoAd(this, TopOnHelper.REWARD_ID_GDT).apply {
            setAdListener(mRewardAdListener)
        }
        mRewardAdForce = TopOnHelper.TopOnRewardAd(this, TopOnHelper.REWARD_ID_GDT, null)
    }

    override fun initView() {
        btnSplash.click {
        }

        btnLoad.click {
            mIntersAdForce.load()
        }
        btnShow.click {
            mIntersAdForce.forceShow()
        }

        btnLoadReward.click {
            mRewardAdForce.load()
        }
        btnShowReward.click {
            mRewardAdForce.forceShow()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
    }


}
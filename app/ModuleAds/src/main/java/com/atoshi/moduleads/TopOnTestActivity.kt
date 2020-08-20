package com.atoshi.moduleads

import com.anythink.interstitial.api.ATInterstitial
import com.anythink.rewardvideo.api.ATRewardVideoAd
import com.atoshi.modulebase.base.BaseActivity
import com.atoshi.modulebase.utils.click
import com.atoshi.modulebase.wx.WXUtils
import kotlinx.android.synthetic.main.activity_top_on_test.*

class TopOnTestActivity : BaseActivity() {
    private lateinit var mRewardAdForceGDT: TopOnHelper.TopOnRewardAd
    private lateinit var mRewardAdForcePangle: TopOnHelper.TopOnRewardAd
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


        mRewardAdForceGDT = TopOnHelper.TopOnRewardAd(this, TopOnHelper.REWARD_ID_GDT, null)
        mRewardAdForcePangle = TopOnHelper.TopOnRewardAd(this, TopOnHelper.REWARD_ID_PANGLE, null)
    }

    override fun initView() {
        btnSplash.click {
            runOnUiThread { WXUtils.login() }
        }

        btnLoad.click {
            mRewardAd.load()
        }
        btnShow.click {
            mRewardAd.show()
        }

        btnLoadReward.click {
            mRewardAdForceGDT.forceShow()
        }
        btnShowReward.click {
            mRewardAdForcePangle.forceShow()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
    }


}
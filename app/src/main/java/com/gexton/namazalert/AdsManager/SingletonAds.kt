package com.gexton.namazalert.AdsManager

import android.content.Context
import com.google.android.gms.ads.*
import com.gexton.namazalert.R

class SingletonAds private constructor(context: Context) {

    private object InterstitialAds {
        fun instance(context: Context): SingletonAds {
            return SingletonAds(context)
        }
    }

    companion object {
        lateinit var instance: InterstitialAd
        fun init(context: Context): SingletonAds {
            val instance: SingletonAds by lazy { InterstitialAds.instance(context) }
            return instance
        }
    }

    private fun setupAd(context: Context) {
        val testDevices: MutableList<String> = ArrayList()
        testDevices.add(AdRequest.DEVICE_ID_EMULATOR)
        val requestConfiguration = RequestConfiguration.Builder().setTestDeviceIds(testDevices).build()
        MobileAds.setRequestConfiguration(requestConfiguration)
        val adRequest = AdRequest.Builder().build()
        instance = InterstitialAd(context.applicationContext)
        instance.adUnitId = context.getString(R.string.admob_interstitial)
        instance.loadAd(adRequest)
        instance.adListener = object : AdListener() {
            override fun onAdClosed() {
                instance.loadAd(adRequest)
                super.onAdClosed()
            }

            override fun onAdFailedToLoad(p0: Int) {
                instance.loadAd(adRequest)
                super.onAdFailedToLoad(p0)
            }
        }
    }

    init {
        setupAd(context)
    }
}
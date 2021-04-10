package com.gexton.salahreminder.AdsManager

import android.content.Context
import android.widget.FrameLayout
import com.gexton.salahreminder.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import java.util.*


fun Context.showBanner(bannerLayout: FrameLayout) {

    val adaptiveAds = AdaptiveAds(this)
    val adView = AdView(this)
    adView.adUnitId = getString(R.string.admob_banner_id)
    bannerLayout.addView(adView)
    val testDevices = ArrayList<String>()
    testDevices.add(AdRequest.DEVICE_ID_EMULATOR)
    val requestConfiguration = RequestConfiguration.Builder().setTestDeviceIds(testDevices).build()
    MobileAds.setRequestConfiguration(requestConfiguration)
    adView.adSize = adaptiveAds.adSize
    adView.loadAd(AdRequest.Builder().build())
}

fun Context.showInterstitial() {

    if (SingletonAds.instance.isLoaded) {
        SingletonAds.instance.show()
    }

}
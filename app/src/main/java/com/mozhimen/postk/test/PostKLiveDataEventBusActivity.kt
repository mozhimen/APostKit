package com.mozhimen.postk.test

import android.os.Bundle
import com.mozhimen.basick.elemk.androidx.appcompat.bases.databinding.BaseActivityVDB
import com.mozhimen.basick.utilk.android.widget.showToast
import com.mozhimen.postk.livedata.PostKLiveData
import com.mozhimen.postk.test.databinding.ActivityPostkLiveDataEventBusBinding

class PostKLiveDataEventBusActivity : BaseActivityVDB<ActivityPostkLiveDataEventBusBinding>() {
    override fun initView(savedInstanceState: Bundle?) {
        PostKLiveData.instance.with<String>("stickyData").observeSticky(this) {
            "黏性事件: $it".showToast()
        }

        PostKLiveData.instance.with<String>("stickyData").observe(this) {
            "非黏性事件: $it".showToast()
        }

        vdb.utilkDataBusMsgBtn.setOnClickListener {
            PostKLiveData.instance.with<String>("stickyData").setStickyValue("即时消息当前界面")
        }
    }
}
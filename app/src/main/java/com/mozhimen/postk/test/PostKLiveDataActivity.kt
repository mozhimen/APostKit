package com.mozhimen.postk.test

import android.os.Bundle
import com.mozhimen.uik.databinding.bases.viewdatabinding.activity.BaseActivityVDB
import com.mozhimen.kotlin.utilk.android.widget.showToast
import com.mozhimen.postk.livedata.PostKLiveData
import com.mozhimen.postk.test.databinding.ActivityPostkLiveDataBinding

class PostKLiveDataActivity : BaseActivityVDB<ActivityPostkLiveDataBinding>() {
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
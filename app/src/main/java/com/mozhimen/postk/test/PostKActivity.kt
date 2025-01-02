package com.mozhimen.postk.test

import android.os.Bundle
import android.view.View
import com.mozhimen.bindk.bases.viewdatabinding.activity.BaseActivityVDB
import com.mozhimen.kotlin.utilk.android.content.startContext
import com.mozhimen.postk.flow.PostKFlow
import com.mozhimen.postk.livedata.PostKLiveData
import com.mozhimen.postk.test.databinding.ActivityPostkBinding

class PostKActivity : BaseActivityVDB<ActivityPostkBinding>() {
    override fun initData(savedInstanceState: Bundle?) {
        PostKLiveData.instance.with<String>("stickyData").setStickyValue("即时消息主界面")
        PostKFlow.instance.withSticky<String>("stickyData").emitOnMain("即时消息主界面")
        super.initData(savedInstanceState)
    }

    fun goPostKLiveData(view: View) {
        startContext<PostKLiveDataActivity>()
    }

    fun goPostKFlow(view: View) {
        startContext<PostKFlowActivity>()
    }
}

data class SampleEvent(
    val name: String = "Tome",
)

sealed interface ParentEvent {
    data class ChildEvent(val name: String = "child") : ParentEvent
}
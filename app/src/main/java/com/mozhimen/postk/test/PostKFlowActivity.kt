package com.mozhimen.postk.test

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.mozhimen.bindk.bases.viewdatabinding.activity.BaseActivityVDB
import com.mozhimen.kotlin.utilk.android.util.UtilKLogWrapper
import com.mozhimen.kotlin.utilk.android.widget.showToast
import com.mozhimen.postk.flow.PostKFlow
import com.mozhimen.postk.test.databinding.ActivityPostkFlowBinding
import kotlinx.coroutines.launch

class PostKFlowActivity : BaseActivityVDB<ActivityPostkFlowBinding>() {
    override fun initView(savedInstanceState: Bundle?) {
//        PostKLiveData.instance.with<String>("stickyData").observeSticky(this) {
//            "黏性事件: $it".showToast()
//        }
//
//        PostKLiveData.instance.with<String>("stickyData").observe(this) {
//            "非黏性事件: $it".showToast()
//        }
        lifecycleScope.launch {
//            PostKFlow.flowOf<SampleEvent>().collect {
//                "非黏性事件: $it".showToast()
//            }
//            PostKFlow.flowOf<ParentEvent>().collect {
//                "非黏性事件: $it".showToast()
//            }
//            PostKFlow.flowOf<ParentEvent.ChildEvent>().collect {
//                "非黏性事件: $it".showToast()
//            }

            PostKFlow.instance.withSticky<String>("stickyData").apply {
                UtilKLogWrapper.d(TAG, "stickyData: $this")
            }.collectAsync {
                "黏性事件: $it".showToast()
            }
        }

        lifecycleScope.launch {
            PostKFlow.instance.with<SampleEvent>("stickyData1").apply {
                UtilKLogWrapper.d(TAG, "stickyData1: $this")
            }.collectAsync {
                Log.d(TAG, "initView: stickyData1 $it")
                "非黏性事件: $it".showToast()
            }
        }

        lifecycleScope.launch {
            PostKFlow.instance.with<ParentEvent.ChildEvent>("stickyData2").apply {
                UtilKLogWrapper.d(TAG, "stickyData1: $this")
            }.collectAsync {
                Log.d(TAG, "initView: stickyData1 $it")
                "非黏性事件: $it".showToast()
            }
        }

        vdb.utilkDataBusMsgBtn.setOnClickListener {
            PostKFlow.instance.with<String>("stickyData").emitOnMain("即时消息当前界面")
            PostKFlow.instance.with<SampleEvent>("stickyData1").emitOnMain(SampleEvent())
            PostKFlow.instance.with<ParentEvent>("stickyData2").emitOnMain(ParentEvent.ChildEvent())
        }
    }
}
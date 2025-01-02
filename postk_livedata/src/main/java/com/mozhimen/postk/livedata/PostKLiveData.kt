package com.mozhimen.postk.livedata

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.mozhimen.kotlin.elemk.androidx.lifecycle.sticky.MutableLiveDataSticky
import com.mozhimen.kotlin.elemk.kotlin.cons.CSuppress
import com.mozhimen.postk.basic.commons.IPostK
import com.mozhimen.postk.basic.commons.IPostKProvider
import com.mozhimen.postk.livedata.PostKLiveData.MutableEventLiveDataSticky
import java.util.concurrent.ConcurrentHashMap

/**
 * @ClassName SenseKLiveDataEventBus
 * @Description
 * 基于事件名称 订阅,分发消息
 * 由于一个liveData只能发送 一种数据类型
 * 所以 不同的event事件, 需要使用不同的liveData实例去分发
 * @Author mozhimen / Kolin Zhao
 * @Date 2022/2/6 16:53
 * @Version 1.0
 */
class PostKLiveData : IPostK<MutableEventLiveDataSticky<*>> {
    companion object {
        @JvmStatic
        val instance = INSTANCE.holder
    }

    /////////////////////////////////////////////////////////////////////////////////////

    private val _eventLiveDataMap = ConcurrentHashMap<String, MutableEventLiveDataSticky<*>>()

    /////////////////////////////////////////////////////////////////////////////////////

    @Suppress(CSuppress.UNCHECKED_CAST)
    override fun <T> with(eventName: String): MutableEventLiveDataSticky<T> =
        _eventLiveDataMap.getOrPut(eventName) { MutableEventLiveDataSticky<T>(eventName) } as MutableEventLiveDataSticky<T>

    /////////////////////////////////////////////////////////////////////////////////////

    internal inner class InnerLifecycleEventObserver(private val _name: String) :
        LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            if (event == Lifecycle.Event.ON_DESTROY)
                _eventLiveDataMap.remove(_name)//监听宿主 发生销毁事件, 主动把liveData移除掉
        }
    }

    inner class MutableEventLiveDataSticky<T>(override val eventName: String) : MutableLiveDataSticky<T>(), IPostKProvider<T> {
        override fun observeSticky(owner: LifecycleOwner, observer: Observer<in T>) {
            owner.lifecycle.addObserver(InnerLifecycleEventObserver(eventName))
            super.observeSticky(owner, observer)
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////

    private object INSTANCE {
        val holder = PostKLiveData()
    }
}
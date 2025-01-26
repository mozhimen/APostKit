package com.mozhimen.postk.flow

import com.mozhimen.kotlin.elemk.commons.ISuspendA_Listener
import com.mozhimen.kotlin.elemk.kotlin.cons.CSuppress
import com.mozhimen.kotlin.utilk.android.util.UtilKLogWrapper
import com.mozhimen.kotlin.utilk.commons.IUtilK
import com.mozhimen.postk.basic.commons.IPostK
import com.mozhimen.postk.basic.commons.IPostKProvider
import com.mozhimen.postk.flow.PostKFlow.MutableEventSharedFlow
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

/**
 * @ClassName PostKFlow
 * @Description TODO
 * @Author mozhimen
 * @Date 2025/1/2
 * @Version 1.0
 */
class PostKFlow : IPostK<MutableEventSharedFlow<*>> {
    companion object {
        @JvmStatic
        val instance = INSTANCE.holder
    }

    /////////////////////////////////////////////////////////////////////////////////////

    private val _eventFlowMap = ConcurrentHashMap<String, MutableEventSharedFlow<*>>()

    /////////////////////////////////////////////////////////////////////////////////////

    @Suppress(CSuppress.UNCHECKED_CAST)
    override fun <T> with(eventName: String): MutableEventSharedFlow<T> =
        _eventFlowMap.getOrPut(eventName) { MutableEventSharedFlow<T>(eventName) } as MutableEventSharedFlow<T>

    fun <T> withSticky(eventName: String): MutableEventSharedFlow<T> =
        _eventFlowMap.getOrPut(eventName) { MutableStickyEventSharedFlow<T>(eventName) } as MutableEventSharedFlow<T>

    /////////////////////////////////////////////////////////////////////////////////////

    open inner class MutableEventSharedFlow<T>(override val eventName: String) : IPostKProvider<T>, MutableSharedFlow<T>, IUtilK {
        protected open val _mutableSharedFlow = MutableSharedFlow<T>()

        override val replayCache: List<T>
            get() = _mutableSharedFlow.replayCache

        override val subscriptionCount: StateFlow<Int>
            get() = _mutableSharedFlow.subscriptionCount

        @ExperimentalCoroutinesApi
        override fun resetReplayCache() {
            _mutableSharedFlow.resetReplayCache()
        }

        override fun tryEmit(value: T): Boolean =
            _mutableSharedFlow.tryEmit(value)

        override suspend fun emit(value: T) {
            _mutableSharedFlow.emit(value)
        }

        override suspend fun collect(collector: FlowCollector<T>): Nothing =
            _mutableSharedFlow.collect(collector)

        ////////////////////////////////////////////

        fun emitOnMain(event: T) {
            @OptIn(DelicateCoroutinesApi::class)
            GlobalScope.launch(Dispatchers.Main) {
                emit(event)
            }
        }

        suspend fun collectSafe(block: ISuspendA_Listener<T>) {
            withContext(Dispatchers.Main) {
                @Suppress("UNCHECKED_CAST")
                try {
                    collect { block.invoke(it) }
                } catch (e: Exception) {
                    e.printStackTrace()
                    UtilKLogWrapper.e(TAG, "collectSafe: ${e.message}")
                } finally {
                    if (subscriptionCount.value == 0) {
                        UtilKLogWrapper.d(TAG, "collectSafe: ")
                        _eventFlowMap.remove(eventName)
                    }
                }
            }
        }

        fun channelFlowOf(): Flow<T> =
            channelFlow { collectSafe(::send) }

        suspend fun collectAsync(block: ISuspendA_Listener<T>) {
            channelFlowOf().collect { block.invoke(it) }
        }
    }

    inner class MutableStickyEventSharedFlow<T>(eventName: String) : MutableEventSharedFlow<T>(eventName) {
        override val _mutableSharedFlow: MutableSharedFlow<T> = MutableSharedFlow(replay = 1, extraBufferCapacity = Int.MAX_VALUE)
    }

    /////////////////////////////////////////////////////////////////////////////////////

    private object INSTANCE {
        val holder = PostKFlow()
    }
}

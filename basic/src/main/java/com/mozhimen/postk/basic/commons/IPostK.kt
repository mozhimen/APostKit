package com.mozhimen.postk.basic.commons

/**
 * @ClassName IPostK
 * @Description TODO
 * @Author mozhimen
 * @Date 2025/1/2
 * @Version 1.0
 */
interface IPostK<P : IPostKProvider<*>> {
    fun <T> with(eventName: String): P
}

interface IPostKProvider<T> {
    val eventName: String
}
package com.androidcollider.easyfin.common.utils

import java.util.concurrent.*

/**
 * @author Ihor Bilous
 */
object BackgroundExecutor {
    private const val CORE_POOL_SIZE = 1
    private const val MAXIMUM_POOL_SIZE = 3
    private const val KEEP_ALIVE = 1
    private val sPoolWorkQueue: BlockingQueue<Runnable> = LinkedBlockingQueue(128)

    /**
     * An [Executor] that can be used to execute tasks in parallel.
     */
    val safeBackgroundExecutor: Executor = ThreadPoolExecutor(
        CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE.toLong(),
        TimeUnit.SECONDS, sPoolWorkQueue
    )
}
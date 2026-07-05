package com.ltcg.multicoremagic;

import net.minecraft.TracingExecutor;
import net.minecraft.util.Util;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Vanilla builds chunk render meshes on Util.backgroundExecutor(), a pool shared with worldgen,
 * structure searches, and other background work (sized cores-1, see net.minecraft.util.Util).
 * This gives chunk mesh building its own dedicated, live-resizable pool instead, so it doesn't
 * contend with everything else sharing that pool.
 */
public final class ChunkBuilderThreadPool {
    private static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {
        private final AtomicInteger count = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "Multicore Magic Chunk Builder #" + count.getAndIncrement());
            thread.setDaemon(true);
            return thread;
        }
    };

    private static final ThreadPoolExecutor POOL = new ThreadPoolExecutor(
        MulticoreMagicConfig.defaultThreadCount(), MulticoreMagicConfig.defaultThreadCount(),
        0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), THREAD_FACTORY
    );

    private static final TracingExecutor DEDICATED_EXECUTOR = new TracingExecutor(POOL);

    private ChunkBuilderThreadPool() {
    }

    public static TracingExecutor executorFor(MulticoreMagicConfig config) {
        return config.enabled ? DEDICATED_EXECUTOR : Util.backgroundExecutor();
    }

    public static void applyThreadCount(int threads) {
        if (threads > POOL.getMaximumPoolSize()) {
            POOL.setMaximumPoolSize(threads);
            POOL.setCorePoolSize(threads);
        } else {
            POOL.setCorePoolSize(threads);
            POOL.setMaximumPoolSize(threads);
        }
    }

    public static int currentThreadCount() {
        return POOL.getCorePoolSize();
    }

    public static int activeThreads() {
        return POOL.getActiveCount();
    }

    public static int queuedTasks() {
        return POOL.getQueue().size();
    }
}

package org.javamoney.moneta.internal.loader;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * <p>Decorator to {@link Executors#defaultThreadFactory()} that make thread as daemon.</p>
 * Set {@link Thread#setDaemon(boolean)} as <code>true</code>
 * @see {@link Thread}
 * @author Otavio Santana
 */
enum DaemonThreadFactory implements ThreadFactory {

	/**
	 * The singleton instance to {@link DaemonThreadFactory}
	 */
	INSTANCE;

	private final ThreadFactory threadFactory;

	{
		threadFactory = Executors.defaultThreadFactory();
	}

	@Override
	public Thread newThread(Runnable runnable) {
		Thread thread = threadFactory.newThread(runnable);
		thread.setDaemon(true);
		return thread;
	}

}

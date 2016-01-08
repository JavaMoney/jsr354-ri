/**
 * Copyright (c) 2012, 2015, Credit Suisse (Anatole Tresch), Werner Keil and others by the @author tag.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
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

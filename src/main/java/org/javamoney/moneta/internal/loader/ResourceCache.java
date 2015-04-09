/**
 * Copyright (c) 2012, 2014, Credit Suisse (Anatole Tresch), Werner Keil and others by the @author tag.
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

import java.io.IOException;

/**
 * Abstraction of a {@link ResourceCache}. By default a file cache is used:
 * {@link DefaultResourceCache}.
 *
 * @author Anatole Tresch
 */
public interface ResourceCache {
    /**
     * Write the given byte array to the format store and register it on the
     * given resource ID.
     *
     * @param resourceId
     *            the resource id, never {@code null}.
     * @param data
     *            the data
     * @throws IOException
     *             when an IO error occurs.
     */
    void write(String resourceId, byte[] data)throws IOException;

    /**
     * Allows to query if a resource with the given id is present within the
     * local cache.
     *
     * @param resourceId
     *            The resourceId
     * @return true, if the resource was found in the local cache.
     */
    boolean isCached(String resourceId);

    /**
     * Reads the given resource, identified by the resourceId, from the cache.
     *
     * @param resourceId
     *            the resource id.
     * @return the data of the resource.
     * @throws IllegalArgumentException
     *             if no such resource is existing.
     */
    byte[] read(String resourceId);

    /**
     * Remove a cache entry.
     * @param resourceId the resource identifier, not null.
     */
    void clear(String resourceId);
}

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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.money.MonetaryException;

/**
 * Default implementation of {@link ResourceCache}, using the local file system.
 *
 * @author Anatole Tresch
 */
public class DefaultResourceCache implements ResourceCache {
    /**
     * The logger used.
     */
    private static final Logger LOG = Logger.getLogger(DefaultResourceCache.class.getName());
    /**
     * Suffix for files created.
     */
    private static final String SUFFIX = ".dat";
    /**
     * Local temp directory.
     */
    private File localDir = new File(System.getProperty("temp.dir", ".resourceCache"));
    /**
     * Cached resources.
     */
    private final Map<String, File> cachedResources = new ConcurrentHashMap<>();

    /**
     * Constructor.
     */
    public DefaultResourceCache() {
        if (!localDir.exists()) {
            if (!localDir.mkdirs()) {
                LOG.severe("Error creating cache dir  " + localDir + ", resource cache disabled!");
                localDir = null;
            } else {
                LOG.finest("Created cache dir  " + localDir);
            }
        } else if (!localDir.isDirectory()) {
            LOG.severe("Error initializing cache dir  " + localDir + ", not a directory, resource cache disabled!");
            localDir = null;
        } else if (!localDir.canWrite()) {
            LOG.severe("Error initializing cache dir  " + localDir + ", not writable, resource cache disabled!");
            localDir = null;
        }
        if (Objects.nonNull(localDir)) {
            File[] files = localDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        String resourceId = file.getName().substring(0, file.getName().length() - 4);
                        cachedResources.put(resourceId, file);
                    }
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.javamoney.moneta.loader.format.ResourceCache#write(java.lang.String
     * , byte[])
     */
    @Override
    public void write(String resourceId, byte[] data) {
        try {
            File file = this.cachedResources.get(resourceId);
            if (Objects.isNull(file)) {
                file = new File(localDir, resourceId + SUFFIX);
                Files.write(file.toPath(), data);
                this.cachedResources.put(resourceId, file);
            } else {
            	Files.write(file.toPath(), data);
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Caching of resource failed: " + resourceId, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.javamoney.moneta.loader.format.ResourceCache#isCached(java.lang
     * .String)
     */
    @Override
    public boolean isCached(String resourceId) {
        return this.cachedResources.containsKey(resourceId);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.javamoney.moneta.loader.format.ResourceCache#read(java.lang.String)
     */
    @Override
    public byte[] read(String resourceId) {
        File f = this.cachedResources.get(resourceId);
        if (Objects.isNull(f)) {
            return null;
        }
        try {
			return Files.readAllBytes(f.toPath());
		} catch (IOException exception) {
			throw new MonetaryException("An error on retrieve the resource id: " + resourceId, exception);

		}
    }

    @Override
    public void clear(String resourceId) {
        File file = this.cachedResources.get(resourceId);
        if (file != null) {
            if (file.exists()) {
                if (!file.delete()) {
                    LOG.warning("Failed to delete caching file: " + file.getAbsolutePath());
                }
            }
            this.cachedResources.remove(resourceId);
        }
    }

    @Override
    public String toString() {
        return "DefaultResourceCache [localDir=" + localDir + ", cachedResources=" + cachedResources + ']';
    }

}

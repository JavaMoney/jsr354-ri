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
package org.javamoney.moneta.loader.internal;

import java.io.*;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private Map<String, File> cachedResources = new ConcurrentHashMap<>();

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
     * org.javamoney.moneta.loader.internal.ResourceCache#write(java.lang.String
     * , byte[])
     */
    @Override
    public void write(String resourceId, byte[] data) {
        try {
            File f = this.cachedResources.get(resourceId);
            if (Objects.isNull(f)) {
                f = new File(localDir, resourceId + SUFFIX);
                writeFile(f, data);
                this.cachedResources.put(resourceId, f);
            } else {
                writeFile(f, data);
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Caching of resource failed: " + resourceId, e);
        }
    }

    /**
     * Writees a file with the given data,
     *
     * @param f    the file
     * @param data the data
     * @throws IOException if writing failed.
     */
    private void writeFile(File f, byte[] data) throws IOException {
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(f));
            bos.write(data);
            bos.flush();
        } finally {
            try {
                if (Objects.nonNull(bos)) {
                    bos.close();
                }
            } catch (Exception e2) {
                LOG.log(Level.SEVERE, "Error closing output stream for " + f, e2);
            }
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.javamoney.moneta.loader.internal.ResourceCache#isCached(java.lang
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
     * org.javamoney.moneta.loader.internal.ResourceCache#read(java.lang.String)
     */
    @Override
    public byte[] read(String resourceId) {
        File f = this.cachedResources.get(resourceId);
        if (Objects.isNull(f)) {
            return null;
        }
        return readFile(f);
    }

    @Override
    public void clear(String resourceId) {
        File f = this.cachedResources.get(resourceId);
        if (f != null) {
            if (f.exists()) {
                if (!f.delete()) {
                    LOG.warning("Failed to delete caching file: " + f.getAbsolutePath());
                }
            }
            this.cachedResources.remove(resourceId);
        }
    }

    /**
     * Read a file.
     *
     * @param f the file
     * @return the bytes read.
     */
    private byte[] readFile(File f) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        BufferedInputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(f));
            byte[] input = new byte[1024];
            int read = 1;
            while (read > 0) {
                read = is.read(input);
                if (read > 0) {
                    bos.write(input, 0, read);
                }
            }
            return bos.toByteArray();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error reading cached resource from " + f, e);
            return null;
        } finally {
            try {
                if (Objects.nonNull(is)) {
                    is.close();
                }
            } catch (Exception e2) {
                LOG.log(Level.SEVERE, "Error closing input stream from " + f, e2);
            }
        }
/*
URI fileUri = this.cachedResource;
        if(fileUri == null){
            String userHome = System.getProperty("user.home");
            File file = new File(userHome + "/.cache", resourceId);
            if(file.exists()){
                fileUri = file.toURI();
            }
        }
        if(fileUri != null){
            File file = new File(fileUri);
            try(
                    FileInputStream fis = new FileInputStream(file);
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    ObjectInputStream ois = new ObjectInputStream(bis)
            ){
                long loadTS = ois.readLong();
                byte[] data = (byte[]) ois.readObject();
                this.lastLoaded = loadTS;
                setData(data);
                return true;
            }
            catch(Exception e){
                LOG.log(Level.WARNING, "Failed to read data from cache: " + fileUri, e);
            }
        }
        return false;
 */
    }

    @Override
    public String toString() {
        return "DefaultResourceCache [localDir=" + localDir + ", cachedResources=" + cachedResources + ']';
    }

}

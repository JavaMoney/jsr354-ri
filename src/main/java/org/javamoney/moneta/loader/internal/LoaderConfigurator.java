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

import org.javamoney.moneta.spi.LoaderService;
import org.javamoney.moneta.spi.LoaderService.UpdatePolicy;
import org.javamoney.moneta.spi.MonetaryConfig;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

class LoaderConfigurator{

    private static final String LOAD = "load.";

    private static final String TYPE = "type";

    private static final Logger LOG = Logger.getLogger(LoaderConfigurator.class.getName());

    private LoaderService loaderService;

    public LoaderConfigurator(LoaderService loaderService){
        Objects.requireNonNull(loaderService);
        this.loaderService = loaderService;
    }

    public void load(){
        Map<String,String> config = MonetaryConfig.getConfig();
        // collect loads
        Set<String> loads = new HashSet<>();
        for(String key : config.keySet()){
            if(key.startsWith(LOAD) && key.endsWith('.' + TYPE)){
                String res = key.substring(LOAD.length());
                res = res.substring(0, res.length() - ('.' + TYPE).length());
                loads.add(res);
            }
        }
        // init loads
        for(String l : loads){
            try{
                initResource(l, config);
            }
            catch(Exception e){
                LOG.log(Level.SEVERE, "Failed to initialize/register resource: " + l, e);
            }
        }
    }

    private void initResource(String name, Map<String,String> allProps) throws MalformedURLException{
        Map<String,String> props = mapProperties(allProps, name);
        UpdatePolicy updatePolicy = UpdatePolicy.valueOf(props.get(TYPE));
        String fallbackRes = props.get("resource");
        if(Objects.isNull(fallbackRes)){
            throw new IllegalArgumentException(LOAD + name + ".resource (classpath resource) required.");
        }
        String resourcesString = props.get("urls");
        String[] resources;
        if(Objects.isNull(resourcesString)){
            LOG.info("No update URLs configured for: " + name);
            resources = new String[0];
        }else{
            resources = resourcesString.split(",");
        }
        URI[] urls = createURIs(resources);
        this.loaderService.registerData(name, updatePolicy, props, getClassLoaderLocation(fallbackRes), urls);
    }

    private URI[] createURIs(String[] resources) throws MalformedURLException{
        List<URI> urls = new ArrayList<>(resources.length);
        for(String res : resources){
            if(res.trim().isEmpty()){
                continue;
            }
            try{
                urls.add(new URL(res.trim()).toURI());
            }
            catch(URISyntaxException e){
                LOG.log(Level.WARNING, "Failed to load resoiurce as URI: " + res.trim(), e);
            }
        }
        return urls.toArray(new URI[urls.size()]);
    }

    private URI getClassLoaderLocation(String res){
        URL url = null;
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if(Objects.nonNull(cl)){
            url = cl.getResource(res);
        }
        if(Objects.isNull(url)){
            url = getClass().getResource(res);
        }
        if(Objects.isNull(url)){
            throw new IllegalArgumentException("Resource not found: " + res);
        }
        try{
            return url.toURI();
        }
        catch(URISyntaxException e){
            LOG.log(Level.WARNING, "Failed to load resoiurce as URI: " + res.trim(), e);
            return null;
        }
    }

    private Map<String,String> mapProperties(Map<String,String> allProps, String name){
        Map<String,String> props = new HashMap<>();
        String start = LOAD + name;
        for(Map.Entry<String,String> entry : allProps.entrySet()){
            if(entry.getKey().startsWith(start)){
                props.put(entry.getKey().substring(start.length() + 1), entry.getValue());
            }
        }
        return props;
    }

}

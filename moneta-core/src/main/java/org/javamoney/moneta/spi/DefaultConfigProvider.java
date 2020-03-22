/*
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
package org.javamoney.moneta.spi;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default implementation of the JavaMoney configuration provider reading all present properties from
 * {@code classpath*:javamoney.properties}. Any key can be explicitly overridden by using system
 * properties.
 */
public class DefaultConfigProvider implements MonetaryConfigProvider {

    private static final Logger LOG = Logger.getLogger(DefaultConfigProvider.class.getName());

    private final Map<String, Integer> priorities = new HashMap<>();
    public final Map<String, String> config = new ConcurrentHashMap<>();

    public DefaultConfigProvider(){
        try {
            Enumeration<URL> urls = getClass().getClassLoader().getResources(
                    "javamoney.properties");
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                try {
                    Properties props = new Properties();
                    props.load(url.openStream());
                    updateConfig(props);
                } catch (Exception e) {
                    LOG.log(Level.SEVERE,
                            "Error loading javamoney.properties, ignoring "
                                    + url, e);
                }
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error loading javamoney.properties.", e);
        }
        System.getProperties().forEach((k,v) -> {
            this.config.put(k.toString(), v.toString());
        });
    }

    @Override
    public String getProperty(String key) {
        String sysProp = System.getProperty(key);
        if(sysProp!=null){
            return sysProp;
        }
        return getProperties().get(key);
    }

    @Override
    public Map<String,String> getProperties(){
        return config;
    }

    private void updateConfig(Properties props) {
        for (Map.Entry<Object, Object> en : props.entrySet()) {
            String key = en.getKey().toString();
            String value = en.getValue().toString();
            int prio = 0;
            if (key.startsWith("{")) {
                int index = key.indexOf('}');
                if (index > 0) {
                    String prioString = key.substring(1, index);
                    try {
                        prio = Integer.parseInt(prioString);
                        key = key.substring(index + 1);
                    } catch (NumberFormatException e) {
                        LOG.warning("Invalid config key in javamoney.properties: " + key);
                    }
                }
            }
            Integer existingPrio = priorities.get(key);
            if (Objects.isNull(existingPrio)) {
                priorities.put(key, prio);
                config.put(key, value);
            } else if (existingPrio < prio) {
                priorities.put(key, prio);
                config.put(key, value);
            } else if (existingPrio == prio) {
                throw new IllegalStateException(
                        "AmbiguousConfiguration detected for '" + key + "'.");
            }
            // else ignore entry with lower prio!
        }
    }
}

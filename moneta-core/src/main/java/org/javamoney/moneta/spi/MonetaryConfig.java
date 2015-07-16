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
package org.javamoney.moneta.spi;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Loader for the Java Money JSR configuration.
 *
 * @author Anatole Tresch
 */
public final class MonetaryConfig {

    private static final Logger LOG = Logger
            .getLogger(MonetaryConfig.class.getName());

    private static final MonetaryConfig INSTANCE = new MonetaryConfig();

    private final Map<String, String> config = new HashMap<>();
    private final Map<String, Integer> priorities = new HashMap<>();

    private MonetaryConfig() {
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

    public static Map<String, String> getConfig() {
        return Collections.unmodifiableMap(INSTANCE.config);
    }

}

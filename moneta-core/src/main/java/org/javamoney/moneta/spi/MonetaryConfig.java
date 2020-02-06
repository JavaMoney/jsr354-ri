/*
  Copyright (c) 2012, 2014, Credit Suisse (Anatole Tresch), Werner Keil and others by the @author tag.

  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy of
  the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  License for the specific language governing permissions and limitations under
  the License.
 */
package org.javamoney.moneta.spi;

import org.javamoney.moneta.internal.DefaultConfigProvider;

import javax.money.spi.Bootstrap;
import java.util.*;
import java.util.logging.Logger;

/**
 * Loader for the Java Money JSR configuration.
 *
 * @author Anatole Tresch
 * @deprecated Will be removed from the SPI. Implement and register an instance of {@link MonetaryConfigProvider}
 * instead of.
 * @see MonetaryConfigProvider
 */
@Deprecated
public final class MonetaryConfig {

    private static final Logger LOG = Logger
            .getLogger(MonetaryConfig.class.getName());

    private static final MonetaryConfig INSTANCE = new MonetaryConfig();

    private MonetaryConfig() {
    }

    /**
     * Sets a new config value. Note that when a custom {@link MonetaryConfigProvider} is registered, writing
     * of configuration values is not supported. In this case a debug log (fine) is written.
     *
     * @param key the key to be set, not null.
     * @param value the new value, or null.
     * @return the previous value.
     */
    public static String setValue(String key, String value){
        if(Bootstrap.getService(MonetaryConfigProvider.class) instanceof DefaultConfigProvider) {
            DefaultConfigProvider defaultConfigProvider = (DefaultConfigProvider)Bootstrap.getService(MonetaryConfigProvider.class);
            if (value == null) {
                return defaultConfigProvider.config.remove(key);
            }
            return defaultConfigProvider.config.put(key, value);
        }
        LOG.fine("MonetaryConfig does not support deprecated write of " + key + "=" + value);
        return null;
    }

    public static Optional<String> getString(String key){
        return Optional.ofNullable(Bootstrap.getService(MonetaryConfigProvider.class).getProperty(key));
    }

    public static Optional<Boolean> getBoolean(String key){
        String val = Bootstrap.getService(MonetaryConfigProvider.class).getProperty(key);
        if(val != null){
            return Optional.ofNullable(Boolean.parseBoolean(val));
        }
        return Optional.empty();
    }

    public static Optional<Integer> getInteger(String key){
        String val = Bootstrap.getService(MonetaryConfigProvider.class).getProperty(key);
        if(val != null){
            return Optional.ofNullable(Integer.parseInt(val));
        }
        return Optional.empty();
    }

    public static Optional<Long> getLong(String key){
        String val = Bootstrap.getService(MonetaryConfigProvider.class).getProperty(key);
        if(val != null){
            return Optional.ofNullable(Long.parseLong(val));
        }
        return Optional.empty();
    }

    public static Optional<Float> getFloat(String key){
        String val = Bootstrap.getService(MonetaryConfigProvider.class).getProperty(key);
        if(val != null){
            return Optional.ofNullable(Float.parseFloat(val));
        }
        return Optional.empty();
    }

    public static Optional<Double> getDouble(String key){
        String val = Bootstrap.getService(MonetaryConfigProvider.class).getProperty(key);
        if(val != null){
            return Optional.ofNullable(Double.parseDouble(val));
        }
        return Optional.empty();
    }

    public static Map<String, String> getConfig() {
        return Collections.unmodifiableMap(Bootstrap.getService(MonetaryConfigProvider.class).getProperties());
    }


}

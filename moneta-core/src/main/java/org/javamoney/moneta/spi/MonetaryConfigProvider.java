/*
  Copyright (c) 2012, 2020, Anatole Tresch, Werner Keil and others by the @author tag.

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

import java.util.Map;

/**
 * Dynamic interface for providing monetary configuration. Register a custom implementation with the {@link java.util.ServiceLoader}
 * to configure JavaMoney.
 */
public interface MonetaryConfigProvider {

    /**
     * Simple methoid to get a property.
     * @param key the key, not null.
     * @return the value, or null.
     */
    default String getProperty(String key){
        return getProperties().get(key);
    }

    /**
     * Get all currently known properties.
     * @return the properties, never null.
     */
    Map<String, String> getProperties();
}

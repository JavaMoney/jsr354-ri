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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used for prioritizing multiple services of the same type. The resulting order can be
 * used, for defining the order within a chain of services, or to select the implementation to be
 * used.
 *
 * @author Anatole Tresch
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServicePriority {

    /**
     * Normal priority.
     */
    public static final int NORM_PRIORITY = 0;
    /**
     * Low priority.
     */
    public static final int LOW_PRIORITY = -100;
    /**
     * High priority.
     */
    public static final int HIGH_PRIORITY = 100;

    /**
     * The priority value.
     */
    int value();
}

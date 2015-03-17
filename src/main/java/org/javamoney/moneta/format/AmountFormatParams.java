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
package org.javamoney.moneta.format;

/**
 * Class holding constants for passing additional parameters to {@link javax.money.MonetaryAmountFactoryQuery}
 * instances for configuring {@link javax.money.format.MonetaryAmountFormat} instances to be accessed.
 */
public final class AmountFormatParams {

    /**
     * Non instantiatable class.
     */
    private AmountFormatParams() {
    }

    /**
     * Allows to pass a pattern as defined by {@link java.text.DecimalFormat}.
     */
    public static final String PATTERN = "pattern";

    /**
     * Allows to define the grouping sizes of the number groups as {@code int[]}, hereby starting from the decimal point.
     * So {@code 2, 2, 3} will format the number {@code 1234567890} as {@code 123.456.78.90}, assuming '.' as the only
     * grouping char. The grouping chars used can be similarly adapted/combined, see #GROUPING_GROUPING_SEPARATORS.
     */
    public static final String GROUPING_SIZES = "groupingSizes";

    /**
     * Allows to define the grouping characters of a number groups as {@code char[]}, hereby starting from the decimal point.
     * So {@code '''',':','.'} will format the number {@code 1234567890} as {@code 1'234:567.890}, assuming 3 as the only
     * grouping size. The grouping sizes used can be similarly adapted/combined, see #GROUPING_SIZES.
     */
    public static final String GROUPING_GROUPING_SEPARATORS = "groupingSeparators";
}

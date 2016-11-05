/*
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
package org.javamoney.moneta;

import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.money.MonetaryContext;
import javax.money.MonetaryContextBuilder;

import org.javamoney.moneta.spi.MonetaryConfig;

/**
 * Evaluates the default {@link MonetaryContext} to be used for {@link Money}.
 * The default {@link MonetaryContext} can be configured by adding a file
 * {@code /javamoney.properties} from the classpath with the following content:
 * <p>
 * <p>
 * <pre>
 * # Default MathContext for Money
 * #-------------------------------
 * # Custom MathContext, overrides entries from org.javamoney.moneta.Money.mathContext
 * # RoundingMode hereby is optional (default = HALF_EVEN)
 * org.javamoney.moneta.Money.defaults.precision=256
 * org.javamoney.moneta.Money.defaults.roundingMode=HALF_EVEN
 * </pre>
 * <p>
 * Hereby the roundingMode constants are the same as defined on
 * {@link RoundingMode}.
 */
class DefaultMonetaryContextFactory {
// TODO this should probably go to "convert" in future releases. Analyze feasability of refactoring.
    public MonetaryContext getContext() {
        try {
            Map<String, String> config = MonetaryConfig.getConfig();
            String value = config.get("org.javamoney.moneta.Money.defaults.precision");
            if (Objects.nonNull(value)) {
                return createMonetaryContextNonNullConfig(config, Integer.parseInt(value));
            } else {
                return createContextWithConfig(config);
            }
        } catch (Exception e) {
            Logger.getLogger(DefaultMonetaryContextFactory.class.getName())
                    .log(Level.SEVERE, "Error evaluating default NumericContext, using default (NumericContext.NUM64).", e);
            return MonetaryContextBuilder.of(Money.class).set(MathContext.DECIMAL64).build();
        }
    }

    private MonetaryContext createContextWithConfig(Map<String, String> config) {
        MonetaryContextBuilder builder = MonetaryContextBuilder.of(Money.class);
        String value = config.get("org.javamoney.moneta.Money.defaults.mathContext");
        if (Objects.nonNull(value)) {
            switch (value.toUpperCase(Locale.ENGLISH)) {
                case "DECIMAL32":
                    Logger.getLogger(Money.class.getName()).info(
                            "Using MathContext.DECIMAL32");
                    builder.set(MathContext.DECIMAL32);
                    break;
                case "DECIMAL64":
                    Logger.getLogger(Money.class.getName()).info(
                            "Using MathContext.DECIMAL64");
                    builder.set(MathContext.DECIMAL64);
                    break;
                case "DECIMAL128":
                    Logger.getLogger(Money.class.getName()).info(
                            "Using MathContext.DECIMAL128");
                    builder.set(MathContext.DECIMAL128);
                    break;
                case "UNLIMITED":
                    Logger.getLogger(Money.class.getName()).info(
                            "Using MathContext.UNLIMITED");
                    builder.set(MathContext.UNLIMITED);
                    break;
                default:
                    Logger.getLogger(Money.class.getName()).warning(
                            "Found invalid MathContext: " + value + ", using default MathContext.DECIMAL64");
                    builder.set(MathContext.DECIMAL64);
            }
        } else {
            Logger.getLogger(Money.class.getName()).info(
                    "Using default MathContext.DECIMAL64");
            builder.set(MathContext.DECIMAL64);
        }
        return builder.build();
    }

    private MonetaryContext createMonetaryContextNonNullConfig(Map<String, String> config, int prec) {
        String value = config.get("org.javamoney.moneta.Money.defaults.roundingMode");
        RoundingMode rm = Objects.nonNull(value) ? RoundingMode.valueOf(value
                .toUpperCase(Locale.ENGLISH)) : RoundingMode.HALF_UP;
        MonetaryContext mc = MonetaryContextBuilder.of(Money.class).setPrecision(prec).set(rm).set(Money.class).build();
        Logger.getLogger(DefaultMonetaryContextFactory.class.getName()).info("Using custom MathContext: precision=" + prec
                + ", roundingMode=" + rm);
        return mc;
    }
}

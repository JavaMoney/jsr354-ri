/*
  Copyright (c) 2012, 2015, Credit Suisse (Anatole Tresch), Werner Keil and others by the @author tag.
  <p>
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy of
  the License at
  <p>
  http://www.apache.org/licenses/LICENSE-2.0
  <p>
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  License for the specific language governing permissions and limitations under
  the License.
 */
package org.javamoney.moneta.format;

import static java.util.Objects.requireNonNull;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.format.AmountFormatContext;
import javax.money.format.AmountFormatContextBuilder;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryParseException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Objects;
import java.util.Optional;

import org.javamoney.moneta.function.MonetaryAmountProducer;

/**
 * The implementation that uses the {@link DecimalFormat} as formatter.
 *
 * @author Otavio Santana
 * @since 1.0.1
 */
public class MonetaryAmountDecimalFormat implements MonetaryAmountFormat {

    static final String STYLE = "MonetaryAmountFormatSymbols";

    private static final AmountFormatContext CONTEXT = AmountFormatContextBuilder.of(STYLE).build();

    private final DecimalFormat decimalFormat;

    private final MonetaryAmountProducer producer;

    private final CurrencyUnit currencyUnit;

    public MonetaryAmountDecimalFormat(DecimalFormat decimalFormat, MonetaryAmountProducer producer, CurrencyUnit currencyUnit) {
        this.decimalFormat = decimalFormat;
        this.producer = producer;
        this.currencyUnit = currencyUnit;
    }


    DecimalFormat getDecimalFormat() {
        return decimalFormat;
    }

    MonetaryAmountProducer getProducer() {
        return producer;
    }

    CurrencyUnit getCurrencyUnit() {
        return currencyUnit;
    }


    public String toLocalizedPattern() {
        return decimalFormat.toLocalizedPattern();
    }

    public String toPattern() {
        return decimalFormat.toPattern();
    }

    @Override
    public AmountFormatContext getContext() {
        return CONTEXT;
    }

    @Override
    public void print(Appendable appendable, MonetaryAmount amount) throws IOException {
        requireNonNull(appendable).append(queryFrom(amount));
    }

    @Override
    public MonetaryAmount parse(CharSequence text) throws MonetaryParseException {
        requireNonNull(text);
        try {
            Number number = decimalFormat.parse(text.toString());
            return producer.create(currencyUnit, number);
        } catch (Exception exception) {
            throw new MonetaryParseException(exception.getMessage(), text, 0);
        }
    }

    @Override
    public String queryFrom(MonetaryAmount amount) {
        return Optional
                .ofNullable(amount)
                .map(m -> decimalFormat.format(amount.getNumber().numberValue(
                        BigDecimal.class))).orElse("null");
    }

    @Override
    public int hashCode() {
        return Objects.hash(decimalFormat, currencyUnit, producer);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (MonetaryAmountDecimalFormat.class.isInstance(obj)) {
            MonetaryAmountDecimalFormat other = MonetaryAmountDecimalFormat.class.cast(obj);
            return Objects.equals(other.decimalFormat, decimalFormat) && Objects.equals(other.producer, producer)
                    && Objects.equals(other.currencyUnit, currencyUnit);
        }
        return false;
    }

    @Override
    public String toString() {
        return MonetaryAmountDecimalFormat.class.getName() + '{' +
                " decimalFormat: " + decimalFormat + ',' +
                " producer: " + producer + ',' +
                " currencyUnit: " + currencyUnit + '}';
    }
}

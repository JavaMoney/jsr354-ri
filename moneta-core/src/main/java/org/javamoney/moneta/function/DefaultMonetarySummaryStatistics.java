/**
 * Copyright (c) 2012, 2015, Anatole Tresch, Werner Keil and others by the @author tag.
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
package org.javamoney.moneta.function;

import java.util.Objects;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;

import org.javamoney.moneta.FastMoney;

/**
 * the default implementations of {@link MonetarySummaryStatistics} This
 * implementations cannot do exchange rate
 *
 * @author otaviojava
 * @author Anatole Tresch
 */
public class DefaultMonetarySummaryStatistics implements MonetarySummaryStatistics {

    private final MonetaryAmount empty;

    protected long count;

    protected MonetaryAmount min;

    protected MonetaryAmount max;

    protected MonetaryAmount sum;

    protected MonetaryAmount average;

    /**
     * Creates a new instance, targeting the given
     * {@link javax.money.CurrencyUnit}.
     *
     * @param currencyUnit the target currency, not null.
     */
    protected DefaultMonetarySummaryStatistics(CurrencyUnit currencyUnit) {
        empty = FastMoney.of(0, Objects.requireNonNull(currencyUnit));
        setSameMonetary(empty);
    }
    
    /**
     * Creates a new instance, targeting the given
     * {@link javax.money.CurrencyUnit}.
     *
     * @param currencyUnit the target currency, not null.
     */
    public static DefaultMonetarySummaryStatistics of(CurrencyUnit currencyUnit) {
    	return new DefaultMonetarySummaryStatistics(currencyUnit);
    }

    @Override
    public void accept(MonetaryAmount amount) {

        if (!empty.getCurrency().equals(
                Objects.requireNonNull(amount).getCurrency())) {
            return;
        }
        if (isEmpty()) {
            setSameMonetary(amount);
            count++;
        } else {
            doSummary(amount);
        }
    }

    @Override
    public CurrencyUnit getCurrencyUnit() {
        return empty.getCurrency();
    }

    @Override
    public MonetarySummaryStatistics combine(
            MonetarySummaryStatistics summaryStatistics) {
        Objects.requireNonNull(summaryStatistics);

        if (!equals(summaryStatistics)) {
            return this;
        }
        min = MonetaryFunctions.min(min, summaryStatistics.getMin());
        max = MonetaryFunctions.max(max, summaryStatistics.getMax());
        sum = sum.add(summaryStatistics.getSum());
        count += summaryStatistics.getCount();
        average = sum.divide(count);
        return this;
    }

    private void doSummary(MonetaryAmount monetaryAmount) {
        min = MonetaryFunctions.min(min, monetaryAmount);
        max = MonetaryFunctions.max(max, monetaryAmount);
        sum = sum.add(monetaryAmount);
        average = sum.divide(++count);
    }

    private boolean isEmpty() {
        return count == 0;
    }

    private void setSameMonetary(MonetaryAmount monetary) {
        min = monetary;
        max = monetary;
        sum = monetary;
        average = monetary;
    }


    @Override
    public long getCount() {
        return count;
    }

    @Override
    public MonetaryAmount getMin() {
        return min;
    }


    @Override
    public MonetaryAmount getMax() {
        return max;
    }


    @Override
    public MonetaryAmount getSum() {
        return sum;
    }


    @Override
    public MonetaryAmount getAverage() {
        return average;
    }

    @Override
    public boolean equals(Object obj) {
        if (DefaultMonetarySummaryStatistics.class.isInstance(obj)) {
            DefaultMonetarySummaryStatistics other = DefaultMonetarySummaryStatistics.class
                    .cast(obj);
            return Objects.equals(empty.getCurrency(),
                    other.empty.getCurrency());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return empty.getCurrency().hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[currency: ").append(empty.getCurrency()).append(',');
        sb.append("count:").append(count).append(',');
        sb.append("min:").append(min).append(',');
        sb.append("max:").append(max).append(',');
        sb.append("sum:").append(sum).append(',');
        sb.append("average:").append(average).append(']');
        return sb.toString();
    }

    @Override
    public boolean isExchangeable() {
        return false;
    }

    @Override
    public MonetarySummaryStatistics to(CurrencyUnit unit) {
        throw new UnsupportedOperationException(
                "the default implementation of MonetarySummaryStatistics cannot do exchange rate");
    }
}
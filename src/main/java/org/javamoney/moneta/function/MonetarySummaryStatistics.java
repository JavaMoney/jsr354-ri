package org.javamoney.moneta.function;

import java.util.Objects;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;

import org.javamoney.moneta.FastMoney;
import org.javamoney.moneta.spi.MoneyUtils;

/**
 * A state object for collecting statistics such as count, min, max, sum, and
 * average.
 * @author otaviojava
 * @author Anatole Tresch
 */
public class MonetarySummaryStatistics {

	private final MonetaryAmount empty;

	private long count;

	private MonetaryAmount min;

	private MonetaryAmount max;

	private MonetaryAmount sum;

	private MonetaryAmount avarage;

    /**
     * Creates a new instance, targeting the given {@link javax.money.CurrencyUnit}.
     *
     * @param currencyUnit the target currency, not null.
     */
    MonetarySummaryStatistics(CurrencyUnit currencyUnit) {
		empty = FastMoney.of(0, Objects.requireNonNull(currencyUnit));
		setSameMonetary(empty);
	}

	/**
	 * Records another value into the summary information.
     *
     * @param amount
     *            the input amount value to be addeed, not null.
     */
    public void accept(MonetaryAmount amount) {
        MoneyUtils.checkAmountParameter(amount, this.empty.getCurrency());
        if (isEmpty()) {
            setSameMonetary(amount);
            count++;
		} else {
            doSummary(amount);
        }
	}

	/**
	 * Combines the state of another {@code MonetarySummaryStatistics} into this
     * one.
     * @param summaryStatistics
     *            another {@code MonetarySummaryStatistics}, not null.
     */
    public MonetarySummaryStatistics combine(MonetarySummaryStatistics summaryStatistics) {
        Objects.requireNonNull(summaryStatistics);
        min = MonetaryFunctions.min(min, summaryStatistics.min);
        max = MonetaryFunctions.max(max, summaryStatistics.max);
        sum = sum.add(summaryStatistics.sum);
        count += summaryStatistics.count;
        avarage = sum.divide(count);
		return this;
	}

	private void doSummary(MonetaryAmount moneraty) {
		min = MonetaryFunctions.min(min, moneraty);
		max = MonetaryFunctions.max(max, moneraty);
		sum = sum.add(moneraty);
		avarage = sum.divide(++count);
	}

	private boolean isEmpty() {
		return count == 0;
	}

	private void setSameMonetary(MonetaryAmount monetary) {
		min = monetary;
		max = monetary;
		sum = monetary;
		avarage = monetary;
    }

    /**
     * Get the number of items added to this summary instance.
     * @return the number of summarized items, >= 0.
     */
    public long getCount() {
		return count;
    }

    /**
     * Get the minimal amount found within this summary.
     * @return the minimal amount, or null if no amount was added to this summary instance.
     */
    public MonetaryAmount getMin() {
		return min;
    }

    /**
     * Get the maximal amount found within this summary.
     * @return the minimal amount, or null if no amount was added to this summary instance.
     */
    public MonetaryAmount getMax() {
		return max;
    }

    /**
     * Get the sum of all amounts within this summary.
     * @return the total amount, or null if no amount was added to this summary instance.
     */
    public MonetaryAmount getSum() {
		return sum;
    }

    /**
     * Get the mean average of all amounts added.
     * @return the mean average amount, or null if no amount was added to this summary instance.
     */
    public MonetaryAmount getAvarage() {
		return avarage;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[count:").append(count).append(",");
		sb.append("min:").append(min).append(",");
		sb.append("max:").append(max).append(",");
		sb.append("sum:").append(sum).append(",");
		sb.append("avarage:").append(avarage).append("]");
		return sb.toString();
	}

}
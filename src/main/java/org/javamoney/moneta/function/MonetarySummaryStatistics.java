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
 */
public class MonetarySummaryStatistics {

	private final MonetaryAmount empty;

	private long count;

	private MonetaryAmount min;

	private MonetaryAmount max;

	private MonetaryAmount sum;

	private MonetaryAmount avarage;

	MonetarySummaryStatistics(CurrencyUnit currencyUnit) {
		empty = FastMoney.of(0, Objects.requireNonNull(currencyUnit));
		setSameMonetary(empty);
	}

	/**
	 * Records another value into the summary information.
	 *
	 * @param moneraty
	 *            the input value
	 */
	public void accept(MonetaryAmount moneraty) {
		MoneyUtils.checkAmountParameter(moneraty, this.empty.getCurrency());
		if (isEmpty()) {
			setSameMonetary(moneraty);
			count++;
		} else {
			doSummary(moneraty);
		}
	}

	/**
	 * Combines the state of another {@code MonetarySummaryStatistics} into this
	 * one.
	 * @param summary
	 *            another {@code MonetarySummaryStatistics}
	 * @throws NullPointerException
	 *             if {@code other} is null
	 */
	public MonetarySummaryStatistics combine(MonetarySummaryStatistics summary) {
		Objects.requireNonNull(summary);
		min = MonetaryFunctions.min(min, summary.min);
		max = MonetaryFunctions.max(max, summary.max);
		sum = sum.add(summary.sum);
		count += summary.count;
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

	public long getCount() {
		return count;
	}

	public MonetaryAmount getMin() {
		return min;
	}

	public MonetaryAmount getMax() {
		return max;
	}

	public MonetaryAmount getSum() {
		return sum;
	}

	public MonetaryAmount getAvarage() {
		return avarage;
	}

}
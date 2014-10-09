package org.javamoney.moneta.function;

import java.util.Objects;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.ExchangeRateProvider;

class ExchangeRateMonetarySummaryStatistics  extends DefaultMonetarySummaryStatistics {

	private ExchangeRateProvider provider;

	private CurrencyConversion currencyConversion;

	ExchangeRateMonetarySummaryStatistics(CurrencyUnit currencyUnit,
			ExchangeRateProvider provider) {
		super(currencyUnit);
		this.provider = provider;
		currencyConversion = provider.getCurrencyConversion(getCurrencyUnit());

	}

	@Override
	public void accept(MonetaryAmount amount) {
		super.accept(currencyConversion.apply(amount));
	}

	@Override
	public MonetarySummaryStatistics combine(
			MonetarySummaryStatistics summaryStatistics) {

		if (isDifferentCurrency(Objects.requireNonNull(summaryStatistics)
				.getCurrencyUnit())) {

			return super.combine(convert(summaryStatistics));

		} else {
			return super.combine(summaryStatistics);
		}
	}

	private MonetarySummaryStatistics convert(
			MonetarySummaryStatistics summaryStatistics) {

		if (summaryStatistics.isExchangeable()) {
			return summaryStatistics.to(getCurrencyUnit());
		}
		return create(summaryStatistics);
	}

	private MonetarySummaryStatistics create(MonetarySummaryStatistics summary) {

		ExchangeRateMonetarySummaryStatistics another = new ExchangeRateMonetarySummaryStatistics(
				getCurrencyUnit(), provider);

		another.average = currencyConversion.apply(summary
				.getAverage());
		another.count = summary.getCount();
		another.max = currencyConversion.apply(summary.getMax());
		another.min = currencyConversion.apply(summary.getMin());
		another.sum = currencyConversion.apply(summary.getSum());
		return another;
	}

	private boolean isDifferentCurrency(CurrencyUnit unit) {
		return !getCurrencyUnit().equals(unit);
	}

	@Override
	public boolean isExchangeable() {
		return true;
	}

	@Override
	public MonetarySummaryStatistics to(CurrencyUnit unit) {
		CurrencyConversion currencyConversion = provider.getCurrencyConversion(unit);
		ExchangeRateMonetarySummaryStatistics another = new ExchangeRateMonetarySummaryStatistics(
				unit, provider);
		another.average = currencyConversion.apply(average);
		another.count = count;
		another.max = currencyConversion.apply(max);
		another.min = currencyConversion.apply(min);
		another.sum = currencyConversion.apply(sum);
		return another;
	}

}
package org.javamoney.moneta.function;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;

public class GroupMonetarySummaryStatistics {

	private Map<CurrencyUnit, MonetarySummaryStatistics> groupSummary = new HashMap<>();

	GroupMonetarySummaryStatistics() {

	}

	public Map<CurrencyUnit, MonetarySummaryStatistics> get() {
		return groupSummary;
	}

	public GroupMonetarySummaryStatistics accept(MonetaryAmount amount) {
		CurrencyUnit currency = Objects.requireNonNull(amount).getCurrency();
		groupSummary.putIfAbsent(currency, new MonetarySummaryStatistics(
				currency));
		MonetarySummaryStatistics summary = groupSummary.get(currency);
		summary.accept(amount);
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(groupSummary);
	}

	@Override
	public boolean equals(Object obj) {
		if (GroupMonetarySummaryStatistics.class.isInstance(obj)) {
			GroupMonetarySummaryStatistics other = GroupMonetarySummaryStatistics.class
					.cast(obj);
			return Objects.equals(groupSummary, other.groupSummary);
		}
		return false;
	}
	@Override
	public String toString() {
		return "GroupMonetarySummaryStatistics: " + groupSummary.toString();
	}

	public GroupMonetarySummaryStatistics combine(
			GroupMonetarySummaryStatistics another) {
		Objects.requireNonNull(another);

		for (CurrencyUnit keyCurrency: another.groupSummary.keySet()) {
			groupSummary.putIfAbsent(keyCurrency,
					new MonetarySummaryStatistics(keyCurrency));
			groupSummary.merge(keyCurrency, another.groupSummary.get(keyCurrency), MonetarySummaryStatistics::combine);
		}
		return this;
	}

}

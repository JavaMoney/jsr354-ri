package org.javamoney.moneta.function;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryException;

import org.javamoney.moneta.spi.MoneyUtils;

/**
 * This singleton class provides access to the predefined monetary functions.
 *
 * @author otaviojava
 * @author anatole
 */
public final class MonetaryFunctions {


    /**
     * Collector to group by CurrencyUnit
     *
     * @return the Collector to of Map<CurrencyUnit, List<MonetaryAmount>>
     */
    public static Collector<MonetaryAmount,?,Map<CurrencyUnit,List<MonetaryAmount>>> groupByCurrencyUnit(){
        return Collectors.groupingBy(MonetaryAmount::getCurrency);
    }

    /**
     * of the summary of the MonetaryAmount
     *
     * @param currencyUnit the target {@link javax.money.CurrencyUnit}
     * @return the MonetarySummaryStatistics
     */
    public static Collector<MonetaryAmount,MonetarySummaryStatistics,MonetarySummaryStatistics> summarizingMonetary(
            CurrencyUnit currencyUnit){
        Supplier<MonetarySummaryStatistics> supplier = () -> new MonetarySummaryStatistics(currencyUnit);
        return Collector.of(supplier, MonetarySummaryStatistics::accept, MonetarySummaryStatistics::combine);
    }

    /**
     * of MonetaryAmount group by MonetarySummary
     *
     * @return the MonetarySummaryStatistics
     */
    public static Collector<MonetaryAmount,GroupMonetarySummaryStatistics,GroupMonetarySummaryStatistics>
    groupBySummarizingMonetary(){
        return Collector.of(GroupMonetarySummaryStatistics::new, GroupMonetarySummaryStatistics::accept,
                            GroupMonetarySummaryStatistics::combine);
    }

    /**
     * Get a comparator for sorting CurrencyUnits ascending.
     *
     * @return the Comparator to sort by CurrencyUnit in ascending order, not null.
     */
    public static Comparator<MonetaryAmount> sortCurrencyUnit(){
        return Comparator.comparing(MonetaryAmount::getCurrency);
    }

    /**
     * Get a comparator for sorting CurrencyUnits descending.
     *
     * @return the Comparator to sort by CurrencyUnit in descending order, not null.
     */
    public static Comparator<MonetaryAmount> sortCurrencyUnitDesc(){
        return sortCurrencyUnit().reversed();
    }

    /**
     * Get a comparator for sorting amount by number value ascending.
     *
     * @return the Comparator to sort by number in ascending way, not null.
     */
    public static Comparator<MonetaryAmount> sortNumber(){
        return Comparator.comparing(MonetaryAmount::getNumber);
    }

    /**
     * Get a comparator for sorting amount by number value descending.
     *
     * @return the Comparator to sort by number in descending way, not null.
     */
    public static Comparator<MonetaryAmount> sortNumberDesc(){
        return sortNumber().reversed();
    }

    /**
	 * Create predicate that filters by CurrencyUnit.
	 * @param currencies
	 *            the target {@link javax.money.CurrencyUnit}
	 * @return the predicate from CurrencyUnit
	 */
	public static Predicate<MonetaryAmount> isCurrency(
			CurrencyUnit... currencies) {

		if (Objects.isNull(currencies) || currencies.length == 0) {
			return m -> true;
		}
		Predicate<MonetaryAmount> predicate = null;

		for (CurrencyUnit currencyUnit : currencies) {
			if (Objects.isNull(predicate)) {
				predicate = m -> m.getCurrency().equals(currencyUnit);
			} else {
				predicate = predicate.or(m -> m.getCurrency().equals(
						currencyUnit));
			}
		}
		return predicate;
    }

    /**
     * Create predicate that filters by CurrencyUnit.
     *
     * @param currencyUnit the target {@link javax.money.CurrencyUnit}
     * @return the predicate from CurrencyUnit
     */
	public static Predicate<MonetaryAmount> fiterByExcludingCurrency(
			CurrencyUnit... currencies) {

		if (Objects.isNull(currencies) || currencies.length == 0) {
			return m -> true;
		}
		return isCurrency(currencies).negate();
    }

    /**
     * Creates filter using isGreaterThan in MonetaryAmount.
     *
     * @param amount
     * @return the filter with isGreaterThan conditions
     */
    public static Predicate<MonetaryAmount> isGreaterThan(MonetaryAmount amount){
        return m -> m.isGreaterThan(amount);
    }

    /**
     * Creates filter using isGreaterThanOrEqualTo in MonetaryAmount
     *
     * @param amount
     * @return the filter with isGreaterThanOrEqualTo conditions
     */
    public static Predicate<MonetaryAmount> isGreaterThanOrEqualTo(MonetaryAmount amount){
        return m -> m.isGreaterThanOrEqualTo(amount);
    }

    /**
     * Creates filter using isLessThan in MonetaryAmount
     *
     * @param amount
     * @return the filter with isLessThan conditions
     */
    public static Predicate<MonetaryAmount> isLessThan(MonetaryAmount amount){
        return m -> m.isLessThan(amount);
    }

    /**
     * Creates filter using isLessThanOrEqualTo in MonetaryAmount
     *
     * @param amount
     * @return the filter with isLessThanOrEqualTo conditions
     */
    public static Predicate<MonetaryAmount> isLessThanOrEqualTo(MonetaryAmount amount){
        return m -> m.isLessThanOrEqualTo(amount);
    }

    /**
     * Creates a filter using the isBetween predicate.
     *
     * @param min min value inclusive, not null.
     * @param max max value inclusive, not null.
     * @return the Predicate between min and max.
     */
    public static Predicate<MonetaryAmount> isBetween(MonetaryAmount min, MonetaryAmount max){
        return isLessThanOrEqualTo(max).and(isGreaterThanOrEqualTo(min));
    }

    /**
     * Adds two monetary together
     *
     * @param a the first operand
     * @param b the second operand
     * @return the sum of {@code a} and {@code b}
     * @throws NullPointerException if a o b be null
     * @throws MonetaryException    if a and b have different currency
     */
    public static MonetaryAmount sum(MonetaryAmount a, MonetaryAmount b){
        MoneyUtils.checkAmountParameter(Objects.requireNonNull(a), Objects.requireNonNull(b.getCurrency()));
        return a.add(b);
    }

    /**
     * Returns the smaller of two {@code MonetaryAmount} values. If the arguments
     * have the same value, the result is that same value.
     *
     * @param a an argument.
     * @param b another argument.
     * @return the smaller of {@code a} and {@code b}.
     */
    public static MonetaryAmount min(MonetaryAmount a, MonetaryAmount b){
        MoneyUtils.checkAmountParameter(Objects.requireNonNull(a), Objects.requireNonNull(b.getCurrency()));
        return a.isLessThan(b) ? a : b;
    }

    /**
     * Returns the greater of two {@code MonetaryAmount} values. If the
     * arguments have the same value, the result is that same value.
     *
     * @param a an argument.
     * @param b another argument.
     * @return the larger of {@code a} and {@code b}.
     */
    public static MonetaryAmount max(MonetaryAmount a, MonetaryAmount b){
        MoneyUtils.checkAmountParameter(Objects.requireNonNull(a), Objects.requireNonNull(b.getCurrency()));
        return a.isGreaterThan(b) ? a : b;
    }

    /**
     * Creates a BinaryOperator to sum.
     *
     * @return the sum BinaryOperator, not null.
     */
    public static BinaryOperator<MonetaryAmount> sum(){
        return MonetaryFunctions::sum;
    }

    /**
	 * Creates a BinaryOperator to calculate the minimum amount
	 *
	 * @return the min BinaryOperator, not null.
	 */
    public static BinaryOperator<MonetaryAmount> min(){
        return MonetaryFunctions::min;
    }

    /**
	 * Creates a BinaryOperator to calculate the maximum amount.
	 *
	 * @return the max BinaryOperator, not null.
	 */
    public static BinaryOperator<MonetaryAmount> max(){
        return MonetaryFunctions::max;
    }
}
package org.javamoney.moneta.function;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryException;

import org.javamoney.moneta.spi.MoneyUtils;

/**
 * This singleton class provides access to the predefined monetary functions.
 * @author otaviojava
 */
public final class MonetaryFunctions {


		public static Collector<MonetaryAmount, ?, Map<CurrencyUnit, List<MonetaryAmount>>> groupByCurrencyUnit() {
			return Collectors.groupingBy(MonetaryAmount::getCurrency);
		}
		/**
		 * sort ascending CurrencyUnit
		 * @return the Comparator sort by CurrencyUnit in ascending way
		 */
		public static  Comparator<MonetaryAmount> sortCurrencyUnit() {
			return Comparator.comparing(MonetaryAmount::getCurrency);
		}
		/**
		 * sort descending CurrencyUnit
		 * @return the Comparator to sort by CurrencyUnit in descending way
		 */
		public static  Comparator<MonetaryAmount> sortCurrencyUnitDesc() {
			return sortCurrencyUnit().reversed();
		}

		/**
		 * sort ascending CurrencyUnit
		 * @return the Comparator to sort by NumberValue in ascending way
		 */
		public static  Comparator<MonetaryAmount> sortNumber() {
			return Comparator.comparing(MonetaryAmount::getNumber);
		}
		/**
		 * sort descending CurrencyUnit
		 * @return the Comparator to order by NumberValue in descending way
		 */
		public static  Comparator<MonetaryAmount> sortNumberDesc() {
			return sortNumber().reversed();
		}

		/**
		 * Filter by CurrencyUnit
		 * @param unit
		 * @return the predicate from CurrencyUnit
		 */
		public static Predicate<MonetaryAmount> isCurrency(CurrencyUnit unit) {
			return  m -> m.getCurrency().equals(unit);
		}
		/**
		 * Filter by is not CurrencyUnit
		 * @param unit
		 * @return the predicate that is not the CurrencyUnit
		 */
		public static Predicate<MonetaryAmount> isNotCurrency(CurrencyUnit unit) {
			return  isCurrency(unit).negate();
		}
		/**
		 * Filter by CurrencyUnits
		 * @param a - first CurrencyUnit
		 * @param units - the another units
		 * @return the predicate in
		 */
		public static Predicate<MonetaryAmount> containsCurrencies(CurrencyUnit a, CurrencyUnit... units) {
			Predicate<MonetaryAmount> inPredicate = isCurrency(a);
			for (CurrencyUnit unit: units) {
				inPredicate = inPredicate.or(isCurrency(unit));
			}
			return  inPredicate;
		}
		/**
		 * Filter using isGreaterThan in MonetaryAmount
		 * @param amount
		 * @return the filter with isGreaterThan conditions
		 */
		public static Predicate<MonetaryAmount> isGreaterThan(MonetaryAmount amount){
			return m -> m.isGreaterThan(amount);
		}
		/**
		 * Filter using isGreaterThanOrEqualTo in MonetaryAmount
		 * @param amount
		 * @return the filter with isGreaterThanOrEqualTo conditions
		 */
		public static Predicate<MonetaryAmount> isGreaterThanOrEqualTo(MonetaryAmount amount){
			return m -> m.isGreaterThanOrEqualTo(amount);
		}
		/**
		 * Filter using isLessThan in MonetaryAmount
		 * @param amount
		 * @return the filter with isLessThan conditions
		 */
		public  static Predicate<MonetaryAmount> isLessThan(MonetaryAmount amount){
			return m -> m.isLessThan(amount);
		}
		/**
		 * Filter using isLessThanOrEqualTo in MonetaryAmount
		 * @param amount
		 * @return the filter with isLessThanOrEqualTo conditions
		 */
		public static Predicate<MonetaryAmount> isLessThanOrEqualTo(MonetaryAmount amount){
			return m -> m.isLessThanOrEqualTo(amount);
		}
		/**
		 * Filter using the isBetween predicate
		 * @param min - min value inclusive
		 * @param max - max value inclusive
		 * @return - the Predicate between min and max
		 */
		public static Predicate<MonetaryAmount> isBetween(MonetaryAmount min, MonetaryAmount max){
			return isLessThanOrEqualTo(max).and(isGreaterThanOrEqualTo(min));
		}

	/**
	 * Adds two monetary together
	 * @param a the first operand
	 * @param b the second operand
	 * @return the sum of {@code a} and {@code b}
	 * @throws NullPointerException if a o b be null
	 * @throws MonetaryException if a and b have different currency
	 */
	public static MonetaryAmount sum(MonetaryAmount a, MonetaryAmount b) {
		MoneyUtils.checkAmountParameter(Objects.requireNonNull(a),
				Objects.requireNonNull(b.getCurrency()));
		return a.add(b);
	}

	/**
	 * Returns the smaller of two {MonetaryAmount int} values. If the arguments
	 * have the same value, the result is that same value.
	 * @param a an argument.
	 * @param b another argument.
	 * @return the smaller of {@code a} and {@code b}.
	 */
	public static MonetaryAmount min(MonetaryAmount a, MonetaryAmount b) {
		MoneyUtils.checkAmountParameter(Objects.requireNonNull(a),
				Objects.requireNonNull(b.getCurrency()));
		return a.isLessThan(b) ? a : b;
	}

	/**
	 * Returns the greater of two {@code MonetaryAmount} values. If the
	 * arguments have the same value, the result is that same value.
	 * @param a an argument.
	 * @param b another argument.
	 * @return the larger of {@code a} and {@code b}.
	 */
	public static MonetaryAmount max(MonetaryAmount a, MonetaryAmount b) {
		MoneyUtils.checkAmountParameter(Objects.requireNonNull(a),
				Objects.requireNonNull(b.getCurrency()));
		return a.isGreaterThan(b) ? a : b;
	}
}
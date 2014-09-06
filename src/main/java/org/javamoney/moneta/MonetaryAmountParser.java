package org.javamoney.moneta;

import java.math.BigDecimal;
import java.util.Objects;

import javax.money.CurrencyUnit;
import javax.money.MonetaryCurrencies;

/**
 * class utils to standardize the toString method and parse to monetary Amount
 * @author otaviojava
 */
class MonetaryAmountParser {

	public static Money parserMoney(CharSequence text) {
		ParserMonetaryAmount parserRestul = parserMonetaryAmount(text);
		return Money.of(parserRestul.number, parserRestul.currencyUnit);
	}

	public static RoundedMoney parserRoundedMoney(CharSequence text) {
		ParserMonetaryAmount parserRestul = parserMonetaryAmount(text);
		return RoundedMoney.of(parserRestul.number, parserRestul.currencyUnit);
	}

	public static FastMoney parserFastMoney(CharSequence text) {
		ParserMonetaryAmount parserRestul = parserMonetaryAmount(text);
		return FastMoney.of(parserRestul.number, parserRestul.currencyUnit);
	}

	private static ParserMonetaryAmount parserMonetaryAmount(CharSequence text) {
		String[] array = Objects.requireNonNull(text).toString().split(" ");
		CurrencyUnit currencyUnit = MonetaryCurrencies.getCurrency(array[0]);
		BigDecimal number = new BigDecimal(array[1]);
		ParserMonetaryAmount parserRestul = new ParserMonetaryAmount(currencyUnit, number);
		return parserRestul;
	}

	private static class ParserMonetaryAmount {
		public ParserMonetaryAmount(CurrencyUnit currencyUnit, BigDecimal number) {
			this.currencyUnit = currencyUnit;
			this.number = number;
		}
		private CurrencyUnit currencyUnit;
		private BigDecimal number;
	}
}

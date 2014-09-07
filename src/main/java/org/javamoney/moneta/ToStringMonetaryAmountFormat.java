package org.javamoney.moneta;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryCurrencies;
import javax.money.format.AmountFormatContext;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryParseException;

/**
 * class to format and parse a text string such as 'EUR 25.25' or vice versa.
 * @author otaviojava
 */
class ToStringMonetaryAmountFormat implements MonetaryAmountFormat {

	private ToStringMonetaryAmountFormatStyle style;

	private ToStringMonetaryAmountFormat(ToStringMonetaryAmountFormatStyle style) {
		this.style = Objects.requireNonNull(style);
	}

	public static ToStringMonetaryAmountFormat of(
			ToStringMonetaryAmountFormatStyle style) {
		return new ToStringMonetaryAmountFormat(style);
	}

	@Override
	public String queryFrom(MonetaryAmount amount) {
		if (Objects.isNull(amount)) {
			return null;
		}
		return amount.toString();
	}

	@Override
	public AmountFormatContext getAmountFormatContext() {
		throw new UnsupportedOperationException(
				"ToStringMonetaryAmountFormat does not the method suport getAmountFormatContext()");
	}

	@Override
	public void print(Appendable appendable, MonetaryAmount amount)
			throws IOException {
		appendable.append(Optional.ofNullable(amount)
				.map(MonetaryAmount::toString).orElse("null"));

	}

	@Override
	public MonetaryAmount parse(CharSequence text)
			throws MonetaryParseException {
		ParserMonetaryAmount amount = parserMonetaryAmount(text);
		return style.to(amount);
	}

	private ParserMonetaryAmount parserMonetaryAmount(CharSequence text) {
		String[] array = Objects.requireNonNull(text).toString().split(" ");
		CurrencyUnit currencyUnit = MonetaryCurrencies.getCurrency(array[0]);
		BigDecimal number = new BigDecimal(array[1]);
		return new ParserMonetaryAmount(currencyUnit, number);
	}
	private class ParserMonetaryAmount {
		public ParserMonetaryAmount(CurrencyUnit currencyUnit, BigDecimal number) {
			this.currencyUnit = currencyUnit;
			this.number = number;
		}

		private CurrencyUnit currencyUnit;
		private BigDecimal number;
	}

	/**
	 * indicates with implementation will used to format or parser in
	 * ToStringMonetaryAmountFormat
	 */
	enum ToStringMonetaryAmountFormatStyle {
		MONEY {
			@Override
			MonetaryAmount to(ParserMonetaryAmount amount) {
				return Money.of(amount.number, amount.currencyUnit);
			}
		},
		FAST_MONEY {
			@Override
			MonetaryAmount to(ParserMonetaryAmount amount) {
				return FastMoney.of(amount.number, amount.currencyUnit);
			}
		},
		ROUNDED_MONEY {
			@Override
			MonetaryAmount to(ParserMonetaryAmount amount) {
				return RoundedMoney.of(amount.number, amount.currencyUnit);
			}
		};
		abstract MonetaryAmount to(ParserMonetaryAmount amount);
	}

}

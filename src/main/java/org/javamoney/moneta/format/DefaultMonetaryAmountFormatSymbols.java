package org.javamoney.moneta.format;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Objects;
import java.util.Optional;

import javax.money.MonetaryAmount;
import javax.money.MonetaryAmountFactory;
import javax.money.format.AmountFormatContext;
import javax.money.format.AmountFormatContextBuilder;
import javax.money.format.MonetaryParseException;

class DefaultMonetaryAmountFormatSymbols implements MonetaryAmountFormatSymbols {

	static final String STYLE = "MonetaryAmountFormatSymbols";

	private static final AmountFormatContext CONTEXT = AmountFormatContextBuilder.of(STYLE).build();

	private final MonetaryAmountSymbols symbols;

	private final DecimalFormat decimalFormat;

	private final MonetaryAmountFactory<?> factory;

	DefaultMonetaryAmountFormatSymbols(MonetaryAmountSymbols symbols, MonetaryAmountFactory<?> factory) {
		this.symbols = symbols;
		this.factory = factory;
		this.decimalFormat = (DecimalFormat) DecimalFormat.getCurrencyInstance();
		decimalFormat.setDecimalFormatSymbols(symbols.getFormatSymbol());
	}

	@Override
	public AmountFormatContext getContext() {
		return CONTEXT;
	}

	@Override
	public void print(Appendable appendable, MonetaryAmount amount)
			throws IOException {

		Objects.requireNonNull(appendable);
		appendable.append(queryFrom(amount));
	}

	@Override
	public MonetaryAmount parse(CharSequence text)
			throws MonetaryParseException {
		Objects.requireNonNull(text);
		try {
			Number number = decimalFormat.parse(text.toString());
			return factory.setCurrency(symbols.getCurrency()).setNumber(number).create();
		}catch (Exception exception) {
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
	public MonetaryAmountSymbols getAmountSymbols() {
		return symbols;
	}

}

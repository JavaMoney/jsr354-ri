package org.javamoney.moneta.function;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.OptionalInt;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;

class RoudingMonetaryAmountOperator implements MonetaryOperator {

	static final RoundingMode DEFAULT_ROUDING_MONETARY_AMOUNT = RoundingMode.HALF_EVEN;

	private final RoundingMode roundingMode;

	private final OptionalInt scaleOptional;

	public RoudingMonetaryAmountOperator() {
		this.roundingMode = DEFAULT_ROUDING_MONETARY_AMOUNT;
		scaleOptional = OptionalInt.empty();
	}

	public RoudingMonetaryAmountOperator(RoundingMode roundingMode) {
		this.roundingMode = roundingMode;
		scaleOptional = OptionalInt.empty();
	}

	public RoudingMonetaryAmountOperator(RoundingMode roundingMode, int scale) {
		this.roundingMode = roundingMode;
		this.scaleOptional = OptionalInt.of(scale);

	}

	@Override
	public MonetaryAmount apply(MonetaryAmount amount) {
		Objects.requireNonNull(amount, "Amount required.");
		CurrencyUnit currency = amount.getCurrency();
		int scale = scaleOptional.orElse(currency.getDefaultFractionDigits());
		BigDecimal value = amount.getNumber().numberValue(BigDecimal.class).setScale(scale, roundingMode);
		return amount.getFactory().setNumber(value).create();
	}

}

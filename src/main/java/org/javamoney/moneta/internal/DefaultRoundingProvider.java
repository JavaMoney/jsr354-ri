package org.javamoney.moneta.internal;

import java.math.RoundingMode;
import java.util.Collections;
import java.util.Set;

import javax.money.CurrencyUnit;
import javax.money.MonetaryContext;
import javax.money.MonetaryOperator;
import javax.money.spi.RoundingProviderSpi;

public class DefaultRoundingProvider implements
		RoundingProviderSpi {

	@Override
	public MonetaryOperator getRounding(CurrencyUnit currency) {
		return new DefaultRounding(currency);
	}

	@Override
	public MonetaryOperator getRounding(CurrencyUnit currency,
			long timestamp) {
		return null;
	}

	@Override
	public MonetaryOperator getCashRounding(CurrencyUnit currency) {
		return new DefaultCashRounding(currency);
	}

	@Override
	public MonetaryOperator getCashRounding(CurrencyUnit currency,
			long timestamp) {
		return null;
	}

	@Override
	public Set<String> getCustomRoundingIds() {
		return Collections.emptySet();
	}

	@Override
	public MonetaryOperator getCustomRounding(String customRoundingId) {
		throw new IllegalArgumentException("No such custom rounding: "
				+ customRoundingId);
	}

	@Override
	public MonetaryOperator getRounding(MonetaryContext monetaryContext) {
		RoundingMode rm = monetaryContext.getAttribute(RoundingMode.class,
				RoundingMode.HALF_EVEN);
		return new DefaultRounding(monetaryContext.getMaxScale(), rm);
	}

}
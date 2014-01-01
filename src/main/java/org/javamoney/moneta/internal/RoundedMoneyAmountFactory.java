package org.javamoney.moneta.internal;

import java.math.RoundingMode;

import javax.money.CurrencyUnit;
import javax.money.MonetaryContext;
import javax.money.MonetaryContext.AmountFlavor;

import org.javamoney.moneta.RoundedMoney;
import org.javamoney.moneta.spi.AbstractAmountFactory;

public class RoundedMoneyAmountFactory extends
		AbstractAmountFactory<RoundedMoney> {

	/*
	 * (non-Javadoc)
	 * @see javax.money.MonetaryAmountFactory#getQueryInclusionPolicy()
	 */
	@Override
	public QueryInclusionPolicy getQueryInclusionPolicy() {
		return QueryInclusionPolicy.DIRECT_REFERENCE_ONLY;
	}

	/*
	 * (non-Javadoc)
	 * @see org.javamoney.moneta.spi.AbstractAmountFactory#create(javax.money.CurrencyUnit,
	 * java.lang.Number, javax.money.MonetaryContext)
	 */
	@Override
	protected RoundedMoney create(CurrencyUnit currency, Number number,
			MonetaryContext monetaryContext) {
		return RoundedMoney.of(currency, number);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.money.MonetaryAmountFactory#getAmountType()
	 */
	@Override
	public Class<RoundedMoney> getAmountType() {
		return RoundedMoney.class;
	}

	/*
	 * (non-Javadoc)
	 * @see org.javamoney.moneta.spi.AbstractAmountFactory#loadDefaultMonetaryContext()
	 */
	@Override
	protected MonetaryContext loadDefaultMonetaryContext() {
		return new MonetaryContext.Builder(RoundedMoney.class).setPrecision(0)
				.setAttribute(RoundingMode.HALF_EVEN)
				.setFlavor(AmountFlavor.UNDEFINED).build();
	}

	/*
	 * (non-Javadoc)
	 * @see org.javamoney.moneta.spi.AbstractAmountFactory#loadMaxMonetaryContext()
	 */
	@Override
	protected MonetaryContext loadMaxMonetaryContext() {
		return new MonetaryContext.Builder(RoundedMoney.class).setPrecision(0)
				.setAttribute(RoundingMode.HALF_EVEN)
				.setFlavor(AmountFlavor.UNDEFINED).build();
	}

}

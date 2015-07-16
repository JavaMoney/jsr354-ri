package org.javamoney.moneta;

import javax.money.CurrencyContext;
import javax.money.CurrencyUnit;

public class InvalidCurrency implements CurrencyUnit {

	@Override
	public int compareTo(CurrencyUnit o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCurrencyCode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumericCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getDefaultFractionDigits() {
		return -1;
	}

	@Override
	public CurrencyContext getContext() {
		// TODO Auto-generated method stub
		return null;
	}

}

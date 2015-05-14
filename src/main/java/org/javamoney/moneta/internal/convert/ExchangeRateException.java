package org.javamoney.moneta.internal.convert;

import javax.money.MonetaryException;

public class ExchangeRateException extends MonetaryException {

	private static final long serialVersionUID = 1L;

	public ExchangeRateException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExchangeRateException(String message) {
		super(message);
	}
}

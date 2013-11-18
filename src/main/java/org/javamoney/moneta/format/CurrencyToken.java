/*
 * Copyright (c) 2012, 2013, Credit Suisse (Anatole Tresch), Werner Keil.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.javamoney.moneta.format;

import java.io.IOException;
import java.text.ParseException;
import java.util.Currency;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;

import org.javamoney.moneta.MoneyCurrency;

/**
 * {@link FormatToken} that adds a localizable {@link String}, read by key from
 * a {@link ResourceBundle}..
 * 
 * @author Anatole Tresch
 * 
 * @param <T>
 *            The concrete type.
 */
final class CurrencyToken implements FormatToken {

	private CurrencyStyle style = CurrencyStyle.CODE;
	private Locale locale;

	public CurrencyToken(CurrencyStyle style, Locale locale) {
		this.locale = locale;
	}

	public CurrencyToken setDisplayType(CurrencyStyle displayType) {
		if (displayType == null) {
			throw new IllegalArgumentException("Display type null.");
		}
		this.style = displayType;
		return this;
	}

	public CurrencyStyle getDisplayType() {
		return this.style;
	}

	protected String getToken(MonetaryAmount amount) {
		switch (style) {
		case NUMERIC_CODE:
			return String.valueOf(MoneyCurrency.from(amount.getCurrency())
					.getNumericCode());
		case NAME:
			return getCurrencyName(amount.getCurrency());
		case SYMBOL:
			return getCurrencySymbol(amount.getCurrency());
		default:
		case CODE:
			return amount.getCurrency().getCurrencyCode();
		}
	}

	private String getCurrencyName(CurrencyUnit currency) {
		if (MoneyCurrency.isJavaCurrency(currency.getCurrencyCode())) {
			Currency cur = Currency.getInstance(currency.getCurrencyCode());
			return cur.getDisplayName(locale);
		}
		return currency.getCurrencyCode();
	}

	private String getCurrencySymbol(CurrencyUnit currency) {
		if (MoneyCurrency.isJavaCurrency(currency.getCurrencyCode())) {
			Currency cur = Currency.getInstance(currency.getCurrencyCode());
			return cur.getSymbol(locale);
		}
		return currency.getCurrencyCode();
	}

	@Override
	public void parse(ParseContext context)
			throws ParseException {
		String token = context.lookupNextToken();
		while (token != null) {
			if (token.trim().isEmpty()) {
				context.consume(token);
				token = context.lookupNextToken();
				continue;
			}
			break;
		}
		try {
			MoneyCurrency cur = null;
			switch (style) {
			case CODE:
				cur = MoneyCurrency.of(token);
				context.consume(token);
				break;
			case SYMBOL:
				if (token.startsWith("$")) {
					cur = MoneyCurrency.of("USD");
					context.consume("$");
				}
				else if (token.startsWith("€")) {
					cur = MoneyCurrency.of("EUR");
					context.consume("€");
				}
				else if (token.startsWith("£")) {
					cur = MoneyCurrency.of("GBP");
					context.consume("£");
				}
			case NAME:
			case NUMERIC_CODE:
			default:
				throw new UnsupportedOperationException("Not yet implemented");
			}
			if (cur != null) {
				context.setParsedCurrency(cur);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void print(Appendable appendable, MonetaryAmount amount)
			throws IOException {
		appendable.append(getToken(amount));
	}
}

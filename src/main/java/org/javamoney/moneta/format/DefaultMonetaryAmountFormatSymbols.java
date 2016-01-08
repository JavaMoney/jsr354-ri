/**
 * Copyright (c) 2012, 2015, Credit Suisse (Anatole Tresch), Werner Keil and others by the @author tag.
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
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Objects;
import java.util.Optional;

import javax.money.MonetaryAmount;
import javax.money.format.AmountFormatContext;
import javax.money.format.AmountFormatContextBuilder;
import javax.money.format.MonetaryParseException;

import org.javamoney.moneta.spi.MonetaryAmountProducer;

/**
 * The default implementation that uses the {@link DecimalFormat} as formatter.
 * @author Otavio Santana
 */
class DefaultMonetaryAmountFormatSymbols implements MonetaryAmountFormatSymbols {

	static final String STYLE = "MonetaryAmountFormatSymbols";

	private static final AmountFormatContext CONTEXT = AmountFormatContextBuilder.of(STYLE).build();

	private final MonetaryAmountSymbols symbols;

	private final DecimalFormat decimalFormat;

	private final MonetaryAmountProducer producer;

	private final MonetaryAmountNumericInformation numericInformation;

	DefaultMonetaryAmountFormatSymbols(MonetaryAmountSymbols symbols, MonetaryAmountProducer producer) {
		this.symbols = symbols;
		this.producer = producer;
		this.decimalFormat = (DecimalFormat) DecimalFormat.getCurrencyInstance();
		decimalFormat.setDecimalFormatSymbols(symbols.getFormatSymbol());
		numericInformation = new MonetaryAmountNumericInformation(decimalFormat);
	}

	DefaultMonetaryAmountFormatSymbols(String pattern, MonetaryAmountSymbols symbols, MonetaryAmountProducer producer) {
		this.symbols = symbols;
		this.producer = producer;
		this.decimalFormat = new DecimalFormat(pattern, symbols.getFormatSymbol());
		numericInformation = new MonetaryAmountNumericInformation(decimalFormat);
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
			return producer.create(symbols.getCurrency(), number);
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

	@Override
	public MonetaryAmountNumericInformation getNumericInformation() {
		return numericInformation;
	}

	@Override
	public int hashCode() {
		return Objects.hash(symbols, numericInformation);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		if (DefaultMonetaryAmountFormatSymbols.class.isInstance(obj)) {
			DefaultMonetaryAmountFormatSymbols other = DefaultMonetaryAmountFormatSymbols.class.cast(obj);
			return Objects.equals(other.symbols, symbols) && Objects.equals(other.numericInformation, numericInformation);
		}
		return false;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(DefaultMonetaryAmountFormatSymbols.class.getName()).append('{')
		.append(" numericInformation: ").append(numericInformation).append(',')
		.append(" symbols: ").append(symbols).append('}');
		return sb.toString();
	}
}
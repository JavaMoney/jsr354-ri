/**
 * Copyright (c) 2012, 2014, Credit Suisse (Anatole Tresch), Werner Keil and others by the @author tag.
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
package org.javamoney.moneta.format.internal;
//
//import java.text.DecimalFormat;
//import java.text.DecimalFormatSymbols;
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.Locale;
//
//import org.javamoney.moneta.format.AmountFormatSymbols;
//
//import javax.money.format.AmountFormatContext;
//
//import org.javamoney.moneta.format.CurrencyStyle;
//import javax.money.spi.AmountStyleProviderSpi;
//
///**
// * Implementation of {@link AmountStyleProviderSpi} based on the corresponding {@link DecimalFormat}
// * .
// *
// * @author Anatole Tresch
// */
//public class DefaultAmountStyleProviderSpi implements
//		AmountStyleProviderSpi {
//	/*
//	 * (non-Javadoc)
//	 * @see javax.money.spi.AmountStyleProviderSpi#getAmountFormatContext(java.util.Locale)
//	 */
//	@Override
//	public AmountFormatContext getAmountFormatContext(Locale locale) {
//		DecimalFormat df = (DecimalFormat) DecimalFormat
//				.getCurrencyInstance(locale);
//		return new AmountFormatContext.Builder(locale)
//				.setGroupingSizes(df.getGroupingSize())
//				.setPattern(df.toPattern())
//				.setCurrencyStyle(CurrencyStyle.CODE)
//				.setSymbols(AmountFormatSymbols.of(locale))
//				.create();
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * @see javax.money.spi.AmountStyleProviderSpi#getSupportedLocales()
//	 */
//	@Override
//	public Collection<Locale> getSupportedLocales() {
//		return Arrays.asList(DecimalFormatSymbols.getAvailableLocales());
//	}
//
//}

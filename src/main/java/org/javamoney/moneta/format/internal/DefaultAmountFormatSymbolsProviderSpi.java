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
//import java.text.DecimalFormatSymbols;
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.Locale;
//import java.util.logging.Logger;
//
//import org.javamoney.moneta.format.AmountFormatSymbols;
//import javax.money.spi.AmountFormatSymbolsProviderSpi;
//
///**
// * Implementation of {@link AmountFormatSymbolsProviderSpi} providing the
// * symbols as defined by {@link DecimalFormatSymbols}.
// *
// * @author Anatole Tresch
// */
//public class DefaultAmountFormatSymbolsProviderSpi implements
//		AmountFormatSymbolsProviderSpi {
//
//	@Override
//	public AmountFormatSymbols getAmountFormatSymbols(Locale locale) {
//		try {
//			DecimalFormatSymbols syms = DecimalFormatSymbols
//					.getInstance(locale);
//			return new AmountFormatSymbols.Builder(locale)
//					.setDecimalSeparator(syms.getDecimalSeparator())
//					.setDigit(syms.getDigit())
//					.setExponentialSeparator(syms.getExponentSeparator())
//					.setGroupingSeparator(syms.getGroupingSeparator())
//					.setMinusSign(syms.getMinusSign())
//					.setPatternSeparator(syms.getPatternSeparator())
//					.setZeroDigit(syms.getZeroDigit()).create();
//		} catch (Exception e) {
//			// not supported, ignore exception
//			Logger.getLogger(getClass().getName()).warning(
//					"Unsupported format locale: " + locale);
//			return null;
//		}
//	}
//
//	@Override
//	public Collection<Locale> getSupportedLocales() {
//		return Arrays.asList(DecimalFormatSymbols.getAvailableLocales());
//	}
//
//}

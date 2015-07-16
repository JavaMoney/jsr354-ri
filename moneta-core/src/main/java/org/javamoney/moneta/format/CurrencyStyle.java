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
package org.javamoney.moneta.format;



/**
 * Defines the different variants of currency formatting.
 * 
 * @author Anatole Tresch
 * @author Werner Keil
 */
public enum CurrencyStyle {

	/**
	 * The currency will be rendered as its (non localized) currency code.
	 * 
	 * @see javax.money.CurrencyUnit#getCurrencyCode()
	 */
	CODE,

	/**
	 * The currency will be rendered as its localized display name. If no display
	 * name is known for the required {@link javax.money.CurrencyUnit}, the currency code
	 * should be used as a fall-back.
	 * 
	 * @see javax.money.CurrencyUnit#getCurrencyCode()
	 * @see java.util.Currency#getDisplayName(java.util.Locale)
	 */
	NAME,

	/**
	 * The currency will be rendered as its (non localized) numeric code.
	 * 
	 * @see javax.money.CurrencyUnit#getNumericCode()
	 */
	NUMERIC_CODE,

	/**
	 * The currency will be rendered as its localized currency symbol. If no
	 * symbol name is known for the required {@link javax.money.CurrencyUnit}, the currency
	 * code should be used as a fall-back.
	 * 
	 * @see javax.money.CurrencyUnit#getCurrencyCode()
	 * @see java.util.Currency#getSymbol(java.util.Locale)
	 */
	SYMBOL
}
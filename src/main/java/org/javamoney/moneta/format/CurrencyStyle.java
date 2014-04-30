/*
 * CREDIT SUISSE IS WILLING TO LICENSE THIS SPECIFICATION TO YOU ONLY UPON THE
 * CONDITION THAT YOU ACCEPT ALL OF THE TERMS CONTAINED IN THIS AGREEMENT.
 * PLEASE READ THE TERMS AND CONDITIONS OF THIS AGREEMENT CAREFULLY. BY
 * DOWNLOADING THIS SPECIFICATION, YOU ACCEPT THE TERMS AND CONDITIONS OF THE
 * AGREEMENT. IF YOU ARE NOT WILLING TO BE BOUND BY IT, SELECT THE "DECLINE"
 * BUTTON AT THE BOTTOM OF THIS PAGE.
 * 
 * Specification: JSR-354 Money and Currency API ("Specification")
 * 
 * Copyright (c) 2012-2013, Credit Suisse All rights reserved.
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
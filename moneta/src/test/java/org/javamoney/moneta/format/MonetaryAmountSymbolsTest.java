package org.javamoney.moneta.format;

import static org.testng.Assert.assertEquals;

import java.util.Locale;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

import org.testng.annotations.Test;

public class MonetaryAmountSymbolsTest {

	@Test
	public void shouldReturnDefaultCurrency() {
		MonetaryAmountSymbols symbols = new MonetaryAmountSymbols();
		CurrencyUnit currency = Monetary.getCurrency(Locale.getDefault(Locale.Category.FORMAT));
		assertEquals(symbols.getCurrency(), currency);
	}

	@Test
	public void shouldReturnCurrencyFromLocale() {
		MonetaryAmountSymbols symbols = new MonetaryAmountSymbols(Locale.US);
		CurrencyUnit currency = Monetary.getCurrency(Locale.US);
		assertEquals(symbols.getCurrency(), currency);
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldReturnNullPointerWhenCurrencyIsNull() {
		MonetaryAmountSymbols symbols = new MonetaryAmountSymbols(Locale.US);
		symbols.setCurrency(null);
	}


	@Test
	public void shouldSetAndReturnCurrencySymbol() {
		MonetaryAmountSymbols symbols = new MonetaryAmountSymbols();
		String currencySymbol = "RL";
		symbols.setCurrencySymbol(currencySymbol);
		assertEquals(symbols.getCurrencySymbol(), currencySymbol);
	}

	@Test
	public void shouldSetAndReturnDecimalSeparator() {
		MonetaryAmountSymbols symbols = new MonetaryAmountSymbols();
		char decimalSeparator = 'A';
		symbols.setDecimalSeparator(decimalSeparator);
		assertEquals(symbols.getDecimalSeparator(), decimalSeparator);
	}

	@Test
	public void shouldSetAndReturnDigit() {
		MonetaryAmountSymbols symbols = new MonetaryAmountSymbols();
		char digit = 'A';
		symbols.setDigit(digit);
		assertEquals(symbols.getDigit(), digit);
	}

	@Test
	public void shouldSetAndReturnExponentSeparator() {
		MonetaryAmountSymbols symbols = new MonetaryAmountSymbols();
		String exponentSeparator = "#";
		symbols.setExponentSeparator(exponentSeparator );
		assertEquals(symbols.getExponentSeparator(), exponentSeparator);
	}

	@Test
	public void shouldSetAndReturnGroupingSeparator() {
		MonetaryAmountSymbols symbols = new MonetaryAmountSymbols();
		char groupingSeparator = '#';
		symbols.setGroupingSeparator(groupingSeparator );
		assertEquals(symbols.getGroupingSeparator(), groupingSeparator);
	}

	@Test
	public void shouldSetAndReturnInfinity() {
		MonetaryAmountSymbols symbols = new MonetaryAmountSymbols();
		String infinity = "@";
		symbols.setInfinity(infinity );
		assertEquals(symbols.getInfinity(), infinity);
	}

	@Test
	public void shouldSetAndReturnInternationalCurrencySymbol() {
		MonetaryAmountSymbols symbols = new MonetaryAmountSymbols();
		String internationalCurrencySymbol = "BRL";
		symbols.setInternationalCurrencySymbol(internationalCurrencySymbol );
		assertEquals(symbols.getInternationalCurrencySymbol(), internationalCurrencySymbol);
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldReturnErrorWhenInternationalCurrencySymbolIsNull() {
		MonetaryAmountSymbols symbols = new MonetaryAmountSymbols();
		String internationalCurrencySymbol = null;
		symbols.setInternationalCurrencySymbol(internationalCurrencySymbol );
		assertEquals(symbols.getInternationalCurrencySymbol(), internationalCurrencySymbol);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void shouldReturnErrorWhenInternationalCurrencySymbolIsInvalid() {
		MonetaryAmountSymbols symbols = new MonetaryAmountSymbols(Locale.US);
		symbols.setInternationalCurrencySymbol("ERROR");
	}

	@Test
	public void shouldSetAndReturnMinusSign() {
		MonetaryAmountSymbols symbols = new MonetaryAmountSymbols();
		char minusSing = '#';
		symbols.setMinusSign(minusSing );
		assertEquals(symbols.getMinusSign(), minusSing);
	}

	@Test
	public void shouldSetAndReturnMonetaryDecimalSeparator() {
		MonetaryAmountSymbols symbols = new MonetaryAmountSymbols();
		char monetaryDecimalSeparator = '#';
		symbols.setMonetaryDecimalSeparator(monetaryDecimalSeparator);
		assertEquals(symbols.getMonetaryDecimalSeparator(),
				monetaryDecimalSeparator);
	}

	@Test
	public void shouldSetAndReturnNan() {
		MonetaryAmountSymbols symbols = new MonetaryAmountSymbols();
		String nan = "NotNumber";
		symbols.setNaN(nan);
		assertEquals(symbols.getNaN(), nan);
	}

	@Test
	public void shouldSetAndReturnPatternSeparator() {
		MonetaryAmountSymbols symbols = new MonetaryAmountSymbols();
		char patternSeparator = ',';
		symbols.setPatternSeparator(patternSeparator);
		assertEquals(symbols.getPatternSeparator(), patternSeparator);
	}

	@Test
	public void shouldSetAndReturnPercent() {
		MonetaryAmountSymbols symbols = new MonetaryAmountSymbols();
		char percent = '$';
		symbols.setPercent(percent);
		assertEquals(symbols.getPercent(),percent);
	}

	@Test
	public void shouldSetAndReturnPerMill() {
		MonetaryAmountSymbols symbols = new MonetaryAmountSymbols();
		char perMill = '$';
		symbols.setPerMill(perMill);
		assertEquals(symbols.getPerMill(),perMill);
	}

	@Test
	public void shouldSetAndReturnZeroDigit() {
		MonetaryAmountSymbols symbols = new MonetaryAmountSymbols();
		char zeroDigit = 'O';
		symbols.setZeroDigit(zeroDigit);
		assertEquals(symbols.getZeroDigit(),zeroDigit);
	}



}

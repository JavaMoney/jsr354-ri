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

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.math.BigDecimal;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.format.MonetaryParseException;

import org.javamoney.moneta.FastMoney;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.RoundedMoney;
import org.javamoney.moneta.format.ToStringMonetaryAmountFormat;
import org.javamoney.moneta.format.ToStringMonetaryAmountFormat.ToStringMonetaryAmountFormatStyle;
import org.testng.annotations.*;

public class ToStringMonetaryAmountFormatSuffixTest {

	private static final CurrencyUnit BRAZILIAN_REAL = Monetary
			.getCurrency("BRL");

	private MonetaryAmount money;
	private MonetaryAmount fastMoney;
	private MonetaryAmount roundedMoney;



	@BeforeMethod
	public void init() throws InterruptedException {
        System.setProperty("org.javamoney.toStringFormatOrder", "ac");
		money = Money.of(BigDecimal.TEN, BRAZILIAN_REAL);
		fastMoney = FastMoney.of(BigDecimal.TEN, BRAZILIAN_REAL);
		roundedMoney = RoundedMoney.of(BigDecimal.TEN, BRAZILIAN_REAL);
	}

	@AfterMethod
	public void tearDown(){
		System.clearProperty("org.javamoney.toStringFormatOrder");
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldReturnNPEWhenTheValueIsNull() {
		ToStringMonetaryAmountFormat format = ToStringMonetaryAmountFormat
				.of(ToStringMonetaryAmountFormatStyle.FAST_MONEY);
		format.parse(null);
	}

	@Test(expectedExceptions = MonetaryParseException.class)
	public void shouldReturnErrorWhenNumberIsInvalid() {
		ToStringMonetaryAmountFormat format = ToStringMonetaryAmountFormat
				.of(ToStringMonetaryAmountFormatStyle.FAST_MONEY);
		format.parse("BRL 23AD");
	}

	@Test(expectedExceptions = MonetaryParseException.class)
	public void shouldReturnErrorWhenCurrencyIsInvalid() {
		ToStringMonetaryAmountFormat format = ToStringMonetaryAmountFormat
				.of(ToStringMonetaryAmountFormatStyle.FAST_MONEY);
		format.parse("AXD 23");
	}

	@Test(expectedExceptions = MonetaryParseException.class)
	public void shouldReturnErrorWhenJustHasNumber() {
		ToStringMonetaryAmountFormat format = ToStringMonetaryAmountFormat
				.of(ToStringMonetaryAmountFormatStyle.FAST_MONEY);
		format.parse("23");
	}

	@Test(expectedExceptions = MonetaryParseException.class)
	public void shouldReturnErrorWhenJustHasCurrency() {
		ToStringMonetaryAmountFormat format = ToStringMonetaryAmountFormat
				.of(ToStringMonetaryAmountFormatStyle.FAST_MONEY);
		format.parse("BRL");
	}

	@Test
	public void parserMoneyTest() {
		executeTest(money, fastMoney, roundedMoney,
				ToStringMonetaryAmountFormatStyle.MONEY);
	}

	@Test
	public void parserFastMoneyTest() {
		executeTest(fastMoney, money, roundedMoney,
				ToStringMonetaryAmountFormatStyle.FAST_MONEY);
	}

	@Test
	public void parserRoundedMoneyTest() {
		executeTest(roundedMoney, fastMoney, money,
				ToStringMonetaryAmountFormatStyle.ROUNDED_MONEY);
	}

	@Test
	public void shoudReturNullStringOnQueryFromWhenMonetaryIsNullWithFastMoney() {
		String result = ToStringMonetaryAmountFormat.of(ToStringMonetaryAmountFormatStyle.FAST_MONEY).queryFrom(null);
		assertEquals(result, "null");
	}

	@Test
	public void shoudReturnToStringOnQueryFromWhenMonetaryWithFastMoney() {
		MonetaryAmount money = Money.of(10, BRAZILIAN_REAL);
		String result = ToStringMonetaryAmountFormat.of(ToStringMonetaryAmountFormatStyle.FAST_MONEY).queryFrom(money);
		assertEquals(result, "10 BRL");
	}

	@Test
	public void shoudReturNullStringOnQueryFromWhenMonetaryIsNullWithMoney() {
		String result = ToStringMonetaryAmountFormat.of(ToStringMonetaryAmountFormatStyle.MONEY).queryFrom(null);
		assertEquals(result, "null");
	}

	@Test
	public void shoudReturnToStringOnQueryFromWhenMonetaryWithMoney() {
		MonetaryAmount money = Money.of(10.1, BRAZILIAN_REAL);
		String result = ToStringMonetaryAmountFormat.of(ToStringMonetaryAmountFormatStyle.MONEY).queryFrom(money);
		assertEquals(result, "10.1 BRL");
	}

	@Test
	public void shoudReturNullStringOnQueryFromWhenMonetaryIsNullWithRoundedMoney() {
		String result = ToStringMonetaryAmountFormat.of(ToStringMonetaryAmountFormatStyle.ROUNDED_MONEY).queryFrom(null);
		assertEquals(result, "null");
	}

	@Test
	public void shoudReturnToStringOnQueryFromWhenMonetaryWithRoundedMoney() {
		MonetaryAmount money = Money.of(10, BRAZILIAN_REAL);
		String result = ToStringMonetaryAmountFormat.of(ToStringMonetaryAmountFormatStyle.ROUNDED_MONEY).queryFrom(money);
		assertEquals(result, "10 BRL");
	}
	@Test
	public void shoudReturNullStringOnPrintWhenMonetaryIsNullWithFastMoney() throws IOException {
		StringBuilder sb = new StringBuilder();
		ToStringMonetaryAmountFormat.of(ToStringMonetaryAmountFormatStyle.FAST_MONEY).print(sb, null);
		assertEquals(sb.toString(), "null");
	}

	@Test
	public void shoudReturnToStringOnPrintWhenHasMonetaryWithFastMoney() throws IOException {
		StringBuilder sb = new StringBuilder();
		MonetaryAmount money = Money.of(10, BRAZILIAN_REAL);
		ToStringMonetaryAmountFormat.of(ToStringMonetaryAmountFormatStyle.FAST_MONEY).print(sb, money);
		assertEquals(sb.toString(), "10 BRL");
	}

	@Test
	public void shoudReturNullStringOnPrintWhenMonetaryIsNullWithMoney() throws IOException {
		StringBuilder sb = new StringBuilder();
		ToStringMonetaryAmountFormat.of(ToStringMonetaryAmountFormatStyle.MONEY).print(sb, null);
		assertEquals(sb.toString(), "null");
	}

	@Test
	public void shoudReturnToStringOnPrintWhenHasMonetaryWithMoney() throws IOException {
		StringBuilder sb = new StringBuilder();
		MonetaryAmount money = Money.of(10, BRAZILIAN_REAL);
		ToStringMonetaryAmountFormat.of(ToStringMonetaryAmountFormatStyle.MONEY).print(sb, money);
		assertEquals(sb.toString(), "10 BRL");
	}
	@Test
	public void shoudReturNullStringOnPrintWhenMonetaryIsNullWithRoundedMoney() throws IOException {
		StringBuilder sb = new StringBuilder();
		ToStringMonetaryAmountFormat.of(ToStringMonetaryAmountFormatStyle.ROUNDED_MONEY).print(sb, null);
		assertEquals(sb.toString(), "null");
	}

	@Test
	public void shoudReturnToStringOnPrintWhenHasMonetaryWithRoundedMoney() throws IOException {
		StringBuilder sb = new StringBuilder();
		MonetaryAmount money = Money.of(10, BRAZILIAN_REAL);
		ToStringMonetaryAmountFormat.of(ToStringMonetaryAmountFormatStyle.ROUNDED_MONEY).print(sb, money);
		assertEquals(sb.toString(), "10 BRL");
	}

	private void executeTest(MonetaryAmount expectedMoney, MonetaryAmount a,
			MonetaryAmount b, ToStringMonetaryAmountFormatStyle type) {

		MonetaryAmount parserAResult = parser(a, type);
		MonetaryAmount parserBResult = parser(b, type);

		assertEquals(parserAResult, expectedMoney);
		assertEquals(parserBResult, expectedMoney);
		assertEquals(parserBResult, parserAResult);
	}

	private MonetaryAmount parser(MonetaryAmount a,
			ToStringMonetaryAmountFormatStyle style) {
		return ToStringMonetaryAmountFormat.of(style).parse(a.toString());
	}
}

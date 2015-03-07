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
package org.javamoney.moneta;

import java.math.BigDecimal;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryCurrencies;
import javax.money.MonetaryRoundings;

import org.testng.annotations.Test;

/**
 * @author Anatole
 * 
 */
public class PerformanceTest {

	protected static final CurrencyUnit EURO = MonetaryCurrencies
			.getCurrency("EUR");

	@Test(enabled = true)
	public void comparePerformanceNoRounding() {
		FastMoney money1 = FastMoney.of(BigDecimal.ONE,EURO);
		long start = System.currentTimeMillis();
		final int NUM = 1000000;
		for (int i = 0; i < NUM; i++) {
			money1 = money1.add(FastMoney.of(1234567.3444,EURO));
			money1 = money1.subtract(FastMoney.of(232323,EURO));
			money1 = money1.multiply(3.4);
			money1 = money1.divide(5.456);
		}
		long end = System.currentTimeMillis();
		long duration = end - start;
		System.out.println("Duration for 1000000 operations (FastMoney/BD): "
				+ duration + " ms (" + ((duration * 1000) / NUM)
				+ " ns per loop) -> "
				+ money1);

		FastMoney money2 = FastMoney.of( BigDecimal.ONE,EURO);
		start = System.currentTimeMillis();
		for (int i = 0; i < NUM; i++) {
			money2 = money2.add(FastMoney.of( 1234567.3444,EURO));
			money2 = money2.subtract(FastMoney.of( 232323,EURO));
			money2 = money2.multiply(3.4);
			money2 = money2.divide(5.456);
		}
		end = System.currentTimeMillis();
		duration = end - start;
		System.out.println("Duration for " + NUM
                + " operations (FastMoney/long): "
                + duration + " ms (" + ((duration * 1000) / NUM)
				+ " ns per loop) -> "
				+ money2);

		FastMoney money3 = FastMoney.of(BigDecimal.ONE,EURO);
		start = System.currentTimeMillis();
		for (int i = 0; i < NUM; i++) {
			money3 = money3.add(FastMoney.of(1234567.3444,EURO));
			money3 = money3.subtract(FastMoney.of( 232323,EURO));
			money3 = money3.multiply(3.4);
			money3 = money3.divide(5.456);
		}
		end = System.currentTimeMillis();
		duration = end - start;
		System.out.println("Duration for " + NUM
                + " operations (FastMoney/FastMoney mixed): "
                + duration + " ms (" + ((duration * 1000) / NUM)
				+ " ns per loop) -> "
				+ money3);
	}
	
	
	@Test(enabled = true)
	public void comparePerformance() {
		StringBuilder b = new StringBuilder();
		b.append("PerformanceTest - Looping code Money,BD:\n");
		b.append("========================================\n");
		b.append("Money money1 = money1.add(Money.of(EURO, 1234567.3444));\n");
		b.append("money1 = money1.subtract(Money.of(EURO, 232323));\n");
		b.append("money1 = money1.multiply(3.4);\n");
		b.append("money1 = money1.divide(5.456);\n");
		b.append("money1 = money1.with(MonetaryRoundings.getRounding());\n");
		System.out.println(b);
		b.setLength(0);
		Money money1 = Money.of(BigDecimal.ONE,EURO);
		long start = System.currentTimeMillis();
		final int NUM = 100000;
		MonetaryAmount adding = Money.of( 1234567.3444,EURO);
		MonetaryAmount subtracting = Money.of( 232323,EURO);
		for (int i = 0; i < NUM; i++) {
			money1 = money1.add(adding);
			money1 = money1.subtract(subtracting);
			money1 = money1.multiply(3.4);
			money1 = money1.divide(5.456);
			money1 = money1.with(MonetaryRoundings.getDefaultRounding());
		}
		long end = System.currentTimeMillis();
		long duration = end - start;
		System.out.println("Duration for " + NUM + " operations (Money,BD): "
				+ duration + " ms (" + ((duration * 1000) / NUM)
				+ " ns per loop) -> "
				+ money1);
		System.out.println();
		b.append("PerformanceTest - Looping code FastMoney,long:\n");
		b.append("==============================================\n");
		b.append("FastMoney money1 = money1.add(FastMoney.of(EURO, 1234567.3444));\n");
		b.append("money1 = money1.subtract(FastMoney.of(EURO, 232323));\n");
		b.append("money1 = money1.multiply(3.4);\n");
		b.append("money1 = money1.divide(5.456);\n");
		b.append("money1 = money1.with(MonetaryRoundings.getRounding());\n");
		System.out.println(b);
		b.setLength(0);
		FastMoney money2 = FastMoney.of( BigDecimal.ONE,EURO);
		start = System.currentTimeMillis();
		adding = FastMoney.of( 1234567.3444,EURO);
		subtracting = FastMoney.of( 232323,EURO);
		for (int i = 0; i < NUM; i++) {
			money2 = money2.add(adding);
			money2 = money2.subtract(subtracting);
			money2 = money2.multiply(3.4);
			money2 = money2.divide(5.456);
			money2 = money2.with(MonetaryRoundings.getDefaultRounding());
		}
		end = System.currentTimeMillis();
		duration = end - start;
		System.out.println("Duration for " + NUM
				+ " operations (FastMoney,long): "
				+ duration + " ms (" + ((duration * 1000) / NUM)
				+ " ns per loop) -> "
				+ money2);
		System.out.println();
		
		b.append("PerformanceTest - Looping code Mixed 1, FastMoney/Money:\n");
		b.append("========================================================\n");
		b.append("FastMoney money1 = money1.add(Money.of(EURO, 1234567.3444));\n");
		b.append("money1 = money1.subtract(Money.of(EURO, 232323));\n");
		b.append("money1 = money1.multiply(3.4);\n");
		b.append("money1 = money1.divide(5.456);\n");
		b.append("money1 = money1.with(MonetaryRoundings.getRounding());\n");
		System.out.println(b);
		b.setLength(0);
		adding = Money.of( 1234567.3444,EURO);
		subtracting = Money.of( 232323,EURO);
		FastMoney money3 = FastMoney.of( BigDecimal.ONE,EURO);
		start = System.currentTimeMillis();
		for (int i = 0; i < NUM; i++) {
			money3 = money3.add(adding);
			money3 = money3.subtract(subtracting);
			money3 = money3.multiply(3.4);
			money3 = money3.divide(5.456);
			money3 = money3.with(MonetaryRoundings.getDefaultRounding());
		}
		end = System.currentTimeMillis();
		duration = end - start;
		System.out.println("Duration for " + NUM
				+ " operations (FastMoney/Money mixed 2): "
				+ duration + " ms (" + ((duration * 1000) / NUM)
				+ " ns per loop) -> "
				+ money3);
		System.out.println();
		b.append("PerformanceTest - Looping code Mixed 2, Money/FastMoney:\n");
		b.append("========================================================\n");
		b.append("Money money1 = money1.add(FastMoney.of(EURO, 1234567.3444));\n");
		b.append("money1 = money1.subtract(FastMoney.of(EURO, 232323));\n");
		b.append("money1 = money1.multiply(3.4);\n");
		b.append("money1 = money1.divide(5.456);\n");
		b.append("money1 = money1.with(MonetaryRoundings.getRounding());\n");
		System.out.println(b);
		b.setLength(0);
		adding = FastMoney.of( 1234567.3444,EURO);
		subtracting = FastMoney.of( 232323,EURO);
		Money money4 = Money.of( BigDecimal.ONE,EURO);
		start = System.currentTimeMillis();
		for (int i = 0; i < NUM; i++) {
			money4 = money4.add(adding);
			money4 = money4.subtract(subtracting);
			money4 = money4.multiply(3.4);
			money4 = money4.divide(5.456);
			money4 = money4.with(MonetaryRoundings.getDefaultRounding());
		}
		end = System.currentTimeMillis();
		duration = end - start;
		System.out.println("Duration for " + NUM
				+ " operations (Money/FastMoney mixed 2): "
				+ duration + " ms (" + ((duration * 1000) / NUM)
				+ " ns per loop) -> "
				+ money4);
		System.out.println();
		System.out.println();
	}

}

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
 * 
 * Contributors: Anatole Tresch - initial implementation Wernner Keil -
 * extensions and adaptions.
 */
package javax.money;

import java.math.BigDecimal;

import org.junit.Test;

/**
 * @author Anatole
 * 
 */
public class PerformanceTest {

	private static final BigDecimal TEN = new BigDecimal(10.0d);
	protected static final CurrencyUnit EURO = Currencies.of("EUR");
	protected static final CurrencyUnit DOLLAR = Currencies
			.of("USD");


	@Test
	public void comparePerformance() {
		Money money1 = Money.of(EURO, BigDecimal.ONE);
		long start = System.currentTimeMillis();
		final int NUM = 1000000;
		for (int i = 0; i < NUM; i++) {
			money1 = money1.add(Money.of(EURO, 1234567.3444));
			money1 = money1.subtract(Money.of(EURO, 232323));
			money1 = money1.multiply(3.4);
			money1 = money1.divide(5.456);
			// money1 = money1.with(MonetaryRoundings.getRounding());
		}
		long end = System.currentTimeMillis();
		long duration = end - start;
		System.out.println("Duration for 1000000 operations (Money/BD): "
				+ duration + " ms (" + ((duration * 1000) / NUM)
				+ " ns per loop) -> "
				+ money1);

		FastMoney money2 = FastMoney.of(EURO, BigDecimal.ONE);
		start = System.currentTimeMillis();
		for (int i = 0; i < NUM; i++) {
			money2 = money2.add(FastMoney.of(EURO, 1234567.3444));
			money2 = money2.subtract(FastMoney.of(EURO, 232323));
			money2 = money2.multiply(3.4);
			money2 = money2.divide(5.456);
			// money2 = money1.with(MonetaryRoundings.getRounding());
		}
		end = System.currentTimeMillis();
		duration = end - start;
		System.out.println("Duration for " + NUM
				+ " operations (IntegralMoney/long): "
				+ duration + " ms (" + ((duration * 1000) / NUM)
				+ " ns per loop) -> "
				+ money2);

		FastMoney money3 = FastMoney.of(EURO, BigDecimal.ONE);
		start = System.currentTimeMillis();
		for (int i = 0; i < NUM; i++) {
			money3 = money3.add(Money.of(EURO, 1234567.3444));
			money3 = money3.subtract(FastMoney.of(EURO, 232323));
			money3 = money3.multiply(3.4);
			money3 = money3.divide(5.456);
			// money3 = money3.with(MonetaryRoundings.getRounding());
		}
		end = System.currentTimeMillis();
		duration = end - start;
		System.out.println("Duration for " + NUM
				+ " operations (IntegralMoney/Money mixed): "
				+ duration + " ms (" + ((duration * 1000) / NUM)
				+ " ns per loop) -> "
				+ money3);
	}

}

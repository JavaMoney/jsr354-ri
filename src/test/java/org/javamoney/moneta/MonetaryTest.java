package org.javamoney.moneta;


import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.math.RoundingMode;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.MonetaryAmountFactoryQueryBuilder;

import org.testng.annotations.Test;

public class MonetaryTest {

	@Test
	public void shouldCreateMonetaryFactory() {
		MonetaryAmount monetaryAmount = Monetary.getAmountFactory(
                MonetaryAmountFactoryQueryBuilder.of()
                .set(RoundingMode.DOWN)
                .setPrecision(256).build()
        ).setCurrency("CHF").setNumber(1234.5678).create();
        assertEquals(256, monetaryAmount.getContext().getPrecision());
        assertEquals(RoundingMode.DOWN, monetaryAmount.getContext().get(RoundingMode.class));
	}

	@Test
	public void shouldCreateMonetaryFactoryWithRoundindModeNull() {
		MonetaryAmount monetaryAmount = Monetary.getAmountFactory(
                MonetaryAmountFactoryQueryBuilder.of()
                .setPrecision(256).build()
        ).setCurrency("CHF").setNumber(1234.5678).create();
        assertEquals(256, monetaryAmount.getContext().getPrecision());
        assertNull(monetaryAmount.getContext().get(RoundingMode.class));
	}

	@Test
	public void shouldCreateMonetaryFactoryWithPrecisionNull() {
		MonetaryAmount monetaryAmount = Monetary.getAmountFactory(
                MonetaryAmountFactoryQueryBuilder.of()
                .set(RoundingMode.HALF_DOWN)
                .setTargetType(Money.class)
                .build()
        ).setCurrency("CHF").setNumber(1234.5678).create();
		assertEquals(0, monetaryAmount.getContext().getPrecision());
        assertEquals(RoundingMode.HALF_DOWN, monetaryAmount.getContext().get(RoundingMode.class));
	}

	@Test
	public void shouldCreateMonetaryFactoryWithBothNull() {
		MonetaryAmount monetaryAmount = Monetary.getAmountFactory(
                MonetaryAmountFactoryQueryBuilder.of()
                .set(RoundingMode.HALF_DOWN)
                .setTargetType(Money.class)
                .build()
        ).setCurrency("CHF").setNumber(1234.5678).create();
		assertEquals(0, monetaryAmount.getContext().getPrecision());
        assertEquals(RoundingMode.HALF_DOWN, monetaryAmount.getContext().get(RoundingMode.class));
	}
}

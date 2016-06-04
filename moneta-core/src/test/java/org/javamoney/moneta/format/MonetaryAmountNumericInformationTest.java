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

import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class MonetaryAmountNumericInformationTest {

	private MonetaryAmountNumericInformation numericInformation;

	@BeforeMethod
	public void setup() {
		numericInformation = new MonetaryAmountNumericInformation(new DecimalFormat());
	}

	@Test
	public void shouldSetAndReturnMaximumFractionDigits() {
		int maximumFractionDigitis = 10;
		numericInformation.setMaximumFractionDigits(maximumFractionDigitis);
		assertEquals(numericInformation.getMaximumFractionDigits(), maximumFractionDigitis);
	}

	@Test
	public void shouldSetAndReturnMaximumIntegerDigits() {
		int maximumIntegerDigits = 10;
		numericInformation.setMaximumIntegerDigits(maximumIntegerDigits);
		assertEquals(numericInformation.getMaximumIntegerDigits(), maximumIntegerDigits);
	}

	@Test
	public void shouldSetAndReturnMinimumFractionDigits() {
		int minimumFractionDigits = 2;
		numericInformation.setMinimumFractionDigits(minimumFractionDigits);
		assertEquals(numericInformation.getMinimumFractionDigits(), minimumFractionDigits);
	}

	@Test
	public void shouldSetAndReturnMinimumIntegerDigits() {
		int minimumIntegerDigits = 2;
		numericInformation.setMinimumIntegerDigits(minimumIntegerDigits);
		assertEquals(numericInformation.getMinimumIntegerDigits(), minimumIntegerDigits);
	}

	@Test
	public void shouldSetAndReturnDecimalSeparatorAlwaysShown() {
		boolean decimalSeparatorAlwaysShown = true;
		numericInformation.setDecimalSeparatorAlwaysShown(decimalSeparatorAlwaysShown);
		assertEquals(numericInformation.isDecimalSeparatorAlwaysShown(), decimalSeparatorAlwaysShown);
	}

	@Test
	public void shouldSetAndReturnGroupingUsed() {
		boolean groupingUsed = true;
		numericInformation.setGroupingUsed(groupingUsed);
		assertEquals(numericInformation.isGroupingUsed(), groupingUsed);
	}

	@Test
	public void shouldSetAndGroupingSize() {
		int groupingSize = 3;
		numericInformation.setGroupingSize(groupingSize);
		assertEquals(numericInformation.getGroupingSize(), groupingSize);
	}

	@Test
	public void shouldSetAndMultiplier() {
		int multiplier = 3;
		numericInformation.setMultiplier(multiplier);
		assertEquals(numericInformation.getMultiplier(), multiplier);
	}

	@Test
	public void shouldSetAndParseBigDecimal() {
		boolean parseBigDecimal = true;
		numericInformation.setParseBigDecimal(parseBigDecimal);
		assertEquals(numericInformation.isParseBigDecimal(), parseBigDecimal);
	}

	@Test
	public void shouldSetAndParseIntegerOnly() {
		boolean parseIntegerOnly = true;
		numericInformation.setParseIntegerOnly(parseIntegerOnly);
		assertEquals(numericInformation.isParseIntegerOnly(), parseIntegerOnly);
	}

	@Test
	public void shouldSetAndRoundingMode() {
		RoundingMode roundingMode = RoundingMode.HALF_EVEN;
		numericInformation.setRoundingMode(roundingMode);
		assertEquals(numericInformation.getRoundingMode(), roundingMode);
	}
}

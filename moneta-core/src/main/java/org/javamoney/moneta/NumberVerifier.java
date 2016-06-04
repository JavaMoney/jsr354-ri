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
package org.javamoney.moneta;

/**
 * Class utils to {@link Number}
 * @author otaviojava
 * @since 1.0.1
 */
class NumberVerifier {

	private NumberVerifier() {
	}

	public static void checkNoInfinityOrNaN(Number number) {
		if (Double.class == number.getClass() || Float.class == number.getClass()) {
			double dValue = number.doubleValue();
			if (Double.isNaN(dValue)) {
				throw new ArithmeticException("Not a valid input: NaN.");
			} else if (Double.isInfinite(dValue)) {
				throw new ArithmeticException("Not a valid input: INFINITY: " + dValue);
			}
		}
	}

	public static boolean isInfinityAndNotNaN(Number number) {
		if (Double.class == number.getClass() || Float.class == number.getClass()) {
			double dValue = number.doubleValue();
			if (Double.isNaN(dValue)) {
				throw new ArithmeticException("Not a valid input: NaN.");
			} else if (Double.isInfinite(dValue)) {
				return true;
			}
		}
		return false;
	}
}

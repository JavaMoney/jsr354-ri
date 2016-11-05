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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Objects;

/**
 * @deprecated
 */
@Deprecated
public final class MonetaryAmountNumericInformation {

	private final DecimalFormat format;

	MonetaryAmountNumericInformation(DecimalFormat format) {
		this.format = format;
	}

	/**
	 * Gets the maximum number of digits allowed in the fraction portion of a number.
	 */
	public int getMaximumFractionDigits(){
		return format.getMaximumFractionDigits();
	}
	/**
	 * Gets the maximum number of digits allowed in the integer portion of a number. For formatting numbers other than BigInteger and BigDecimal objects,
	 * the lower of the return value and 309 is used.
	 */
	public int getMaximumIntegerDigits(){
		return format.getMaximumIntegerDigits();
	}
	/**
	 *  Gets the minimum number of digits allowed in the fraction portion of a number.
	 *  For formatting numbers other than BigInteger and BigDecimal objects, the lower of the return value
	 *  and 340 is used.
	 */
	public int getMinimumFractionDigits(){
		return format.getMinimumFractionDigits();
	}
	/**
	 * Gets the minimum number of digits allowed in the integer portion of a number.
	 * For formatting numbers other than BigInteger and BigDecimal objects,
	 * the lower of the return value and 309 is used.
	 */
	public int getMinimumIntegerDigits(){
		return format.getMinimumIntegerDigits();
	}
	/**
	 * Allows you to get the behavior of the decimal separator with integers.
	 */
	public boolean isDecimalSeparatorAlwaysShown(){
		return format.isDecimalSeparatorAlwaysShown();
	}
	/**
	 * Returns true if grouping is used in this format.
	 */
	public boolean isGroupingUsed(){
		return format.isGroupingUsed();
	}
	/**
	 * Return the grouping size. Grouping size is the number of digits between grouping separators in the integer portion of a number. For example, in the number "123,456.78", the grouping size is 3.
	 */
	public int getGroupingSize(){
		return format.getGroupingSize();
	}
	/**
	 * Gets the multiplier for use in percent, per mille, and similar formats.
	 */
	public int getMultiplier(){
		return format.getMultiplier();
	}
	/**
	 *Returns true if this format will parse, the value part, as {@link BigDecimal} only.
	 */
	public boolean	isParseBigDecimal(){
		return format.isParseBigDecimal();
	}
	/**
	 *Returns true if this format will parse, the value part, as integers only.
	 */
	public boolean isParseIntegerOnly(){
		return format.isParseIntegerOnly();
	}
	/**
	 * Gets the RoundingMode used in this DecimalFormat.
	 */
	public RoundingMode getRoundingMode(){
		return format.getRoundingMode();
	}

	/**
	 * Sets the maximum number of digits allowed in the fraction portion of a number.
	 */
	public void setMaximumFractionDigits(int maximumFractionDigitis){
		format.setMaximumFractionDigits(maximumFractionDigitis);
	}
	/**
	 * Sets the maximum number of digits allowed in the integer portion of a number.
	 * For formatting numbers other than BigInteger and BigDecimal objects, the lower
	 * of newValue and 309 is used. Negative input values are replaced with 0.
	 */
	public void setMaximumIntegerDigits(int maximumIntegerDigits){
		format.setMaximumIntegerDigits(maximumIntegerDigits);
	}

	/**
	 * Sets the minimum number of digits allowed in the fraction portion of a number. For formatting numbers other than BigInteger and BigDecimal objects,
	 * the lower of newValue and 340 is used.
	 * Negative input values are replaced with 0.
	 */
	public void setMinimumFractionDigits(int minimumFractionDigits){
		format.setMinimumFractionDigits(minimumFractionDigits);
	}

	/**
	 * Sets the minimum number of digits allowed in the integer portion of a number. For formatting numbers other than BigInteger and BigDecimal objects,
	 * the lower of newValue and 309 is used.
	 * Negative input values are replaced with 0.
	 */
	public void setMinimumIntegerDigits(int minimumIntegerDigits){
		format.setMinimumIntegerDigits(minimumIntegerDigits);
	}

	/**
	 * Allows you to get the behavior of the decimal separator with integers.
	 */
	public void setDecimalSeparatorAlwaysShown(boolean decimalSeparatorAlwaysShown){
		format.setDecimalSeparatorAlwaysShown(decimalSeparatorAlwaysShown);
	}

	/**
	 * Set whether or not grouping will be used in this format.
	 */
	public void setGroupingUsed(boolean groupingUsed){
		format.setGroupingUsed(groupingUsed);
	}

	/**
	 * Set the grouping size. Grouping size is the number of digits between grouping separators
	 * in the integer portion of a number.
	 */
	public void setGroupingSize(int groupingSize){
		format.setGroupingSize(groupingSize);
	}

	/**
	 * Sets the multiplier for use in percent, per mille, and similar formats.
	 */
	public void setMultiplier(int multiplier){
		format.setMultiplier(multiplier);
	}
	/**
	 * Sets the RoundingMode used in this DecimalFormat.
	 */
	public void setRoundingMode(RoundingMode roundingMode){
		format.setRoundingMode(roundingMode);
	}

	/**
	 *Sets if this format will parse, the value part, as integers only.
	 */
	public void setParseIntegerOnly(boolean intergerOnley){
		format.setParseIntegerOnly(intergerOnley);
	}

	/**
	 *Sets if this format will parse, the value part, as {@link BigDecimal} only.
	 */
	public void setParseBigDecimal(boolean parseBigDecimal){
		format.setParseBigDecimal(parseBigDecimal);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(format);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		if (MonetaryAmountNumericInformation.class.isInstance(obj)) {
			MonetaryAmountNumericInformation other = MonetaryAmountNumericInformation.class.cast(obj);
			return Objects.equals(other.format, format);
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(MonetaryAmountNumericInformation.class.getName()).append('{')
		.append(" maximumFractionDigits: ").append(getMaximumFractionDigits()).append(',')
		.append(" maximumIntegerDigits: ").append(getMaximumIntegerDigits()).append(',')
		.append(" minimumFractionDigits: ").append(getMinimumFractionDigits()).append(',')
		.append(" minimumIntegerDigits: ").append(getMinimumIntegerDigits()).append(',')
		.append(" decimalSeparatorAlwaysShown: ").append(isDecimalSeparatorAlwaysShown()).append(',')
		.append(" groupingUsed: ").append(isGroupingUsed()).append(',')
		.append(" groupingSize: ").append(getGroupingSize()).append(',')
		.append(" multiplier: ").append(getMultiplier()).append(',')
		.append(" parseBigDecimal: ").append(isParseBigDecimal()).append(',')
		.append(" parseIntegerOnly: ").append(isParseIntegerOnly()).append(',')
		.append(" roundingMode: ").append(getRoundingMode()).append('}');
		return sb.toString();
	}
}

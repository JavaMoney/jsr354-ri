/*
 * CREDIT SUISSE IS WILLING TO LICENSE THIS SPECIFICATION TO YOU ONLY UPON THE
 * CONDITION THAT YOU ACCEPT ALL OF THE TERMS CONTAINED IN THIS AGREEMENT.
 * PLEASE READ THE TERMS AND CONDITIONS OF THIS AGREEMENT CAREFULLY. BY
 * DOWNLOADING THIS SPECIFICATION, YOU ACCEPT THE TERMS AND CONDITIONS OF THE
 * AGREEMENT. IF YOU ARE NOT WILLING TO BE BOUND BY IT, SELECT THE "DECLINE"
 * BUTTON AT THE BOTTOM OF THIS PAGE. Specification: JSR-354 Money and Currency
 * API ("Specification") Copyright (c) 2012-2013, Credit Suisse All rights
 * reserved.
 */
package org.javamoney.moneta.format.internal;

/**
 * Small utility class that supports flexible grouping of an input String using
 * different grouping characters and sizes.
 * 
 * @author Anatole Tresch
 */
final class StringGrouper {

	private char[] groupCharacters = new char[] { ' ' };
	private int[] groupSizes = new int[] { 3 };
	private boolean reverse;

	public StringGrouper() {
	}

	public StringGrouper(char... groupCharacters) {
		setGroupChars(groupCharacters);
	}

	public StringGrouper(char groupCharacter, int... groupSizes) {
		setGroupChars(new char[] { groupCharacter });
		setGroupSizes(groupSizes);
	}

	public StringGrouper(char[] groupCharacters, int... groupSizes) {
		setGroupChars(groupCharacters);
		setGroupSizes(groupSizes);
	}

	public StringGrouper setGroupChars(char... groupCharacters) {
		if (groupCharacters == null) {
			throw new IllegalArgumentException("groupCharacters is required.");
		}
		this.groupCharacters = groupCharacters.clone();
		return this;
	}

	public char[] getGroupChars() {
		return this.groupCharacters.clone();
	}

	public int[] getGroupSizes() {
		return this.groupSizes.clone();
	}

	public StringGrouper setGroupSizes(int... groupSizes) {
		if (groupSizes == null) {
			throw new IllegalArgumentException("groupSizes is required.");
		}
		this.groupSizes = groupSizes.clone();
		return this;
	}

	public StringGrouper setReverse(boolean reverse) {
		this.reverse = reverse;
		return this;
	}

	public boolean isReverse() {
		return this.reverse;
	}

	public String group(String input) {
		// strip way starting and ending alpha chars
		StringBuilder builder = new StringBuilder(4);
		String start = null;
		String end = null;
		char[] inputArr = input.toCharArray();
		for (char ch : inputArr) {
			if (Character.isDigit(ch)) {
				break;
			}
			builder.append(ch);
		}
		start = builder.toString();
		builder.setLength(0);
		for (int i = inputArr.length - 1; i >= 0; i--) {
			if (Character.isDigit(inputArr[i])) {
				break;
			}
			builder.insert(0, inputArr[i]);
		}
		end = builder.toString();
		builder.setLength(0);
		builder.append(start);
		builder.append(groupNumeric(input.substring(start.length(),
				input.length() - end.length())));
		builder.append(end);
		return builder.toString();
	}

	public String groupNumeric(String input) {
		if (groupSizes.length == 0 || groupCharacters.length == 0) {
			return input;
		}
		if (reverse) {
			return formatInternalReverse(input);
		}
		int groupIndex = 0;
		int sizeIndex = 0;
		char groupChar = groupCharacters[groupIndex];
		int groupSize = groupSizes[sizeIndex];
		if (groupSize <= 0) {
			// Bad case
			return input;
		}
		char[] group = new char[groupSize];
		int pos = input.length();
		StringBuilder result = new StringBuilder(input.length() + 4);
		while (pos > 0) {
			if (groupSize == 0) {
				// Bad case
				return input;
			}
			int start = pos - groupSize;
			if (!(result.length() == 0)) {
				result.insert(0, groupChar);
				// move characters/sizes forward if possible.
				if (groupIndex < (groupCharacters.length - 1)) {
					groupChar = groupCharacters[++groupIndex];
				}
			}
			if (start >= 0) {
				input.getChars(start, pos, group, 0);
				result.insert(0, group, 0, groupSize);
				pos -= groupSize;
				if (sizeIndex < (groupSizes.length - 1)) {
					groupSize = groupSizes[++sizeIndex];
					if (groupSize > group.length) {
						group = new char[groupSize];
					}
				}
			} else {
				input.getChars(0, pos, group, 0);
				result.insert(0, group, 0, pos);
				break;
			}
		}
		return result.toString();
	}

	private String formatInternalReverse(String input) {
		int groupIndex = 0;
		int sizeIndex = 0;
		char groupChar = groupCharacters[groupIndex];
		int groupSize = groupSizes[sizeIndex];
		if (groupSize <= 0) {
			// Bad case
			return input;
		}
		char[] group = new char[groupSize];
		int pos = 0;
		StringBuilder result = new StringBuilder(input.length() + 4);
		while (pos < input.length()) {
			if (groupSize == 0) {
				// Bad case
				return input;
			}
			int end = pos + groupSize;
			if (!(result.length() == 0)) {
				result.append(groupChar);
				// move characters/sizes forward if possible.
				if (groupIndex < (groupCharacters.length - 1)) {
					groupChar = groupCharacters[++groupIndex];
				}
			}
			if (end <= input.length()) {
				input.getChars(pos, end, group, 0);
				result.append(group, 0, groupSize);
				pos += groupSize;
				if (sizeIndex < (groupSizes.length - 1)) {
					groupSize = groupSizes[++sizeIndex];
					if (groupSize > group.length) {
						group = new char[groupSize];
					}
				}
			} else {
				input.getChars(pos, input.length(), group, 0);
				result.append(group, 0, input.length() - pos);
				break;
			}
		}
		return result.toString();
	}

}

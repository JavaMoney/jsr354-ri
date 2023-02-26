/*
  Copyright (c) 2012, 2023, Werner Keil and others by the @author tag.

  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy of
  the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  License for the specific language governing permissions and limitations under
  the License.
 */
package org.javamoney.moneta.convert.imf;

import java.time.YearMonth;
import java.util.Objects;

import static org.javamoney.moneta.convert.imf.defaults.Defaults.*;

enum IMFHistoricalType {
	SDR_Currency(SCRCV), Currency_SDR(CVSDR);

	private final String type;

	IMFHistoricalType(String type) {
		this.type = type;
	 }

	public String getType() {
		return type;
	}

	public String getUrl(YearMonth yearMonth) {
		return String.format(HOST, Objects.requireNonNull(yearMonth), type);
	}
}

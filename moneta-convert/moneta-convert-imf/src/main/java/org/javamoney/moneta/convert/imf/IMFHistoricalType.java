package org.javamoney.moneta.internal.convert.imf;

import java.time.YearMonth;
import java.util.Objects;

enum IMFHistoricalType {

	 SDR_Currency("SDRCV"), Currency_SDR("CVSDR");

	 private final String type;

	 private static final String HOST = "http://www.imf.org/external/np/fin/data/rms_mth.aspx?SelectDate=%s&reportType=%s&tsvflag=Y";

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

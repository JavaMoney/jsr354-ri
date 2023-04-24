package org.javamoney.moneta.convert.ecb.defaults;
/** This class is necessary to declare the "defaults" package using just for resources */
public final class Defaults {
    private Defaults() {}

    public static final String ECB_CURRENT_URL = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";
    public static final String ECB_CURRENT_FALLBACK_PATH = "org/javamoney/moneta/convert/ecb/defaults/eurofxref-daily.xml";

    public static final String ECB_HIST90_URL = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist-90d.xml";

    public static final String ECB_HIST90_FALLBACK_PATH = "org/javamoney/moneta/convert/ecb/defaults/eurofxref-hist-90d.xml";

    public static final String ECB_HIST_URL = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist.xml";
}

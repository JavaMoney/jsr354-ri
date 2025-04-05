/*
 * Copyright (c) 2023-2025, Werner Keil and others by the @author tag.
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
package org.javamoney.moneta.convert.ecb.defaults;
/** This class is necessary to declare the "defaults" package using just for resources,
 * it also serves as constant collection for default strings */
public final class Defaults {
    private Defaults() {}

    public static final String ECB_CURRENT_URL = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";
    public static final String ECB_CURRENT_FALLBACK_PATH = "/org/javamoney/moneta/convert/ecb/defaults/eurofxref-daily.xml";

    public static final String ECB_HIST90_URL = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist-90d.xml";
    public static final String ECB_HIST90_FALLBACK_PATH = "/org/javamoney/moneta/convert/ecb/defaults/eurofxref-hist-90d.xml";

    public static final String ECB_HIST_URL = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist.xml";
    public static final String ECB_HIST_FALLBACK_PATH = "/org/javamoney/moneta/convert/ecb/defaults/eurofxref-hist.xml";
}

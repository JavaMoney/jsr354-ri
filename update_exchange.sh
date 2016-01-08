#!/bin/sh
# Copyright (c) 2012, 2015, Credit Suisse (Anatole Tresch), Werner Keil and others by the @author tag.
#
# Licensed under the Apache License, Version 2.0 (the "License"); you may not
# use this file except in compliance with the License. You may obtain a copy of
# the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
# License for the specific language governing permissions and limitations under
# the License.
#
wget http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml
mv eurofxref-daily.xml src/main/resources/java-money/defaults/ECB/eurofxref-daily.xml
wget http://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist-90d.xml
mv eurofxref-hist-90d.xml src/main/resources/java-money/defaults/ECB/eurofxref-hist-90d.xml
wget http://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist.xml
mv eurofxref-hist.xml src/main/resources/java-money/defaults/ECB/eurofxref-hist.xml
wget http://www.imf.org/external/np/fin/data/rms_five.aspx?tsvflag=Y
mv rms_five.aspx?tsvflag=Y rms_five.xls
mv rms_five.xls src/main/resources/java-money/defaults/IMF/rms_five.xls



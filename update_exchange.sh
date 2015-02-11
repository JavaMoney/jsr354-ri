wget http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml
mv eurofxref-daily.xml src/main/resources/java-money/defaults/ECB/eurofxref-daily.xml
wget http://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist-90d.xml
mv eurofxref-hist-90d.xml src/main/resources/java-money/defaults/ECB/eurofxref-hist-90d.xml
wget http://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist.xml
mv eurofxref-hist.xml src/main/resources/java-money/defaults/ECB/eurofxref-hist.xml
wget http://www.imf.org/external/np/fin/data/rms_five.aspx?tsvflag=Y
mv rms_five.aspx?tsvflag=Y rms_five.xls
mv rms_five.xls src/main/resources/java-money/defaults/IMF/rms_five.xls



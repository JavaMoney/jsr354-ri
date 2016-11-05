printf "\n Downloading the ECB daily resource. \n"
wget http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml
mv eurofxref-daily.xml src/main/resources/java-money/defaults/ECB/eurofxref-daily.xml
printf "\n Done. Downloading the ECB-90 resource. \n"
wget http://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist-90d.xml
mv eurofxref-hist-90d.xml src/main/resources/java-money/defaults/ECB/eurofxref-hist-90d.xml
printf "\n Done. Downloading the ECB-hist resource. \n"
wget http://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist.xml
mv eurofxref-hist.xml src/main/resources/java-money/defaults/ECB/eurofxref-hist.xml
printf "\n Done.
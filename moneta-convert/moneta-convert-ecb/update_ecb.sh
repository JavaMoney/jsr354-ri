#!/usr/bin/env bash
printf "\n Downloading the ECB daily resource. \n"
# wget https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml
#mv eurofxref-daily.xml src/main/resources/org/javamoney/moneta/convert/ecb/defaults/eurofxref-daily.xml
curl --fail "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml" \
		--output src/main/resources/org/javamoney/moneta/convert/ecb/defaults/eurofxref-daily.xml
printf "\n Done. Downloading the ECB-90 resource. \n"
# wget https://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist-90d.xml
# mv eurofxref-hist-90d.xml src/main/resources/org/javamoney/moneta/convert/ecb/defaults/eurofxref-hist-90d.xml
curl --fail "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist-90d.xml" \
		--output src/main/resources/org/javamoney/moneta/convert/ecb/defaults/eurofxref-hist-90d.xml
printf "\n Done. Downloading the ECB-hist resource. \n"
wget https://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist.xml
mv eurofxref-hist.xml src/main/resources/org/javamoney/moneta/convert/ecb/defaults/eurofxref-hist.xml
# curl --fail "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist.xml" \
#		--output src/main/resources/org/javamoney/moneta/convert/ecb/defaults/eurofxref-hist.xml
printf "\n Done."
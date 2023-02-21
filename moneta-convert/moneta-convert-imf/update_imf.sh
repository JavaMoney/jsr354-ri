#!/usr/bin/env bash
printf "\n Downloading the IMF resource. \n"
# wget https://www.imf.org/external/np/fin/data/rms_five.aspx?tsvflag=Y
# mv rms_five.aspx?tsvflag=Y src/main/resources/org/javamoney/moneta/convert/imf/defaults/rms_five.tsv
curl --fail "https://www.imf.org/external/np/fin/data/rms_five.aspx?tsvflag=Y" \
		--output src/main/resources/org/javamoney/moneta/convert/imf/defaults/rms_five.tsv 
printf "\n Done. \n"
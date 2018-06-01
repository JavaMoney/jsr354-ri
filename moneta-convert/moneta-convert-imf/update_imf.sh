#!/usr/bin/env bash
printf "\n Downloading the IMF resource. \n"
wget -U "Chrome/51.0.2704.103" http://www.imf.org/external/np/fin/data/rms_five.aspx?tsvflag=Y
mv rms_five.aspx?tsvflag=Y src/main/resources/java-money/defaults/IMF/rms_five.xls
printf "\n Done. \n"
printf "\n Downloading the IMF resource. \n"
wget http://www.imf.org/external/np/fin/data/rms_five.aspx?tsvflag=Y
mv rms_five.aspx?tsvflag=Y src/main/resources/java-money/defaults/IMF/rms_five.xls
printf "\n Done. \n"
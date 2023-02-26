#!/usr/bin/env bash
set -eu
yearmonths="$@"
first=true
for yearmonth in $yearmonths
do
	$first || sleep 2
	first=false
	date=$(date --date="$yearmonth-01 +1 month -1 day" +%Y-%m-%d)
	curl --fail "https://www.imf.org/external/np/fin/data/rms_mth.aspx?SelectDate=${date}&reportType=CVSDR&tsvflag=Y" \
		--output $yearmonth.tsv \
		|| break
done

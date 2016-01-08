/**
 * Copyright (c) 2012, 2015, Credit Suisse (Anatole Tresch), Werner Keil and others by the @author tag.
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
package org.javamoney.moneta.internal.loader;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

class ScheduledDataLoaderService {

	private static final Logger LOG = Logger.getLogger(ScheduledDataLoaderService.class.getName());

	private final Timer timer;

	 ScheduledDataLoaderService(Timer timer) {
		this.timer = timer;
	}

	public void execute(final LoadableResource load) {
	        Objects.requireNonNull(load);
	        TimerTask task = new TimerTask() {
	            @Override
	            public void run() {
	                try {
	                    load.load();
	                } catch (Exception e) {
	                    LOG.log(Level.SEVERE, "Failed to update remote resource: " + load.getResourceId(), e);
	                }
	            }
	        };
	        Map<String, String> props = load.getProperties();
	        if (Objects.nonNull(props)) {
	            String value = props.get("period");
	            long periodMS = parseDuration(value);
	            value = props.get("delay");
	            long delayMS = parseDuration(value);
	            if (periodMS > 0) {
	                timer.scheduleAtFixedRate(task, delayMS, periodMS);
	            } else {
	                value = props.get("at");
	                if (Objects.nonNull(value)) {
	                    List<GregorianCalendar> dates = parseDates(value);
	                    dates.forEach(date -> timer.schedule(task, date.getTime(), 3_600_000 * 24 /* daily */));
	                }
	            }
	        }
	    }

	 /**
	     * Parse the dates of type HH:mm:ss:nnn, whereas minutes and smaller are
	     * optional.
	     *
	     * @param value the input text
	     * @return the parsed
	     */
	    private List<GregorianCalendar> parseDates(String value) {
	        String[] parts = value.split(",");
	        List<GregorianCalendar> result = new ArrayList<>();
	        for (String part : parts) {
	            if (part.isEmpty()) {
	                continue;
	            }
	            String[] subparts = part.split(":");
	            GregorianCalendar cal = new GregorianCalendar();
	            for (int i = 0; i < subparts.length; i++) {
	                switch (i) {
	                    case 0:
	                        cal.set(GregorianCalendar.HOUR_OF_DAY, Integer.parseInt(subparts[i]));
	                        break;
	                    case 1:
	                        cal.set(GregorianCalendar.MINUTE, Integer.parseInt(subparts[i]));
	                        break;
	                    case 2:
	                        cal.set(GregorianCalendar.SECOND, Integer.parseInt(subparts[i]));
	                        break;
	                    case 3:
	                        cal.set(GregorianCalendar.MILLISECOND, Integer.parseInt(subparts[i]));
	                        break;
	                }
	            }
	            result.add(cal);
	        }
	        return result;
	    }

	    /**
	     * Parse a duration of the form HH:mm:ss:nnn, whereas only hours are non
	     * optional.
	     *
	     * @param value the input value
	     * @return the duration in ms.
	     */
	     private long parseDuration(String value) {
	        long periodMS = 0L;
	        if (Objects.nonNull(value)) {
	            String[] parts = value.split(":");
	            for (int i = 0; i < parts.length; i++) {
	                switch (i) {
	                    case 0: // hours
	                        periodMS += (Integer.parseInt(parts[i])) * 3600000L;
	                        break;
	                    case 1: // minutes
	                        periodMS += (Integer.parseInt(parts[i])) * 60000L;
	                        break;
	                    case 2: // seconds
	                        periodMS += (Integer.parseInt(parts[i])) * 1000L;
	                        break;
	                    case 3: // ms
	                        periodMS += (Integer.parseInt(parts[i]));
	                        break;
	                    default:
	                        break;
	                }
	            }
	        }
	        return periodMS;
	    }

	@Override
	public String toString() {
		return ScheduledDataLoaderService.class.getName() + '{' + " timer: "
				+ timer + '}';
	}
}

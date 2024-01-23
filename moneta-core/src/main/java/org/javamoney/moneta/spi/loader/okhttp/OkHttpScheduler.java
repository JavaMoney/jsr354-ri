/*
  Copyright (c) 2024 Werner Keil and others by the @author tag.

  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy of
  the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  License for the specific language governing permissions and limitations under
  the License.
 */
package org.javamoney.moneta.spi.loader.okhttp;

import org.javamoney.moneta.spi.loader.LoaderListener;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

class OkHttpScheduler {

    private static final Logger LOG = Logger.getLogger(OkHttpScheduler.class.getName());

    private final Timer timer;
    private final LoaderListener listener;

    public OkHttpScheduler(Timer timer, LoaderListener listener) {
        this.timer = timer;
        this.listener = listener;
    }

    public void execute(final LoadableHttpResource load) {
        Objects.requireNonNull(load);
        Map<String, String> props = load.getProperties();
        if (Objects.nonNull(props)) {
            String value = props.get("period");
            long periodMS = parseDuration(value);
            value = props.get("delay");
            long delayMS = parseDuration(value);
            if (periodMS > 0) {
                timer.scheduleAtFixedRate(createTimerTask(load), delayMS, periodMS);
            } else {
                value = props.get("at");
                if (Objects.nonNull(value)) {
                    List<GregorianCalendar> dates = parseDates(value);
                    dates.forEach(date -> timer.schedule(createTimerTask(load), date.getTime(), 3_600_000 * 24 /* daily */));
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

    private TimerTask createTimerTask(final LoadableHttpResource load)
    {
        return new TimerTask() {
            @Override
            public void run() {
                try {
                    if (load.load()) {
                        listener.trigger(load.getResourceId(), load);
                    }
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, "Failed to update remote resource: " + load.getResourceId(), e);
                }
            }
        };
    }

    @Override
    public String toString() {
        return OkHttpScheduler.class.getName() + '{' + " timer: "
                + timer + '}';
    }
}

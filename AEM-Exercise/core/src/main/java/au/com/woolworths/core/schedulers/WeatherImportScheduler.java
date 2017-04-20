/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package au.com.woolworths.core.schedulers;

import static java.lang.String.format;

import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.woolworths.core.services.WeatherForcastImporter;

/**
 * A simple demo for cron-job like tasks that get executed regularly.
 * It also demonstrates how property values can be set. Users can
 * set the property values in /system/console/configMgr
 */
@Component(metatype = true, label = "Weather Forecast Scheduler Task", description = "Imports Forecasts into repository on a scheduled basis")
@Service(value = Runnable.class)
@Properties({
    @Property(name = "scheduler.expression", value = "0 1/15 * * * ?", description = "Cron-job expressions"),
    @Property(name = "scheduler.concurrent", boolValue=false, description = "Whether or not to schedule this task concurrently")
})
public class WeatherImportScheduler implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Reference
    private WeatherForcastImporter importer;

    @Override
    public void run() {
        try {
            logger.info("{} has started", getClass().getSimpleName());
            importer.importForecast();
        } finally {
            logger.info("{} has completed", getClass().getSimpleName());
        }
    }

    @Activate
    protected void activate(final Map<String, Object> config) {
        logger.debug(format("%s activated", getClass().getSimpleName()));
    }

}

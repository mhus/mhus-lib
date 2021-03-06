/**
 * Copyright (C) 2002 Mike Hummel (mh@mhus.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.lib.logging.adapters;

import java.io.Serializable;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;

import de.mhus.lib.core.logging.Log;

public class Log4JAppender extends AbstractAppender {

    public Log4JAppender(
            String name,
            Filter filter,
            Layout<? extends Serializable> layout,
            boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions, Property.EMPTY_ARRAY);
    }

    public Log4JAppender(String name, Filter filter, Layout<? extends Serializable> layout) {
        super(name, filter, layout, true, Property.EMPTY_ARRAY);
    }

    @Override
    public void append(LogEvent evt) {
        Level level = evt.getLevel();
        String loggerName = evt.getLoggerName();
        Object msg = evt.getMessage();
        Throwable t = evt.getThrown();

        Log logger = Log.getLog(loggerName);

        StackTraceElement location = evt.getSource();
        String method =
                location.getClassName()
                        + "."
                        + location.getMethodName()
                        + ":"
                        + location.getLineNumber();

        int l = level.intLevel();
        if (l == Level.FATAL.intLevel()) logger.f(method, msg, t);
        else if (l == Level.ERROR.intLevel()) logger.e(method, msg, t);
        else if (l == Level.WARN.intLevel()) logger.w(method, msg, t);
        else if (l == Level.INFO.intLevel()) logger.i(method, msg, t);
        else if (l == Level.DEBUG.intLevel()) logger.d(method, msg, t);
        else if (l == Level.TRACE.intLevel()) logger.t(method, msg, t);
        else logger.t(method, msg, t);
    }
    //
    //    @Override
    //    public void close() {}
    //
    //    @Override
    //    public boolean requiresLayout() {
    //        return false;
    //    }
    //
    //    public static void configure() {
    //
    //        Log4JAppender appender = new Log4JAppender();
    //        appender.setThreshold(Level.ALL);
    //        appender.setName("mlog2log4j");
    //        appender.activateOptions();
    //
    //        Logger.getRootLogger().addAppender(appender);
    //    }
}

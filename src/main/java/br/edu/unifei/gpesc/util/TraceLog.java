/*
 * Copyright (C) 2016 - GEPESC - Universidade Federal de Itajuba
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package br.edu.unifei.gpesc.util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author isaac
 */
public class TraceLog {

    private static final Logger LOGGER = Logger.getLogger("SAS_LOG");

    public static void setLogFile(String logFile) {
        try {
            FileHandler fileHandler = new FileHandler(logFile);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
        }
        catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Creating logger", ex);
        }
    }

    public static void setConsoleLogEnable(boolean enable) {
        LOGGER.setUseParentHandlers(enable);
    }

    public static void logE(Throwable t) {
        logE("", t);
    }

    public static void logE(String msg, Throwable t) {
        LOGGER.log(Level.SEVERE, msg, t);
    }

    public static void logW(Throwable t) {
        LOGGER.log(Level.WARNING, "", t);
    }

    public static void logD(String msg) {
        LOGGER.log(Level.INFO, msg);
    }
}

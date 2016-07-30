/*
 * Copyright (C) 2015 - GPESC - Universidade Federal de Itajuba
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
package br.edu.unifei.gpesc.app;

import java.util.Properties;
import java.util.ResourceBundle;

/**
 * This class manages all internationalization string resources and provides
 * convenient print methods.
 *
 * This Messages is an Adapter of {@link br.edu.unifei.gpesc.util.Messages}.
 *
 * @author Isaac Caldas Ferreira
 */
public final class Messages {

    /**
     * The Resource Bundle for this application.
     */
    private static final br.edu.unifei.gpesc.util.Messages sMessages;

    /**
     * Static initializer.
     */
    static {
        ResourceBundle bundle = ResourceBundle.getBundle("messages");

        // copy all properties
        Properties p = new Properties();
        for (String key : bundle.keySet()) {
            p.put(key, bundle.getString(key));
        }

        // share
        sMessages = new br.edu.unifei.gpesc.util.Messages(p);
    }

    /**
     * This method is used to get an internationalization string. <br>
     * All strings messages are in src/main/resources/messages.properties.
     * @param key The key for the message string.
     * @return The internationalization string message.
     */
    public static String i18n(String key) {
        return sMessages.i18n(key);
    }

    /**
     * Same of {@link Messages#i18n(String)} with extra arguments.
     * @param key The key for the message string.
     * @param args The extra arguments.
     * @return The internationalization string message.
     */
    public static String i18n(String key, Object... args) {
        return String.format(i18n(key), args);
    }

    /**
     * This method prints an internationalization string with line end
     * on console using {@link System#out}.
     * @param key The key for the message string.
     * @param args The extra arguments.
     */
    public static void printlnLog(String key, Object... args) {
        System.out.println(i18n(key, args));
    }

    /**
     * This method prints an internationalization string  on console using
     * {@link System#out}.
     * @param key The key for the message string.
     * @param args The extra arguments.
     */
    public static void printLog(String key, Object... args) {
        System.out.print(i18n(key, args));
    }

    /**
     * Has the same effect of calling System.out.print(String).
     * @param text The string to be printed.
     */
    public static void print(String text) {
        System.out.print(text);
    }

    /**
     * Has the same effect of calling System.out.println(String).
     * @param text The string to be printed.
     */
    public static void println(String text) {
        System.out.println(text);
    }
}

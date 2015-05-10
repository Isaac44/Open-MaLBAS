/*
 * Copyright (C) 2015 Universidade Federal de Itajuba
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

import java.util.ResourceBundle;

/**
 * This class manages all auxiliary resources that this program needs.
 * <p> It contains:
 * <br><b> String internationalization; </b>
 *
 *
 * @author isaac
 */
public final class Messages {

    private static ResourceBundle sResourceBundle;

    public static String i18n(String key) {
        if (sResourceBundle == null) sResourceBundle = ResourceBundle.getBundle("messages");
        return sResourceBundle.getString(key);
    }

    public static String log(String key, Object... args) {
        return String.format(i18n(key), args);
    }

    public static void printlnLog(String key, Object... args) {
        System.out.println(log(key, args));
    }

    public static void printLog(String key, Object... args) {
        System.out.print(log(key, args));
    }

    public static void print(String text) {
        System.out.print(text);
    }

    public static void println(String text) {
        System.out.println(text);
    }
}

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

import static br.edu.unifei.gpesc.app.Messages.i18n;
import br.edu.unifei.gpesc.util.FileUtils;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Isaac Caldas Ferreira
 */
public class Configuration {

    private final Properties mConfigurations = new Properties();

    private Configuration() {
        loadConfigs();
    }

    private void loadConfigs() {
        File folder = new File("config");
        File[] files = folder.listFiles(
                new FileUtils.ExtensionFilter(".properties"));

        if (files != null) {
            FileReader reader;
            for (File file : files) {
                try {
                    reader = new FileReader(file);
                    mConfigurations.load(reader);
                    reader.close();
                }
                catch (IOException ignore) {}
            }
        }
    }

    public String getProperty(String key) {
        String value = mConfigurations.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException(i18n("Exception.MissingParameter", key));
        } else {
            return value;
        }
    }

    public int getIntegerProperty(String key) {
        return Integer.parseInt(getProperty(key));
    }

    public double getDoublePropertie(String key) {
        return Double.parseDouble(getProperty(key));
    }

    public String getProperty(String key, String defaultValue) {
        return mConfigurations.getProperty(key, defaultValue);
    }

    public int getIntegerProperty(String key, int defaultValue) {
        String value = mConfigurations.getProperty(key);
        return (value != null) ? Integer.parseInt(value) : defaultValue;
    }

    public double getDoubleProperty(String key, double defaultValue) {
        String value = mConfigurations.getProperty(key);
        return (value != null) ? Double.parseDouble(value) : defaultValue;
    }

    private static Configuration sInstance;

    public static Configuration getInstance() {
        if (sInstance == null) sInstance = new Configuration();
        return sInstance;
    }
}

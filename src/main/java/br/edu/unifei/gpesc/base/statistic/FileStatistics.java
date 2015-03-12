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
package br.edu.unifei.gpesc.base.statistic;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 *
 * @author Isaac Caldas Ferreira
 */
public class FileStatistics extends Statistics<String> {

    /**
     * Creates a File Statistic with a determined number of sets.
     * @param numberOfSets The number of sets. If the sets are separated into
     * folders, then this is the number of directories.
     */
    public FileStatistics(int numberOfSets) {
        super(numberOfSets);
    }

    /**
     * Process a text file considering each word as a statistical data.
     * @param file The file to process.
     * @param charsetName The charset of the file.
     * @param set The set to which this file belongs.
     */
    public void processFile(File file, String charsetName, int set) {
        try {
            Scanner scanner = new Scanner(file, charsetName);
            incrementSetSize(set);

            while (scanner.hasNext()) {
                insertData(scanner.next(), set);
            }
        }
        catch (FileNotFoundException e) {}
    }

    /**
     * Process a text file considering each word as a statistical data.
     * Equivalent to call processFile(java.io.File, "ASCII", int)}.
     * @param file The file to process.
     * @param set The set to which this file belongs.
     * @see FileStatistic#processFile(java.io.File, java.lang.String, int)
     */
    public void processFile(File file, int set) {
        processFile(file, "ASCII", set);
    }
}

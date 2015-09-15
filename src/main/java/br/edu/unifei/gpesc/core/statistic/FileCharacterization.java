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
package br.edu.unifei.gpesc.core.statistic;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 *
 * @author isaac
 */
public class FileCharacterization extends Characterization<String> {

    /**
     *
     * @param statisticalVector
     */
    public FileCharacterization(Characteristics<String> statisticalVector) {
        super(statisticalVector);
    }

    /**
     *
     * @param file The file to process.
     * @param charsetName The charset of the file.
     */
    public void processFile(File file, String charsetName) {
        try {
            Scanner scanner = new Scanner(file, charsetName);
            while (scanner.hasNext()) {
                insertData(scanner.next());
            }
            scanner.close();
        }
        catch (FileNotFoundException e) {}
    }

    /**
     * Process a text file considering each word as a statistical data.
     * Equivalent to call {@link FileStatisticalCharacterization#processFile(File, "ASCII")}.
     * @param file The file to process.
     * @see FileStatisticalCharacterization#processFile(File, String).
     */
    public void processFile(File file) {
        processFile(file, "ASCII");
    }

}

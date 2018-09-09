/*
 * Copyright (C) 2017 - GEPESC - Universidade Federal de Itajuba
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author Isaac C. Ferreira
 */
public class CharacteristicsHelper {

    public static Characteristics<String> fromFile(File file, int quantity) {
        Scanner scanner = new Scanner("");

        try {
            scanner = new Scanner(new BufferedInputStream(new FileInputStream(file)));
            scanner.nextLine(); // ignore the method

            Characteristics<String> characteristics = new Characteristics<String>();

            if (quantity == -1) {
                while (scanner.hasNext()) {
                    characteristics.insertData(scanner.next());
                    scanner.nextLine();
                }
            } else {
                for (int i=0; i < quantity; i++) {
                    if (scanner.hasNext()) {
                        characteristics.insertData(scanner.next());
                        scanner.nextLine();
                    } else {
                        break;
                    }
                }
            }

            return characteristics;
        }
        catch (IOException e) {
            return null;
        }
        finally {
            scanner.close();
        }
    }

}

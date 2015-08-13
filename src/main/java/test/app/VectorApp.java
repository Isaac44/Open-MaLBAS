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
package test.app;

import br.edu.unifei.gpesc.sas.modules.NeuralVector;
import br.edu.unifei.gpesc.statistic.StatisticalCharacteristic;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author isaac
 */
public class VectorApp {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        StatisticalCharacteristic<String> characteristic = new StatisticalCharacteristic<String>();

        String path = "/home/isaac/Unifei/Mestrado/SAS/Mail_Test/Febuary/";
        Scanner scanner = new Scanner(new File(path, "clean/statistics.txt"));
        for (int i=0; i<2000; i++) {
            characteristic.insertData(scanner.next());
            scanner.nextLine();
        }

        System.out.println("count="+characteristic.getObjectCount());

        NeuralVector.folderVectorization(characteristic, new File(path, "clean/ham"), new File(path, "vector/ham"), NeuralVector.HAM, false);
        NeuralVector.folderVectorization(characteristic, new File(path, "clean/spam"), new File(path, "vector/spam"), NeuralVector.SPAM, false);

    }

}

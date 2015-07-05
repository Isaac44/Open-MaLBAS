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
package test.mlp;

import br.edu.unifei.gpesc.neural.mlp3.train.DualLayer;
import br.edu.unifei.gpesc.neural.mlp3.train.FirstLayer;
import br.edu.unifei.gpesc.neural.mlp3.train.TrainMlp;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Scanner;

/**
 *
 * @author Isaac Caldas Ferreira
 */
public class Test3 {

    private static DualLayer[] load(File file, int inLen, int outLen) throws FileNotFoundException {
        Scanner scan = new Scanner(file);
        scan.useLocale(Locale.US);

        LinkedList<DualLayer> dualList = new LinkedList<DualLayer>();
        double[] inArray = new double[inLen];
        double[] outArray = new double[outLen];

        int count = 0;

        while (scan.hasNext()) {
            for (int i=0; i<inLen; i++) {
                inArray[i] = scan.nextDouble();
            }

            for (int i=0; i<outLen; i++) {
                outArray[i] = scan.nextDouble();
            }

            FirstLayer inputLayer = new FirstLayer(inArray);
            FirstLayer outputLayer = new FirstLayer(outArray);

            dualList.add(new DualLayer(inputLayer, outputLayer));
        }

        DualLayer[] array = new DualLayer[dualList.size()];
        return dualList.toArray(array);
    }

    public static void main(String[] args) throws FileNotFoundException {
        TrainMlp mlp = new TrainMlp(10, 10, 12, 2);

        String path = "/home/isaac/Unifei/CodeStation/AntiSpamMestrado/neural/dat/";

        DualLayer[] trainInput = load(new File(path, "tpat.dat"), 10, 2);
        DualLayer[] validationInput = load(new File(path, "vpat.dat"), 10, 2);

        mlp.setInputArray(trainInput);
        mlp.setValidationArray(validationInput);


        mlp.trainByEpoch();

    }

}

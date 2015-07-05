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

package test.auxiliar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Isaac Caldas Ferreira
 */
public class StatisticsSampleMaker {

    public static void createStatisticsFile(File file, String[] dataArray, int[] maxObjectArray) throws FileNotFoundException, IOException {
        FileWriter fileWriter = new FileWriter(file);

        int quantity;
        for (int i=0; i<dataArray.length; i++) {
            quantity = (int) (Math.random() * maxObjectArray[i]);
            if (quantity > 0) {
                fileWriter.append("\n");
                for (int k=0; k<quantity; k++) {
                    fileWriter.append(dataArray[i]).append(" ");
                }
            }
        }
        fileWriter.close();
    }

    public static void createStatisticsFileInFolder(File folder, int quantity, String[] dataArray, int[] maxObjectArray) throws IOException {
        int maxDigits = (int) (Math.log(quantity) / Math.log(16)) + 1;
        String baseName = "test_statistic_%0" + maxDigits + "x.eml";

        if (!folder.exists()) folder.mkdir();

        for (int i=0; i<quantity; i++) {
            createStatisticsFile(new File(folder, String.format(baseName, i)), dataArray, maxObjectArray);
        }
    }

    public static void main(String[] args) throws IOException {
        final String[] dataArray = { "macaco", "ferragem", "conselho", "serra", "aparelho", "trem", "banda", "listras", "ovelha" };
//        final String[] dataArray = { "macaco", "bando", "capela", "grupo", "atlas", "maquina", "marinheiro", "medico", "ferragem", "conselho", "cordilheira", "serra", "serrania", "aparelho", "trem", "banda", "charanga", "filarmonica", "orquestra", "lista", "onda", "esqueleto", "ovelha"};

        String rootFolderPath = "/home/isaac/Unifei/Mestrado/SAS/Statistics/MethodsData2/";

        // distribution
        int len = dataArray.length;
        int max = 30;
        int decai = (int) ((max / (float) len) + 0.5f);

        int[] hamDistribution = new int[len];
        int[] spamDistribution = new int[len];

        int value = max;

        for (int i=0; i<dataArray.length; i++) {
            hamDistribution[i] = value;
            spamDistribution[len-i-1] = value;
            value -= decai;
        }

        createStatisticsFileInFolder(new File(rootFolderPath, "ham") , 20, dataArray, hamDistribution);
        createStatisticsFileInFolder(new File(rootFolderPath, "spam"), 20, dataArray, spamDistribution);

    }

}

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

    public static void createStatisticsFile(File file, String[] dataArray, int maxObjectData) throws FileNotFoundException, IOException {
        FileWriter fileWriter = new FileWriter(file);

        int quantity;
        for (String data : dataArray) {
            quantity = (int) (Math.random() * maxObjectData);
            if (quantity > 0) {
                fileWriter.append("\n");
                for (int i=0; i<quantity; i++) {
                    fileWriter.append(data).append(" ");
                }
            }
        }
//        fileWriter.flush();
        fileWriter.close();
    }

    public static void createStatisticsFileInFolder(File folder, int quantity, String[] dataArray, int maxObjectData) throws IOException {
        String baseName = "test_statistic_%x.eml";//+(quantity/10)+"d.eml";

        for (int i=0; i<quantity; i++) {
            createStatisticsFile(new File(folder, String.format(baseName, i)), dataArray, maxObjectData);
        }
    }

    public static void main(String[] args) throws IOException {
        final String[] daraArray = { "macaco", "bando", "capela", "grupo", "atlas", "maquina", "marinheiro", "medico", "ferragem", "conselho", "cordilheira", "serra", "serrania", "aparelho", "trem", "banda", "charanga", "filarmonica", "orquestra", "lista", "onda", "esqueleto", "ovelha"};

        String rootFolderPath = "/home/isaac/Unifei/Mestrado/SAS/Statistics/DataSample/";

        createStatisticsFileInFolder(new File(rootFolderPath, "ham"), 20000, daraArray, 20);
        createStatisticsFileInFolder(new File(rootFolderPath, "spam"), 20000, daraArray, 20);

    }

}

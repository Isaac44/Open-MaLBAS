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

import br.edu.unifei.gpesc.sas.modules.NeuralCharacteristic;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 *
 * @author isaac
 */
public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        String basePath = "/home/isaac/Unifei/CodeStation/SistemaAntiSPAM_SAS/NN-Otavio/dat/";
        File statisticsFile = new File("/home/isaac/Unifei/Mestrado/SAS/Mail_Test/Febuary/clean/", "statistics.txt");
        ProcessMail processMail = new ProcessMail(basePath + "data.dat", basePath + "wfin.dat", statisticsFile);

        while (true) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter the path to the file: ");
            File mailFile = new File(scanner.nextLine());

            if (mailFile.isFile()) {
                NeuralCharacteristic result = processMail.processMail(mailFile);
                System.out.println("Result: "+result.NAME);
            }
            else {
                System.out.println("ERROR: The path is not valid.");
            }
        }
    }

}

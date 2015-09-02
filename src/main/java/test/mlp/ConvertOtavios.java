/*
 * Copyright (C) 2015 - GEPESC - Universidade Federal de Itajuba
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

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author Isaac Caldas Ferreira
 */
public class ConvertOtavios {

    private void write(DataOutputStream outStream, String line) throws IOException {
        Scanner scan = new Scanner(line);
        double value;
        for (int i=0; i<10; i++) {
            value = scan.nextDouble();
            outStream.writeDouble(value);
        }
    }

    private void readAndWrite(int lines, File file, DataOutputStream first, DataOutputStream second) throws IOException {
        Scanner scanner = new Scanner(file);

        for (int i=0; i<lines; i++) {
            write(first, scanner.nextLine());
        }

        for (int i=0; i<lines; i++) {
            write(second, scanner.nextLine());
        }
    }

    private void process() throws IOException {
        String oldPath = "/home/isaac/Unifei/Mestrado/SAS/Mail_Test/September/otavio-nn/old/";
        String newPath = "/home/isaac/Unifei/Mestrado/SAS/Mail_Test/September/otavio-nn/otavio_test/";

        DataOutputStream first = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(new File(newPath, "spam"))));
        DataOutputStream second = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(new File(newPath, "ham"))));

        first.writeInt(250);
        first.writeInt(10);

        second.writeInt(250);
        second.writeInt(10);

        readAndWrite(250, new File(oldPath, "pat.dat"), first, second);
//        readAndWrite(125, new File(oldPath, "vpat.dat"), first, second);

        first.close();
        second.close();
    }

    public static void main(String[] args) throws IOException {
        new ConvertOtavios().process();
    }

}

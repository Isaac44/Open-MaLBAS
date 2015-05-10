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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * @author isaac
 */
public class ReplicateFile {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        int COPIES = 3;

        String path = "/home/isaac/Unifei/Mestrado/SAS/Mail_Test/Febuary/vector/";
        String name = "ham";

        File outFile = new File(path, name);
        File inFile = new File(path, name + ".old");

        outFile.renameTo(inFile);

        // copiar
        byte[] data = new byte[(int) inFile.length() + 100];

        FileInputStream inStream = new FileInputStream(inFile);
        int readed = inStream.read(data);
        inStream.close();

        FileOutputStream outStream = new FileOutputStream(outFile);

        int cp = 1;
        while (true) {
            outStream.write(data, 0, readed);

            if (cp < COPIES) outStream.write("\n".getBytes());
            else break;
            
            cp++;
        }


    }

}

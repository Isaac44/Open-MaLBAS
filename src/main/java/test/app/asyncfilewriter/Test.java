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
package test.app.asyncfilewriter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Isaac Caldas Ferreira
 */
public class Test {

    public static void main2(String[] args) throws FileNotFoundException, IOException {
        OutputStream out = new BufferedOutputStream(new FileOutputStream(new File("/home/isaac/Público/Test/myFile.txt")));

        for (int i=0; i<100000; i++) {
            System.out.println("i = " + i);
            out.write(String.valueOf(i).getBytes());
            out.write("\n".getBytes());
        }

        out.close();
    }

    public static void main(String[] args) throws InterruptedException, FileNotFoundException {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        AsyncWriter writer = new AsyncWriter(executor, new File("/home/isaac/Público/Test/myFile.txt"));


        long time;

        for (int i=0; i<100000; i++) {
//            System.out.println("i = " + i);
            writer.append(String.valueOf(i));
            writer.append("\n");

//            time = System.currentTimeMillis();
//            Thread.sleep(20);
//            System.out.println("time = " + (System.currentTimeMillis() - time));
        }


        executor.shutdown();
    }


}

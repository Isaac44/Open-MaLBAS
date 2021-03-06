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
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 *
 * @author Isaac Caldas Ferreira
 */
public class Test01 {

    private static double[][] loadVectors(File vectors) throws IOException {
        // open file
        FileChannel fileIn = new FileInputStream(vectors).getChannel();

        // load data
        int bufferLen = (int) vectors.length();
        ByteBuffer inBuffer = ByteBuffer.allocate(bufferLen);

        int readed = fileIn.read(inBuffer);

        if (readed != bufferLen) {
            throw new IOException("Not all data was readed.");
        }

        fileIn.close();
        inBuffer.flip(); // reset pointer

        // vector layers
        int quantityOfLayers = inBuffer.getInt();
        int vectorLength = inBuffer.getInt(); // quantity of features of the vector

        double[][] layers = new double[quantityOfLayers][vectorLength];

        for (double[] layer : layers) {
            for (int k = 0; k < layer.length; k++) {
                layer[k] = inBuffer.getDouble();
            }
        }

        return layers;
    }

    public static void main(String[] args) throws IOException {
        String path = "/home/isaac/Unifei/Mestrado/SAS/Mail_Test/August/vectors/vector_chi2_500/";

        double[][] spamLayers = loadVectors(new File(path, "spam"));
        double[][] hamLayers = loadVectors(new File(path, "ham"));

        System.out.println("length = " + spamLayers.length);
        System.out.println("length = " + hamLayers.length);
    }

}

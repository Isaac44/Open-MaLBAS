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
package test.mlp;

import br.edu.unifei.gpesc.core.neural.NeuralModule;
import br.edu.unifei.gpesc.mlp.Mlp;
import br.edu.unifei.gpesc.util.Configuration;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Comparator;

/**
 *
 * @author Isaac C. Ferreira
 */
public class Benchmark {

    private static Writer mWriter;
    private static final String[] HH = {"5", "10", "20", "30", "40", "50"};
    private static String mHH;

    private static Configuration getCfg(File vector) {
        Configuration c = new Configuration();
        c.put("WEIGHTS_FOLDER", vector.getAbsolutePath()); // where to save
        c.put("DEFAULT_FIRST_HIDDEN_FUNCTION", "TANSIG");
        c.put("DEFAULT_SECOND_HIDDEN_FUNCTION", "TANSIG");
        c.put("DEFAULT_OUTPUT_FUNCTION", "LOGSIG");

        c.put("DEFAULT_EPOCHS", "20");
        c.put("DEFAULT_MOMENTUM", "0.9");
        c.put("DEFAULT_LEARN_RATE", "0.00001");
        c.put("DEFAULT_RANDOMIZER_SEED", "0");
        c.put("NUMBER_OF_ACTIVES_THREADS", "1");

        c.put("VECTOR_FOLDER", vector.getAbsolutePath());
        c.put("PATTERN_TRAINING", "1");
        c.put("PATTERN_VALIDATION", "1");
        c.put("PATTERN_TEST", "0");

//        int half = Integer.parseInt(vector.getName()) / 2;
//        String strHalf = String.valueOf(half);

        c.put("TRAIN_FIRST_HIDDEN_LAYER_VALUES", mHH);
        c.put("TRAIN_SECOND_HIDDEN_LAYER_VALUES", "2");

        return c;
    }

    private static void train(File vector) throws IOException {
        long time;

        time = System.currentTimeMillis();
        NeuralModule nm = new NeuralModule(getCfg(vector));
        nm.setUp();
        mWriter.append(vector.getName()).append(" setup ").append(String.valueOf((System.currentTimeMillis() - time))).append("\n");

        time = System.currentTimeMillis();
        nm.start2();
        mWriter.append(vector.getName()).append(" train ").append(String.valueOf((System.currentTimeMillis() - time))).append("\n");
    }

    private static void test(File vector) throws IOException {
        double[][] hams = open(new File(vector, "ham"));
        double[][] spams = open(new File(vector, "ham"));

        Mlp mlp = Mlp.loadMlp(new File(vector, "train_1.dat"));

        long time = System.currentTimeMillis();

        for (double[] ham : hams) {
            mlp.runTestNonSup(ham);
        }

        for (double[] spam : spams) {
            mlp.runTestNonSup(spam);
        }

        mWriter.append(vector.getName()).append(" test ").append(String.valueOf((System.currentTimeMillis() - time))).append("\n");
    }

    private static void benchmark(File folder) throws IOException {
        File[] files = folder.listFiles();
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        for (String hh : HH) {
            System.out.println("hidden = " + hh);
            mHH = hh;
            mWriter = new BufferedWriter(new FileWriter(new File(folder, hh + "_2.txt")));

            for (File vector : files) {
                if (vector.isDirectory()) {
                    train(vector);
                    test(vector);
                    mWriter.append("\n");
                }
            }

            mWriter.close();
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("usage: path = ");
            return;
        }
//        String path = "/home/isaac/Unifei/Mestrado/Resultados/AnaliseSAS/NeuralSpeed/FD";
        String path = args[0];
        benchmark(new File(path));
    }

    // ---------------------------------------------------------------------------------------------
    // Open
    // ---------------------------------------------------------------------------------------------

    public static double[][] open(File vector) throws IOException {
        // open file
        FileChannel fileIn = new FileInputStream(vector).getChannel();

        // load data
        int bufferLen = (int) vector.length();
        ByteBuffer inBuffer = ByteBuffer.allocate(bufferLen);

        int readed = fileIn.read(inBuffer);

        if (readed != bufferLen) {
            throw new IOException("Not all data was readed.");
        }

        fileIn.close();
        inBuffer.flip(); // reset pointer

        // create input layer
        int quantity = inBuffer.getInt();

        double[][] vectors = new double[100][inBuffer.getInt()];

        for (int i = 0; i < quantity; i++) {
            for (int k = 0; k < vectors[i].length; k++) {
                vectors[i][k] = inBuffer.getDouble();
            }
        }

        return vectors;
    }

}

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
package br.edu.unifei.gpesc.core.modules;

import br.edu.unifei.gpesc.core.statistic.*;
import br.edu.unifei.gpesc.mlp.layer.*;
import br.edu.unifei.gpesc.util.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Scanner;

/**
 * Beta class.
 * @author isaac
 */
public class Vector {

    public static final String HAM = NeuralCharacteristic.HAM.STR_VALUE;
    public static final String SPAM = NeuralCharacteristic.SPAM.STR_VALUE;

    public static int[] getVectorArray(Characteristics<String> characteristic, String fileText) {
        Characterization<String> characterization = new Characterization<String>(characteristic);
        int[] dataVector = characterization.getCharacterizationArray();
        double[] dataNormalized = new double[dataVector.length];

        Scanner scanner = new Scanner(fileText);
        while (scanner.hasNext()) {
            characterization.insertData(scanner.next());
        }

        Normalization.featureScaling(dataVector, dataNormalized);

        for (int i=0; i<dataVector.length; i++) {
            dataVector[i] = (int) (dataNormalized[i] * 10E12);
        }

        return dataVector;
    }

    public static void folderVectorization(Characteristics<String> characteristic, File folder, File output, String set, boolean append) throws IOException  {
        File[] fileArray = folder.listFiles(new FileUtils.IsFileFilter());
        if (fileArray != null) {

            BufferedWriter writer = new BufferedWriter(new FileWriter(output, append));
            FileCharacterization characterization = new FileCharacterization(characteristic);

            int[] dataVector = characterization.getCharacterizationArray();
            double[] dataNormalized = new double[dataVector.length];

            int i;
            for (i=0; i<fileArray.length-1; i++) {
                characterization.processFile(fileArray[i]);

                if (hasOnlyZeros(dataVector)) {
                    System.out.println("vetor zerado");
                    continue;
                }

                Normalization.featureScaling(dataVector, dataNormalized);

                for (double value : dataNormalized) {
                    writer.append(Integer.toString((int) (value * 10E12)));
                    writer.append(" ");
                    //writer.append(String.format(Locale.US, "%.12f", value)).append(" ");
                }
                writer.append(set);
                writer.append("\n");
            }

            //last
            characterization.processFile(fileArray[i]);

                if (hasOnlyZeros(dataVector)) {
                    System.out.println("vetor zerado");
                }

                Normalization.featureScaling(dataVector, dataNormalized);

                for (double value : dataNormalized) {
                    writer.append(Integer.toString((int) (value * 10E12)));
                    writer.append(" ");
//                    writer.append(String.format(Locale.US, "%.12f", value)).append(" ");
                }
                writer.append(set);
            // endlast

            writer.close();
        }
    }

    public static VectorCounter doVectorization(Characteristics<String> characteristic, File folderInput, File outFile) throws IOException  {
        File[] fileArray = folderInput.listFiles(new FileUtils.IsFileFilter());
        VectorCounter processLog = new VectorCounter();

        if (fileArray != null) {
            FileOutputStream fileStream = new FileOutputStream(outFile);
            FileChannel fileOut = fileStream.getChannel();

            Writer writeLink = new BufferedWriter(new FileWriter(new File(outFile.getAbsolutePath() + ".link")));

            FileCharacterization characterization = new FileCharacterization(characteristic);

            int[] dataVector = characterization.getCharacterizationArray();
            double[] dataNormalized = new double[dataVector.length];

            // allocate the output buffer
            // -> 2 Integers + (n files * vector size) Double
            int bufferSize = 2 * Double.BYTES + fileArray.length * dataVector.length * Double.BYTES;

            ByteBuffer outBuffer = ByteBuffer.allocate(bufferSize);
            outBuffer.putInt(0); // reserved: quantity of valid vectors
            outBuffer.putInt(dataVector.length); // line size (quantity of indexes of the vector array)

            ConsoleProgress progress = ConsoleProgress.getGlobalInstance(fileArray.length);
            int k = 0;

            for (File file : fileArray) {
                progress.setValue(k++);

                characterization.cleanCharacterizationArray();
                characterization.processFile(file);

                if (!hasOnlyZeros(dataVector)) {
                    Normalization.featureScaling(dataVector, dataNormalized);

                    for (double value : dataNormalized) {
                        outBuffer.putDouble(value);
                    }

                    writeLink.append(file.getName()).append("\n");
                    processLog.incGoodVectorsCount();
                }
                else {
                    processLog.incZeroedVectorsCount();
                }
            }

            // quantity of valid patterns
            outBuffer.putInt(0, processLog.getGoodVectorsCount());
            outBuffer.flip();

            fileOut.write(outBuffer);
            fileOut.close();
            fileStream.close();

            writeLink.close();

            progress.end();
        }
        return processLog;
    }

    public static double[] getVector(Characteristics<String> characteristics, String text ){
        Characterization<String> characterization = new Characterization<String>(characteristics);
        int[] dataVector = characterization.getCharacterizationArray();
        double[] dataNormalized = new double[dataVector.length];

        Scanner scanner = new Scanner(text);
        while (scanner.hasNext()) {
            characterization.insertData(scanner.next());
        }

        Normalization.featureScaling(dataVector, dataNormalized);

        return dataNormalized;
    }

    public static VectorCounter simulateVectorization(Characteristics<String> characteristic, File folderInput) throws IOException  {
        File[] fileArray = folderInput.listFiles(new FileUtils.IsFileFilter());
        VectorCounter result = new VectorCounter();

        if (fileArray != null) {
            FileCharacterization characterization = new FileCharacterization(characteristic);

            int[] rawVector = characterization.getCharacterizationArray();
            double[] normVector = new double[rawVector.length];

            // allocate the output buffer
            // -> 2 Integers + (n files * vector size) Double
            int bufferSize = 2 * Double.BYTES + fileArray.length * rawVector.length * Double.BYTES;

            ByteBuffer outBuffer = ByteBuffer.allocate(bufferSize);
            outBuffer.putInt(0); // reserved: quantity of valid vectors
            outBuffer.putInt(rawVector.length); // line size (quantity of indexes of the vector array)

            ConsoleProgress progress = ConsoleProgress.getGlobalInstance(fileArray.length);
            int k = 0;

            for (File file : fileArray) {
                progress.setValue(k++);

                characterization.cleanCharacterizationArray();
                characterization.processFile(file);

                if (!hasOnlyZeros(rawVector)) {
                    Normalization.featureScaling(rawVector, normVector);

                    for (double value : normVector) {
                        outBuffer.putDouble(value);
                    }

                    result.incGoodVectorsCount();
                }
                else {
                    result.incZeroedVectorsCount();
                }

                // Zeros count
                result.addZeroesCount(countZeros(rawVector));

            }

            // quantity of valid patterns
            outBuffer.putInt(0, result.getGoodVectorsCount());
            outBuffer.flip();

            progress.end();
        }
        return result;
    }

    // -------------------------------------------------------------------------

    /**
     * SPAM: 1 0
     * HAM: 0 1
     * @param patternFile
     * @param outNeuron1
     * @param outNeuron2
     * @return
     * @throws IOException
     */
    public static PatternLayer[] loadNeurons(File patternFile, double outNeuron1, double outNeuron2) throws IOException {
        FileChannel fileIn = new FileInputStream(patternFile).getChannel();
        ByteBuffer inBuffer = ByteBuffer.allocate((int) fileIn.size());
        fileIn.read(inBuffer);

        // create neurons
        inBuffer.flip();
        int quantityOfVectors = inBuffer.getInt();
        int vectorLength = inBuffer.getInt();

        double[] vectorArray = new double[vectorLength];

        // neurons
        PatternLayer[] patternArray = new PatternLayer[quantityOfVectors];
        NeuronLayer outputLayer = new NeuronLayer(new double[] {outNeuron1, outNeuron2});

        for (int k=0; k<quantityOfVectors; k++) {

            for (int i=0; i<vectorArray.length; i++) {
                vectorArray[i] = inBuffer.getDouble();
            }

            patternArray[k] = new PatternLayer(new NeuronLayer(vectorArray), outputLayer);

        }

        return patternArray;
    }

    private static boolean hasOnlyZeros(int[] array) {
        for (int value : array) {
            if (value != 0) return false;
        }
        return true;
    }

    private static int countZeros(int[] array) {
        int count = 0;
        for (int value : array) {
            if (value == 0) {
                count++;
            }
        }
        return count;
    }
}

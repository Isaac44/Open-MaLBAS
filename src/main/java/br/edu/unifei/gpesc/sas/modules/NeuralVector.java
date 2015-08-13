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
package br.edu.unifei.gpesc.sas.modules;

import br.edu.unifei.gpesc.neural.mlp3.train.NeuronLayer;
import br.edu.unifei.gpesc.neural.mlp3.train.PatternLayer;
import br.edu.unifei.gpesc.statistic.FileStatisticalCharacterization;
import br.edu.unifei.gpesc.statistic.Normalization;
import br.edu.unifei.gpesc.statistic.StatisticalCharacteristic;
import br.edu.unifei.gpesc.statistic.StatisticalCharacterization;
import br.edu.unifei.gpesc.util.ConsoleProgress;
import br.edu.unifei.gpesc.util.FileUtils;
import br.edu.unifei.gpesc.util.ProcessLog;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Scanner;

/**
 * Beta class.
 * @author isaac
 */
public class NeuralVector {

    public static final String HAM = NeuralCharacteristic.HAM.STR_VALUE;
    public static final String SPAM = NeuralCharacteristic.SPAM.STR_VALUE;

    public static int[] getVectorArray(StatisticalCharacteristic<String> characteristic, String fileText) {
        StatisticalCharacterization<String> characterization = new StatisticalCharacterization<String>(characteristic);
        int[] dataVector = characterization.getStatisticalCharacterizationArray();
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

    public static void folderVectorization(StatisticalCharacteristic<String> characteristic, File folder, File output, String set, boolean append) throws IOException  {
        File[] fileArray = folder.listFiles(FileUtils.getFileFilter());
        if (fileArray != null) {

            BufferedWriter writer = new BufferedWriter(new FileWriter(output, append));
            FileStatisticalCharacterization characterization = new FileStatisticalCharacterization(characteristic);

            int[] dataVector = characterization.getStatisticalCharacterizationArray();
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

    public static ProcessLog doVectorization(StatisticalCharacteristic<String> characteristic, File folderInput, File outFile) throws IOException  {
        File[] fileArray = folderInput.listFiles(FileUtils.getFileFilter());
        ProcessLog processLog = new ProcessLog();

        if (fileArray != null) {
            FileChannel fileOut = new FileOutputStream(outFile).getChannel();

            FileStatisticalCharacterization characterization = new FileStatisticalCharacterization(characteristic);

            int[] dataVector = characterization.getStatisticalCharacterizationArray();
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
//                progress.setValue(k++);

                characterization.processFile(file);

                if (!hasOnlyZeros(dataVector)) {
                    Normalization.featureScaling(dataVector, dataNormalized);

                    for (double value : dataNormalized) {
                        outBuffer.putDouble(value);
                    }

                    processLog.incSucessCount();
                }
                else {
                    processLog.incErrorCount();
                }
            }

            outBuffer.putInt(0, processLog.sucess());
            outBuffer.flip();

            fileOut.write(outBuffer);
            fileOut.close();

            progress.end();
        }
        return processLog;
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

    public static boolean hasOnlyZeros(int[] array) {
        for (int value : array) {
            if (value != 0) return false;
        }
        return true;
    }
}

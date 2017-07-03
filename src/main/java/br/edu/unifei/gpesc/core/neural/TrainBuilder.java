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
package br.edu.unifei.gpesc.core.neural;

import br.edu.unifei.gpesc.core.modules.Spam;
import br.edu.unifei.gpesc.mlp.TrainMlp;
import br.edu.unifei.gpesc.mlp.layer.NeuronLayer;
import br.edu.unifei.gpesc.mlp.layer.NeuronLayer.Neuron;
import br.edu.unifei.gpesc.mlp.layer.PatternLayer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 *
 * @author Isaac Caldas Ferreira
 */
public class TrainBuilder {

    public static final NeuronLayer HAM = new NeuronLayer(Spam.HAM);
    public static final NeuronLayer SPAM = new NeuronLayer(Spam.SPAM);

    private PatternLayer[] mTraining;
    private PatternLayer[] mValidation;
    private PatternLayer[] mTest;

    /**
     * Use this class to build the MLP.
     *
     * It receives the ham and spam vectors and the quantity of those that will be used for Training
     * and for Validation.
     *
     * The remaining quantity, if any, will be used for Test. This means that if
     * <b> trainP + validP < 1 </b> than the result of <b> 1 - trainP + validP</b> will be the
     * percentual to be used for Test.
     *
     * @param hamVectors
     * @param spamVectors
     * @param trainP Training percentual.
     * @param validP Validation percentual.
     *
     * @throws IOException If any IO error occurs.
     */
    public TrainBuilder(File hamVectors, File spamVectors, double trainP, double validP) throws IOException {
        double testP = 1.0 - trainP - validP;

        if (testP < 0) {
            throw new IllegalArgumentException("Wrong percentage. trainP + validP must be <= 1");
        }

        createPatternLayers(hamVectors, spamVectors, trainP, validP);
    }

    public TrainMlp buildWith(int h1Len, int h2Len) {
        TrainMlp trainMlp = new TrainMlp(mTraining[0].inputLayer.getLength(), h1Len, h2Len, 2);
        trainMlp.setInputArray(mTraining);
        trainMlp.setValidationArray(mValidation);
        return trainMlp;
    }

    public PatternLayer[] getTestPatterns() {
        return mTest;
    }

    public static PatternLayer[] createPatterns(File vectors, NeuronLayer output) throws IOException {
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

        // create input layer
        PatternLayer[] layers = new PatternLayer[inBuffer.getInt()];
        double[] activations = new double[inBuffer.getInt()];

        for (int i=0; i<layers.length; i++) {

            // load neuron activation
            for (int k=0; k<activations.length; k++) {
                activations[k] = inBuffer.getDouble();
            }

            // add new pattern.
            layers[i] = new PatternLayer(new NeuronLayer(activations), output);
        }

        return layers;
    }

    private static PatternLayer[] replicate(PatternLayer[] array, int newSize) {
        // new array
        PatternLayer[] newArray = new PatternLayer[newSize];
        int offset = 0;

        // full copies
        int fullCopies = newSize / array.length;

        for (int k=0; k < fullCopies; k++) {
            System.arraycopy(array, 0, newArray, offset, array.length);
            offset += array.length;
        }

        // remain copies
        int remainCopies = newSize % array.length;
        System.arraycopy(array, 0, newArray, offset, remainCopies);

        // return
        return newArray;
    }

    private static PatternLayer[][] split(PatternLayer[] array, int trainLen, int validLen) {
        // arrays
        PatternLayer[] train = new PatternLayer[trainLen];
        PatternLayer[] valid = new PatternLayer[validLen];
        PatternLayer[] test  = new PatternLayer[array.length - trainLen - validLen];

        // copy
        System.arraycopy(array, 0,        train, 0, trainLen);
        System.arraycopy(array, trainLen, valid, 0, validLen);
        System.arraycopy(array, validLen, test,  0, test.length);

        // return
        return new PatternLayer[][] {train, valid, test};
    }

    public static PatternLayer[] merge(PatternLayer[] array1, PatternLayer[] array2) {
        // array
        PatternLayer[] arrayMerged = new PatternLayer[array1.length + array2.length];

        // merge
        System.arraycopy(array1, 0, arrayMerged, 0, array1.length);
        System.arraycopy(array2, 0, arrayMerged, array1.length, array2.length);

        // return
        return arrayMerged;
    }

    private void createPatternLayers(File hamVectors, File spamVectors, double trainP, double validP) throws IOException {
        /* Open Vectors and equalize the length between them */
        final int length;

        PatternLayer[] hamLayers = createPatterns(hamVectors, HAM);
        PatternLayer[] spamLayers = createPatterns(spamVectors, SPAM);

        // replicate
        if (spamLayers.length < hamLayers.length) {
            length = hamLayers.length;
            spamLayers = replicate(spamLayers, length);
        }
        else if (hamLayers.length < spamLayers.length) {
            length = spamLayers.length;
            hamLayers = replicate(hamLayers, spamLayers.length);
        }
        else {
            length = hamLayers.length; // equals
        }

        /* Split the pattern on train, validation and test  */
        int trainLen = (int) (length * trainP);
        int validLen = (int) (length * validP);

        PatternLayer[][] hamSplited = split(hamLayers, trainLen, validLen);
        PatternLayer[][] spamSplited = split(spamLayers, trainLen, validLen);

        // merge
        mTraining   = merge(spamSplited[0], hamSplited[0]);
        mValidation = merge(spamSplited[1], hamSplited[1]);
        mTest       = merge(spamSplited[2], hamSplited[2]);
    }

    private static void write(File file, PatternLayer[] layers) throws IOException {
        FileWriter writer = new FileWriter(file);
        for (PatternLayer layer : layers) {
            for (Neuron neuron : layer.inputLayer.getNeurons()) {
                writer.append(String.valueOf(neuron.activation));
                writer.append("\t");
            }
            for (Neuron neuron : layer.outputLayer.getNeurons()) {
                writer.append(String.valueOf(neuron.activation));
                writer.append("\t");
            }
            writer.append("\n");
        }
        writer.close();
    }

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
        String path = "/home/isaac/Unifei/Mestrado/SAS/Mail_Test/2015/August/vectors/vector_chi2_500/";
        TrainBuilder t = new TrainBuilder(new File(path, "ham"), new File(path, "spam"), 0.4, 0.2);
        TrainMlp mlp = t.buildWith(10, 10);
        mlp.runTrainByEpoch();
        mlp.saveMlp(new File(path, "500_nn"));

    }

}

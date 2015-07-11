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
package br.edu.unifei.gpesc.neural.mlp3.train;

import br.edu.unifei.gpesc.neural.mlp3.util.Function;
import br.edu.unifei.gpesc.neural.mlp3.util.LogSig;
import br.edu.unifei.gpesc.neural.mlp3.util.TanSig;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 *
 * @author Isaac Caldas Ferreira
 */
public abstract class Mlp {

    /**
     * The layer enumerator
     */
    public static enum Layer {

        /**
         * First hidden layer.
         */
        HIDDEN_1,

        /**
         * Second hidden layer.
         */
        HIDDEN_2,

        /**
         * Output layer
         */
        OUTPUT;
    }

    /**
     * The input layer.
     */
    protected final NeuronLayer mInputLayer;

    /**
     * The connections layers (hiddens and output)
     */
    protected final ConnectionLayer[] mLayerArray;

    /**
     * Creates a MLP.
     *
     * @param inLen The length of the input layer.
     * @param h1Len The length of the first hidden layer.
     * @param h2Len The length of the second hidden layer.
     * @param outLen The length of the output layer.
     */
    protected Mlp(int inLen, int h1Len, int h2Len, int outLen) {
        mInputLayer = new NeuronLayer(inLen);

        int h1 = Layer.HIDDEN_1.ordinal();
        int h2 = Layer.HIDDEN_2.ordinal();
        int out = Layer.OUTPUT.ordinal();

        mLayerArray = new ConnectionLayer[3];
        mLayerArray[h1] = new ConnectionLayer(h1Len, mInputLayer, new TanSig());
        mLayerArray[h2] = new ConnectionLayer(h2Len, mLayerArray[h1], new TanSig());
        mLayerArray[out] = new ConnectionLayer(outLen, mLayerArray[h2], new LogSig());
    }

    /**
     * Sets the transfer function to network layer.
     * @param layer The layer id.
     * @param function The transfer function.
     */
    public void setLayerFunction(Layer layer, Function function) {
        mLayerArray[layer.ordinal()].setFunction(function);
    }

    /**
     * Computes the activation (output) for all connection layers.
     */
    protected void computeActivationOutput() {
        for (ConnectionLayer layer : mLayerArray) {
            layer.computeActivationOutput();
        }
    }

    public void saveMlp(File file) throws IOException {
        FileChannel fileOut = new FileOutputStream(file).getChannel();

        // calculate the size of the file (optimization)
        int bufferSize = 0;
        for (ConnectionLayer layer : mLayerArray) {
            bufferSize += Integer.BYTES; // reserve for neurons size info
            bufferSize += layer.getLength() * Double.BYTES; // reserve for neurons
            bufferSize += layer.getConnectionsLength() * Double.BYTES; // reserve for matrix
        }

        // write to the file
        ByteBuffer outBuffer = ByteBuffer.allocate(bufferSize);

        // layers length
        for (ConnectionLayer layer : mLayerArray) {
            outBuffer.putInt(layer.getLength());
        }

        // neuron array and connection matrix
        for (ConnectionLayer layer : mLayerArray) {
            layer.toByteBuffer(outBuffer);
        }

        outBuffer.flip();
        fileOut.write(outBuffer);
        fileOut.close();
    }

    private static Mlp createMlp(File file, int which) throws IOException  {
        FileChannel fileIn = new FileInputStream(file).getChannel();

        ByteBuffer buffer = ByteBuffer.allocate((int) file.length());
        fileIn.read(buffer);

        Mlp mlp;

        int inLen = buffer.getInt();
        int h1Len = buffer.getInt();
        int h2Len = buffer.getInt();
        int outLen = buffer.getInt();

        switch (which) {
            case 1: mlp = new RunMlp(inLen, h1Len, h2Len, outLen); break;
            default: mlp = new TrainMlp(inLen, h1Len, h2Len, outLen);
        }

        for (ConnectionLayer layer : mlp.mLayerArray) {
            layer.loadFromByteBuffer(buffer);
        }

        return mlp;
    }

    public static RunMlp createRunMlp(File file) throws IOException  {
        return (RunMlp) createMlp(file, 1);
    }

    public static TrainMlp createTrainMlp(File file) throws IOException  {
        return (TrainMlp) createMlp(file, 0);
    }
}

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
package br.edu.unifei.gpesc.core.mlp;

import br.edu.unifei.gpesc.core.mlp.NeuronLayer.Neuron;
import br.edu.unifei.gpesc.neural.mlp.core.MlpTrain;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 *
 * @author Isaac Caldas Ferreira
 */
public class RunMlp extends Mlp {

    public RunMlp(int inLen, int h1Len, int h2Len, int outLen) {
        super(inLen, h1Len, h2Len, outLen);
    }


    /**
     *
     * @param patterns
     */
    public void runTestSup(PatternLayer[] patterns) {

        int npat;   // numero de um padrao de teste
        double perr;   // erro de um padrao de teste
        double errtot;   // erro total dos padroes de teste

        MlpTrain.Print output = new MlpTrain.Print();

        // Execucao dos padroes de teste na rede neural
        npat = 0;
        errtot = 0.0f;

        NeuronLayer inputLayer = mInputLayer;
        ConnectionLayer outputLayer = getOutputLayer();

        for (PatternLayer pattern : patterns) {
            npat++;

            inputLayer.setNeurons(pattern.inputLayer);
            computeActivationOutput();

            perr = outputLayer.getDifferenceTotal(pattern.outputLayer);
            errtot += perr;

            output.format("Padrao %d:\n", npat);
            for (NeuronLayer.Neuron neuron : pattern.inputLayer.mNeurons) {
                output.format("   %.4f", neuron.activation);
            }
            output.format("\n   ===> Saida Esperada:  ");
            for (NeuronLayer.Neuron neuron : pattern.outputLayer.mNeurons) {
                output.format("   %.4f", neuron.activation);
            }

            output.format("\n   ===> Saida Obtida:  ");
            for (NeuronLayer.Neuron neuron : outputLayer.mNeurons) {
                output.format("   %.4f", neuron.activation);
            }
            output.format("\n   ===> Erro do padrao de teste:  %.4f\n\n\n\n", perr);

        }

        System.out.println("Erro total = " + errtot);
    }

    /**
     * Runs a not supervisioned test.
     * @param neurons The input activation neurons.
     * @return The output array.
     */
    public double[] runTestNonSup(double[] neurons) {
        mInputLayer.setNeurons(new NeuronLayer(neurons));
        computeActivationOutput();

        Neuron[] outNeurons = getOutputLayer().getNeurons();
        double[] output = new double[outNeurons.length];

        for (int i=0; i<output.length; i++) {
            output[i] = outNeurons[i].activation;
        }

        return output;
    }

    /**
     * Loads the mlp data from a file, the creates the network. <br>
     * This method is meant to be used with a saved {@link MlpTrain}.
     * @param file The file to the mlp data.
     * @return The previously saved mlp.
     * @throws IOException If any IO error occurs.
     */
    public static RunMlp loadMlp(File file) throws IOException {
        FileChannel fileIn = new FileInputStream(file).getChannel();

        ByteBuffer inBuffer = ByteBuffer.allocate((int) file.length());
        fileIn.read(inBuffer);
        inBuffer.flip();

        int inLen = inBuffer.getInt();
        int h1Len = inBuffer.getInt();
        int h2Len = inBuffer.getInt();
        int outLen = inBuffer.getInt();

        RunMlp mlp = new RunMlp(inLen, h1Len, h2Len, outLen);

        for (ConnectionLayer layer : mlp.mLayerArray) {
            layer.loadFromByteBuffer(inBuffer);
        }

        return mlp;
    }
}

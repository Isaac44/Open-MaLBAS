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

import br.edu.unifei.gpesc.neural.mlp.core.MlpTrain;

/**
 *
 * @author Isaac Caldas Ferreira
 */
public class RunMlp extends Mlp {

    public RunMlp(int inLen, int h1Len, int h2Len, int outLen) {
        super(inLen, h1Len, h2Len, outLen);
    }

       // TEST
    public void runTestSup(PatternLayer[] patterns) {

        int npat;   // numero de um padrao de teste
        double perr;   // erro de um padrao de teste
        double errtot;   // erro total dos padroes de teste

        MlpTrain.Print output = new MlpTrain.Print();

        // Execucao dos padroes de teste na rede neural
        npat = 0;
        errtot = 0.0f;

        NeuronLayer inputLayer = mInputLayer;
        ConnectionLayer outputLayer = mLayerArray[Layer.OUTPUT.ordinal()];

        for (PatternLayer pattern : patterns) {
            npat++;

            inputLayer.setNeuron(pattern.inputLayer);
            computeActivationOutput();

            perr = outputLayer.getDifferenceTotal(pattern.outputLayer);
            errtot += perr;

            output.format("Padrao %d:\n", npat);
            for (NeuronLayer.Neuron neuron : pattern.inputLayer.mNeuronArray) {
                output.format("   %.4f", neuron.activation);
            }
            output.format("\n   ===> Saida Esperada:  ");
            for (NeuronLayer.Neuron neuron : pattern.outputLayer.mNeuronArray) {
                output.format("   %.4f", neuron.activation);
            }

            pattern.outputLayer.computeDifference(outputLayer);

            output.format("\n   ===> Saida Obtida:  ");
            for (NeuronLayer.Neuron neuron : pattern.outputLayer.mNeuronArray) {
                output.format("   %.4f", neuron.activation);
            }
            output.format("\n   ===> Erro do padrao de teste:  %.4f\n\n\n\n", perr);

        }
    }

}

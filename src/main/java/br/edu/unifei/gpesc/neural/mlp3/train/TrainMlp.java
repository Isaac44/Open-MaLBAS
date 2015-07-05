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
import br.edu.unifei.gpesc.neural.mlp3.core.LogSig;
import br.edu.unifei.gpesc.neural.mlp3.core.TanSig;
import java.io.FileNotFoundException;
import java.util.Random;
import test.mlp.Test3;

/**
 *
 * @author Isaac Caldas Ferreira
 */
public class TrainMlp {

    private static final int HIDDEN_1 = 0;
    private static final int HIDDEN_2 = 1;
    private static final int OUTPUT = 2;

    /**
     * The max absolute value of the weight.
     */
    private double mMaxAbsoluteWeight = 0.1;

    /**
     * The quantity of epochs to be processed.
     */
    private int mEpochs = 20;

    /**
     * The momentum rate.
     */
    private double mMomentum = 0.9;

    private long mPrimeSeed = 7;//31 * System.currentTimeMillis();

    /**
     * The learn rate.
     */
    private double mLearnRate = 0.00001;

    private final FirstLayer mInputLayer;
    private final FirstLayer mOutputLayer;

    private final NextLayer[] mLayerArray;

    private DualLayer[] mTrainInputArray;
    private DualLayer[] mValidationArray;

    public TrainMlp(int inLen, int h1Len, int h2Len, int outLen) {
        mInputLayer = new FirstLayer(inLen);
        mOutputLayer = new FirstLayer(outLen);

        mLayerArray = new NextLayer[3];
        mLayerArray[HIDDEN_1] = new NextLayer(h1Len, mInputLayer, new TanSig());
        mLayerArray[HIDDEN_2] = new NextLayer(h2Len, mLayerArray[HIDDEN_1], new TanSig());
        mLayerArray[OUTPUT] = new NextLayer(outLen, mLayerArray[HIDDEN_2], new LogSig());
    }

    private void genBiasAndWeights() {
        Random random = new Random(mPrimeSeed);
        for (NextLayer layer : mLayerArray) {
            layer.genBiasAndWeights(random, mMaxAbsoluteWeight);
        }
    }

    private void computeOutput() {
        for (NextLayer layer : mLayerArray) {
            layer.computeActivation();
        }
    }

    private void printDebug() {
        for (NextLayer layer : mLayerArray) {
            layer.printDebug();
        }
    }

    public double runValSup() {

        double perr;   // erro de um padrao de validacao
        double errtot;   // erro total dos padroes de validacao

        // Execucao dos padroes de validacao na rede neural
        errtot = 0.0;
        FirstLayer inputLayer = mInputLayer;
        NextLayer outputLayer = mLayerArray[OUTPUT];

        for (DualLayer validationLayer : mValidationArray) {
            inputLayer.setNeuron(validationLayer.firstLayer);
            computeOutput();

            perr = outputLayer.getError(validationLayer.nextLayer);
            errtot += perr;

        }  // fim while

        // reset output layer
        outputLayer.setNeuron(mOutputLayer);

        // Retorna o erro total sobre os padroes de validacao
        return errtot;
    }

    private void resetLayers() {
        for (NextLayer layer : mLayerArray) {
            layer.reset();
        }
    }

    private void computeError(FirstLayer trainOutput) {
        NextLayer[] layers = mLayerArray;

        // Calculo do delta dos neuronios na camada de saida
        NextLayer.computeOutputError(trainOutput, layers[OUTPUT]);

        // Calculo do delta dos neuronios nas camadas escondidas
        // Out->H2, H2->H1
        for (int i=layers.length-1; i>= 1; i--) {
            layers[i].computeError();
        }
    }

    private void computeBedAndWedIncrement() {
        for (NextLayer layer : mLayerArray) {
            layer.computeBedAndWedIncrement();
        }
    }

    public void setInputArray(DualLayer[] trainArray) {
        mTrainInputArray = trainArray;
    }

    public void setValidationArray(DualLayer[] validationArray) {
        mValidationArray = validationArray;
    }

    public void setPrimeSeed(long seed) {
        mPrimeSeed = seed;
    }

    private void computeDbiasDweights(double lrate, double momentum) {
        for (NextLayer layer : mLayerArray) {
            layer.computeBiasAndWeightsDeltas(lrate, momentum);
        }
    }

    private void changeBiasWeights() {
        for (NextLayer layer : mLayerArray) {
            layer.changeBiasAndWeights();
        }
    }

    /**
     * FIXME: alterando variaveis globais iniciais!!!!
     * TODO: adicionar modificador 'final' nelas.
     * @param errtot
     * @param lerrtot
     */
    private void changeRates(double errtot, double lerrtot) {
        if (errtot >= lerrtot) {
            mMomentum = 0.0f;
            mLearnRate /= 2.0;
        } else {
            mLearnRate *= 1.02;
        }
    }

    public void trainByEpoch() {
        int passo;   // cada passo inclui "nepochs" epocas de treinamento
        double tperr;   // erro de um padrao de treinamento
        double errtot;   // erro total dos padroes de treinamento na epoca atual
        double lerrtot;   // erro total dos padroes de treinamento na epoca anterior
        double errtrain;   // erro total dos padroes de treinamento no passo atual
        double lerrtrain;   // erro total dos padroes de treinamento no passo anterior
        double errval;   // erro total dos padroes de validacao no passo atual
        double lerrval;   // erro total dos padroes de validacao no passo anterior
        String rootDir;   // nome do diretorio raiz que contem resultados do treinamento
        String trainDir;   // nome do diretorio que contem resultados de cada treino
        String logFile;   // nome do arquivo de "log" do treinamento

        FirstLayer inputLayer = mInputLayer;
        NextLayer outputLayer = mLayerArray[OUTPUT];

        // init bias and weights
        genBiasAndWeights();

        // Inicializacao dos erros sobre o conjunto de treinamento
        errtot = 1.0e+30f;
        lerrtot = 1.0e+30f;
        errtrain = 1.0e+30f;
        lerrtrain = 1.0e+30f;

        // Obtem o erro sobre o conjunto de validacao
        errval = runValSup();

        System.out.println("errval = " + errval);

        lerrval = 1.0e+30f;

        MlpTrain.Print output = new MlpTrain.Print();

        passo = 0;
        while (errval < lerrval) {
            passo++;
            output.format("\n\n\n\n\nPasso %d:\n\n\n", passo);

            // Treinamento das "nepochs" epocas
            for (int ep = 1; ep <= mEpochs; ep++) {
                errtot = 0.0f;
                resetLayers();

                for (DualLayer trainLayer : mTrainInputArray) {
                    inputLayer.setNeuron(trainLayer.firstLayer);
                    computeOutput();
                    computeError(trainLayer.nextLayer);

                    computeBedAndWedIncrement();

                    tperr = outputLayer.getError(trainLayer.nextLayer);
                    errtot += tperr;
                }

                computeDbiasDweights(mLearnRate, mMomentum);

//                printDebug();
//                if (mInputLayer != null) System.exit(0);

                changeBiasWeights();
                changeRates(errtot, lerrtot);


//                output.format("   ===>   Epoca %d:\n", ep);
//                output.format("      ===>   Erro Total (Epoca Anterior)= %.10f\n", lerrtot);
//                output.format("      ===>   Erro Total (Epoca Atual)= %.10f\n", errtot);
//                output.format("      ===>   momentum (Epoca Atual)= %.2f\n", mMomentum);
//                output.format("      ===>   lrate (Epoca Atual)= %.10f\n\n\n", mLearnRate);

                lerrtot = errtot;
            }   // fim for-ep

            lerrval = errval;
            errval = runValSup();

            output.format("   ===>   Erro sobre o conjunto de validacao ");
            output.format("(Passo Anterior)= %.10f\n", lerrval);
            output.format("   ===>   Erro sobre o conjunto de validacao ");
            output.format("(Passo Atual)= %.10f\n", errval);
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        Test3.main(args);
    }
}

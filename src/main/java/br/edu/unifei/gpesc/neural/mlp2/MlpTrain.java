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
package br.edu.unifei.gpesc.neural.mlp2;

import java.util.Random;
import static br.edu.unifei.gpesc.neural.mlp2.NeuralLayer.FunctionEnum.*;

/**
 *
 * @author Isaac Caldas Ferreira
 */
public class MlpTrain extends Mlp{

    /**
     * The max absolute value of the weight.
     */
    private float mMaxAbsoluteWeight = 0.1f;

    /**
     * The quantity of epochs to be processed.
     */
    private int mEpochs = 20;

    /**
     * The momentum rate.
     */
    private float mMomentum = 0.9f;

    /**
     * The learn rate.
     */
    private float mLearnRate = 0.000001f;

    /**
     * The neuron array.
     */
    private NeuronTrain[] mNeuronArray;

    /**
     * The matrix with the train patterns.
     */
    private float[][] mTrainPatternMatrix;

    /**
     * The matrix with the validation patterns.
     */
    private float[][] mValidationPatternMatrix;

    /**
     * The neural network connections
     */
    private LinkTrain[][] mNeuralConnectionMatrix;

    /**
     * Random for bias and weight generation.
     */
    private final Random mRandom = new Random();


    public MlpTrain(int inLen, int h1Len, int h2Len, int outLen) {
        super(inLen, h1Len, h2Len, outLen);
    }

    /**
     * Sets the quantity of epochs of each train to be processed.
     * @param numberOfEpochs The quantity of epochs.
     */
    public void setNumberOfEpochs(int numberOfEpochs) {
        mEpochs = numberOfEpochs;
    }

    /**
     * Sets the absolute max weight value.
     * @param maxWeight The max weight value.
     */
    public void setMaxWeight(float maxWeight) {
        mMaxAbsoluteWeight = Math.abs(maxWeight);
    }

    /**
     * Sets the randomizer seed.
     * @param seed The randomizer seed.
     */
    public void setRandomizerSeed(long seed) {
        mRandom.setSeed(seed);
    }

    /**
     * Sets the momentum rate.
     * @param momentum The momentum rate.
     */
    public void setMomentumRate(float momentum) {
        mMomentum = momentum;
    }

    /**
     * Sets the learn rate.
     * @param learnRate The learn rate.
     */
    public void setLearnRate(float learnRate) {
        mLearnRate = learnRate;
    }

    /**
     * Sets the train patterns. <br>
     * This method will copy each pattern on the tpatterns argument and add to
     * the {@link MlpTrain#mTrainPatternMatrix}.
     * @param tpatterns The input train patterns.
     */
    public void setTrainPatterns(float[][] tpatterns) {
        if (tpatterns[0].length != mInputLayer.length) {
            throw new IllegalArgumentException();
        }

        float[][] pat = new float[tpatterns.length][layersLength()];

        for (int i=0; i<pat.length; i++) {
            System.arraycopy(tpatterns[i], 0, pat[i], 0, tpatterns[i].length);
        }

        mTrainPatternMatrix = pat;
    }

    /**
     * Starts the mlp train.
     */
    public void start() {
        // gen bias

    }

    /**
     * Randomly generates the bias and weights for the neural network training.
     */
    private void genBiasWeights() {
        // optimization
        Random rnd = mRandom;
        NeuronTrain[] neurons = mNeuronArray;
        LinkTrain[][] connections = mNeuralConnectionMatrix;
        float maxWeight = mMaxAbsoluteWeight;

        int i, j;

        int niH1 = mHiddenLayer1.first;
        int nfH1 = mHiddenLayer1.last;

        int niI = mInputLayer.first;
        int nfI = mInputLayer.last;

        int niH2 = mHiddenLayer2.first;
        int nfH2 = mHiddenLayer2.last;

        int niO = mOutputLayer.first;
        int nfO = mOutputLayer.last;

        // gen bias and weights
        for (i = niH1; i <= nfH1; i++) {
            neurons[i].bias = rnd.nextFloat() * maxWeight;
            if (!rnd.nextBoolean()) {
                neurons[i].bias *= -1.0f;
            }
            for (j = niI; j <= nfI; j++) {
                connections[i][j].weight = rnd.nextFloat() * maxWeight;
                if (!rnd.nextBoolean()) {
                    connections[i][j].weight *= -1.0f;
                }
            }
        }

        for (i = niH2; i <= nfH2; i++) {
            neurons[i].bias = rnd.nextFloat() * maxWeight;
            if (!rnd.nextBoolean()) {
                neurons[i].bias *= -1.0f;
            }
            for (j = niH1; j <= nfH1; j++) {
                connections[i][j].weight = rnd.nextFloat() * maxWeight;
                if (!rnd.nextBoolean()) {
                    connections[i][j].weight *= -1.0f;
                }
            }
        }

        for (i = niO; i <= nfO; i++) {
            neurons[i].bias = rnd.nextFloat() * maxWeight;
            if (!rnd.nextBoolean()) {
                neurons[i].bias *= -1.0f;
            }
            for (j = niH2; j <= nfH2; j++) {
                connections[i][j].weight = rnd.nextFloat() * maxWeight;
                if (!rnd.nextBoolean()) {
                    connections[i][j].weight *= -1.0f;
                }
            }
        }
    }

    /**
     * Computes the pattern error.
     * @param pattern The pattern array.
     * @return The error.
     */
    public float getPatternError(float[] pattern) {
        // optimization
        float aux1;

        NeuronTrain[] neuronio = mNeuronArray;

        int niO = mOutputLayer.first;
        int nfO = mOutputLayer.last;

        // compute
        aux1 = 0.0f;
        for (int i = niO; i <= nfO; i++) {
            aux1 += (float) Math.abs(pattern[i] - neuronio[i].activation);
        }

        return aux1;
    }

    /**
     * Updates the bias and the weights pesos.
     */
    private void changeBiasWeights() {
        // optimization
        NeuronTrain[] neurons = mNeuronArray;
        LinkTrain[][] connections = mNeuralConnectionMatrix;

        int i, j;

        int niH1 = mHiddenLayer1.first;
        int nfH1 = mHiddenLayer1.last;

        int niI = mInputLayer.first;
        int nfI = mInputLayer.last;

        int niH2 = mHiddenLayer2.first;
        int nfH2 = mHiddenLayer2.last;

        int niO = mOutputLayer.first;
        int nfO = mOutputLayer.last;

        // compute

        for (i = niH1; i <= nfH1; i++) {
            for (j = niI; j <= nfI; j++) {
                connections[i][j].weight += connections[i][j].dweight;
            }
            neurons[i].bias += neurons[i].dbias;
        }

        for (i = niH2; i <= nfH2; i++) {
            for (j = niH1; j <= nfH1; j++) {
                connections[i][j].weight += connections[i][j].dweight;
            }
            neurons[i].bias += neurons[i].dbias;
        }

        for (i = niO; i <= nfO; i++) {
            for (j = niH2; j <= nfH2; j++) {
                connections[i][j].weight += connections[i][j].dweight;
            }
            neurons[i].bias += neurons[i].dbias;
        }
    }

    /**
     * Changes depending on the variation of the total error,
     * the {@link MlpTrain#mLearnRate} and the {@link MlpTrain#mMomentum}.
     * @param currError Current total error of the training patterns.
     * @param prevError Error previous total of training patterns.
     */
    public void changeRates(float currError, float prevError) {
        if (currError >= prevError) {
            mMomentum = 0.0f;
            mLearnRate /= 2.0f;
        } else {
            mLearnRate *= 1.02f;
        }
    }

    /**
     * Computes the {@link NeuronTrain#bed} for each bias and the
     * {@link LinkTrain#wed} for each weight.
     */
    public void computeBedWed() {
        // optimization
        NeuronTrain[] neurons = mNeuronArray;
        LinkTrain[][] connections = mNeuralConnectionMatrix;

        int i, j;

        int niH1 = mHiddenLayer1.first;
        int nfH1 = mHiddenLayer1.last;

        int niI = mInputLayer.first;
        int nfI = mInputLayer.last;

        int niH2 = mHiddenLayer2.first;
        int nfH2 = mHiddenLayer2.last;

        int niO = mOutputLayer.first;
        int nfO = mOutputLayer.last;

        // compute

        for (i = niH1; i <= nfH1; i++) {
            for (j = niI; j <= nfI; j++) {
                connections[i][j].wed = neurons[i].delta * neurons[j].activation;
            }
            neurons[i].bed = neurons[i].delta;
        }

        for (i = niH2; i <= nfH2; i++) {
            for (j = niH1; j <= nfH1; j++) {
                connections[i][j].wed = neurons[i].delta * neurons[j].activation;
            }
            neurons[i].bed = neurons[i].delta;
        }

        for (i = niO; i <= nfO; i++) {
            for (j = niH2; j <= nfH2; j++) {
                connections[i][j].wed = neurons[i].delta * neurons[j].activation;
            }
            neurons[i].bed = neurons[i].delta;
        }
    }

    /**
     * Computes the {@link NeuronTrain#dbias} "dbias" for each
     * {@link NeuronTrain#bias} and the {@link LinkTrain#dweight} for each
     * weight.
     */
    public void computeDbiasDweights() {
        // optimization
        NeuronTrain[] neurons = mNeuronArray;
        LinkTrain[][] connections = mNeuralConnectionMatrix;

        int i, j;

        int niH1 = mHiddenLayer1.first;
        int nfH1 = mHiddenLayer1.last;

        int niI = mInputLayer.first;
        int nfI = mInputLayer.last;

        int niH2 = mHiddenLayer2.first;
        int nfH2 = mHiddenLayer2.last;

        int niO = mOutputLayer.first;
        int nfO = mOutputLayer.last;

        float learnRate = mLearnRate;
        float momentum = mMomentum;

        // compute

        for (i = niH1; i <= nfH1; i++) {
            for (j = niI; j <= nfI; j++) {
                connections[i][j].dweight = learnRate * connections[i][j].wed + momentum * connections[i][j].dweight;
            }
            neurons[i].dbias = learnRate * neurons[i].bed + momentum * neurons[i].dbias;
        }
        for (i = niH2; i <= nfH2; i++) {
            for (j = niH1; j <= nfH1; j++) {
                connections[i][j].dweight = learnRate * connections[i][j].wed + momentum * connections[i][j].dweight;
            }
            neurons[i].dbias = learnRate * neurons[i].bed + momentum * neurons[i].dbias;
        }
        for (i = niO; i <= nfO; i++) {
            for (j = niH2; j <= nfH2; j++) {
                connections[i][j].dweight = learnRate * connections[i][j].wed + momentum * connections[i][j].dweight;
            }
            neurons[i].dbias = learnRate * neurons[i].bed + momentum * neurons[i].dbias;
        }
    }

    /**
     * Computes the {@link NeuronTrain#delta} for each neuron.
     * @param pattern The input train pattern.
     */
    public void computeError(float[] pattern) {
        // optimization

        int i;
        float aux1;
        float aux2;

        NeuronTrain[] neurons = mNeuronArray;
        LinkTrain[][] connections = mNeuralConnectionMatrix;
        float[][] tpatterns = mTrainPatternMatrix;

        int niH1 = mHiddenLayer1.first;
        int nfH1 = mHiddenLayer1.last;

        int niH2 = mHiddenLayer2.first;
        int nfH2 = mHiddenLayer2.last;

        int niO = mOutputLayer.first;
        int nfO = mOutputLayer.last;

        // Compute the delta of the output layer neurons.
        for (i = niO; i <= nfO; i++) {
            aux1 = pattern[i] - neurons[i].activation;
            switch (mOutputLayer.functionId) {
                case LINEAR: {
                    neurons[i].delta = aux1;
                    break;
                }
                case LOGSIG: {
                    aux2 = neurons[i].activation;
                    neurons[i].delta = aux1 * aux2 * (1.0f - aux2);
                    break;
                }
                case TANSIG: {
                    aux2 = neurons[i].activation;
                    neurons[i].delta = aux1 * (float) (-1.0 * (Math.pow((double) aux2, 2.0) - 1.0));
                    break;
                }
            }
        }

        // Compute the delta of the second hidden layer neurons.
        for (i = niH2; i <= nfH2; i++) {
            aux1 = 0.0f;
            for (int k = niO; k <= nfO; k++) {
                aux1 += neurons[k].delta * connections[k][i].weight;
            }
            switch (mHiddenLayer2.functionId) {
                case LINEAR: {
                    neurons[i].delta = aux1;
                    break;
                }
                case LOGSIG: {
                    aux2 = neurons[i].activation;
                    neurons[i].delta = aux1 * aux2 * (1.0f - aux2);
                    break;
                }
                case TANSIG: {
                    aux2 = neurons[i].activation;
                    neurons[i].delta = aux1 * (float) (-1.0 * (Math.pow((double) aux2, 2.0) - 1.0));
                    break;
                }
            }
        }

        // Compute the delta of the firsthidden layer neurons.
        for (i = niH1; i <= nfH1; i++) {
            aux1 = 0.0f;
            for (int k = niH2; k <= nfH2; k++) {
                aux1 += neurons[k].delta * connections[k][i].weight;
            }
            switch (mHiddenLayer1.functionId) {
                case LINEAR: {
                    neurons[i].delta = aux1;
                    break;
                }
                case LOGSIG: {
                    aux2 = neurons[i].activation;
                    neurons[i].delta = aux1 * aux2 * (1.0f - aux2);
                    break;
                }
                case TANSIG: {
                    aux2 = neurons[i].activation;
                    neurons[i].delta = aux1 * (float) (-1.0 * (Math.pow((double) aux2, 2.0) - 1.0));
                    break;
                }
            }
        }
    }

    /**
     * Computes the {@link NeuronTrain#bed} increment for each bias and the
     * {@link LinkTrain#wed} increment for each weight.
     */
    public void computeIncBedWed() {
        // optimization
        NeuronTrain[] neurons = mNeuronArray;
        LinkTrain[][] connections = mNeuralConnectionMatrix;

        int i, j;

        int niH1 = mHiddenLayer1.first;
        int nfH1 = mHiddenLayer1.last;

        int niI = mInputLayer.first;
        int nfI = mInputLayer.last;

        int niH2 = mHiddenLayer2.first;
        int nfH2 = mHiddenLayer2.last;

        int niO = mOutputLayer.first;
        int nfO = mOutputLayer.last;

        // compute

        for (i = niH1; i <= nfH1; i++) {
            for (j = niI; j <= nfI; j++) {
                connections[i][j].wed += neurons[i].delta * neurons[j].activation;
            }
            neurons[i].bed += neurons[i].delta;
        }
        for (i = niH2; i <= nfH2; i++) {
            for (j = niH1; j <= nfH1; j++) {
                connections[i][j].wed += neurons[i].delta * neurons[j].activation;
            }
            neurons[i].bed += neurons[i].delta;
        }
        for (i = niO; i <= nfO; i++) {
            for (j = niH2; j <= nfH2; j++) {
                connections[i][j].wed += neurons[i].delta * neurons[j].activation;
            }
            neurons[i].bed += neurons[i].delta;
        }
    }

    /**
     * Computa a ativacao dos neuronios da camada de saida da rede neural.
     *
     */
    public void computeOutput() {
        // optimization
        NeuronTrain[] neurons = mNeuronArray;
        LinkTrain[][] connections = mNeuralConnectionMatrix;

        int i, j;

        int niH1 = mHiddenLayer1.first;
        int nfH1 = mHiddenLayer1.last;

        int niI = mInputLayer.first;
        int nfI = mInputLayer.last;

        int niH2 = mHiddenLayer2.first;
        int nfH2 = mHiddenLayer2.last;

        int niO = mOutputLayer.first;
        int nfO = mOutputLayer.last;

        TransferFunction h1Function = mHiddenLayer1.function;
        TransferFunction h2Function = mHiddenLayer2.function;
        TransferFunction outFunction = mOutputLayer.function;

        // Compute the neurons activation on the first hidden layer.
        for (i = niH1; i <= nfH1; i++) {
            neurons[i].netinput = neurons[i].bias;
            for (j = niI; j <= nfI; j++) {
                neurons[i].netinput += connections[i][j].weight * neurons[j].activation;
            }
            neurons[i].activation = h1Function.compute(neurons[i].netinput);
        }

        // Compute the neurons activation on the second hidden layer.
        for (i = niH2; i <= nfH2; i++) {
            neurons[i].netinput = neurons[i].bias;
            for (j = niH1; j <= nfH1; j++) {
                neurons[i].netinput += connections[i][j].weight * neurons[j].activation;
            }
            neurons[i].activation = h2Function.compute(neurons[i].netinput);
        }

        // Compute the neurons activation on the output layer.
        for (i = niO; i <= nfO; i++) {
            neurons[i].netinput = neurons[i].bias;
            for (j = niH2; j <= nfH2; j++) {
                neurons[i].netinput += connections[i][j].weight * neurons[j].activation;
            }
            neurons[i].activation = outFunction.compute(neurons[i].netinput);
        }
    }

    /**
     * Inits the neural network. <br>
     * This method initializes:
     * <ul>
     * <li>{@link MlpTrain#mNeuralConnectionMatrix}</li>
     * <li>{@link MlpTrain#mNeuronArray}</li>
     * <li>{@link MlpTrain#mTrainPatternMatrix}</li>
     * </ul>
     */
    private void initNeuralNetwork() {
        // optimization
        int i, j;

        int length = layersLength();

        NeuronTrain[] neurons = new NeuronTrain[length];
        LinkTrain[][] connections = new LinkTrain[length][length];

        // compute
        for (i = 0; i < length; i++) {
            neurons[i] = new NeuronTrain();

            for (j = 0; j < length; j++) {
                connections[i][j] = new LinkTrain();
            }
        }

        // attributions
        mNeuronArray = neurons;
        mNeuralConnectionMatrix = connections;
    }

    private int layersLength() {
        return mInputLayer.length + mHiddenLayer1.length +
                mHiddenLayer2.length + mOutputLayer.length;
    }

    /**
     * Resets the neural network. <br>
     * It must be called if another train will be executed.
     */
    public void reset() {
        for (NeuronTrain neuron : mNeuronArray) {
            neuron.activation = 0.0f;
            neuron.netinput = 0.0f;
            neuron.bias = 0.0f;
            neuron.delta = 0.0f;
            neuron.bed = 0.0f;
            neuron.dbias = 0.0f;
        }
    }

    /**
     * Inserts an execution pattern (test or validation) on the neural network.
     * @param pattern The pattern array to be inserted.
     */
    private void insertPattern(float[] pattern) {
        // optimization
        NeuronTrain[] neurons = mNeuronArray;

        int niI = mInputLayer.first;
        int nfI = mInputLayer.last;

        // compte
        for (int j = niI; j <= nfI; j++) {
            neurons[j].activation = pattern[j];
        }
    }

    /**
     * Resets the {@link LinkTrain#wed} and {@link NeuronTrain#bed} values.
     */
    private void resetBedWed() {
        // optimization
        int i, j;

        LinkTrain[][] conexao = mNeuralConnectionMatrix;
        NeuronTrain[] neuronio = mNeuronArray;
        int length = neuronio.length;

        // compute
        for (i = 0; i < length; i++) {
            for (j = 0; j < length; j++) {
                conexao[i][j].wed = 0.0f;
            }
            neuronio[i].bed = 0.0f;
        }
    }

    /**
     * Resets the {@link NeuronTrain#netinput} and
     * {@link NeuronTrain#activation} values.
     *
     */
    public void resetNetinputActivation() {
        for (NeuronTrain neurons : mNeuronArray) {
            neurons.netinput = 0.0f;
            neurons.activation = 0.0f;
        }
    }

    /**
     * Performs validation standards, supervised way. <br>
     * For each processed pattern, the result is compared to the expected
     * result, generating an error. The total error of these patterns
     * is returned.
     *
     * @return The total error over the validation patterns.
     */
    public float runValSup() {

        float perr;   // erro de um padrao de validacao
        float errtot;   // erro total dos padroes de validacao

        // Execucao dos padroes de validacao na rede neural
        errtot = 0.0f;
        for (float[] vpat : mValidationPatternMatrix) {
            insertPattern(vpat);
            computeOutput();
            perr = getPatternError(vpat);
            errtot += perr;
        }

        return errtot;
    }

        /**
     * Treina, pelo metodo epoch-by-epoch, a rede neural.
     *
     * @param valFileName - nome do arquivo de onde os padroes de validacao
     * serao lidos.
     */
    public void trainByEpoch() {

        int passo;   // cada passo inclui "nepochs" epocas de treinamento
        float tperr;   // erro de um padrao de treinamento
        float errtot;   // erro total dos padroes de treinamento na epoca atual
        float lerrtot;   // erro total dos padroes de treinamento na epoca anterior
        float errtrain;   // erro total dos padroes de treinamento no passo atual
        float errval;   // erro total dos padroes de validacao no passo atual
        float lerrval;   // erro total dos padroes de validacao no passo anterior

        // Geracao dos bias e pesos iniciais
        genBiasWeights();

        // Inicializacao da taxa de aprendizagem e momentum
        float lrate = mLearnRate;
        float momentum = mMomentum;
        int nepochs = mEpochs;

        int ntpatterns = mNeuronArray.length;
        float[][] tpatterns = mTrainPatternMatrix;

        // Inicializacao dos erros sobre o conjunto de treinamento
        errtot = 1.0e+30f;
        lerrtot = 1.0e+30f;
        errtrain = 1.0e+30f;

        // Obtem o erro sobre o conjunto de validacao
        errval = runValSup();
        lerrval = 1.0e+30f;

        // Treinamento de cada passo de "nepochs" epocas
        passo = 0;
        while (errval < lerrval) {
            passo++;

            // Treinamento das "nepochs" epocas
            for (int ep = 1; ep <= nepochs; ep++) {
                errtot = 0.0f;
                resetBedWed();
                resetNetinputActivation();
                for (float[] tpat : tpatterns) {
                    insertPattern(tpat);
                    computeOutput();
                    computeError(tpat);
                    computeIncBedWed();
                    tperr = getPatternError(tpat);
                    errtot += tperr;
                }
                computeDbiasDweights();
                changeBiasWeights();
                changeRates(errtot, lerrtot);
                lerrtot = errtot;
            }

            // Obtencao do erro sobre o conjunto de treinamento
            errtrain = errtot;

            // Obtencao do erro sobre o conjunto de validacao
            lerrval = errval;
            errval = runValSup();

        }   // fim while

        // Remocao do ultimo passo, posto que seu erro e' maior do que o do passo anterior
        if (passo > 1) {
            passo--;
        }
    }

    /**
     * Provides a convenient way to access the neural connections.
     */
    private class LinkTrain {

        /**
         * The weight.
         */
        float weight;   // peso

        /**
         * The weight's error derivative.
         */
        float wed;

        /**
         * The weight delta.
         */
        float dweight;
    }

    /**
     * Provides a convenient way to access the neurons.
     */
    private static class NeuronTrain {

        /**
         * The activation value.
         */
        float activation;

        /**
         * The net input.
         */
        float netinput;

        /**
         * The bias connection.
         */
        float bias;

        /**
         * The error derivative.
         */
        float delta;

        /**
         * The bias conection error derivative.
         */
        float bed;

        /**
         * The bias delta.
         */
        float dbias;
    }
}

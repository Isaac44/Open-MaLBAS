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
    private int mNumberOfEpochs = 20;

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
        mNumberOfEpochs = numberOfEpochs;
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

        // gen bias and weights
        for (int i = mHiddenLayer1.first; i <= mHiddenLayer1.last; i++) {
            neurons[i].bias = rnd.nextFloat() * maxWeight;
            if (!rnd.nextBoolean()) {
                neurons[i].bias *= -1.0f;
            }
            for (int j = mInputLayer.first; j <= mInputLayer.last; j++) {
                connections[i][j].weight = rnd.nextFloat() * maxWeight;
                if (!rnd.nextBoolean()) {
                    connections[i][j].weight *= -1.0f;
                }
            }
        }

        for (int i = mHiddenLayer2.first; i <= mHiddenLayer2.last; i++) {
            neurons[i].bias = rnd.nextFloat() * maxWeight;
            if (!rnd.nextBoolean()) {
                neurons[i].bias *= -1.0f;
            }
            for (int j = mHiddenLayer1.first; j <= mHiddenLayer1.last; j++) {
                connections[i][j].weight = rnd.nextFloat() * maxWeight;
                if (!rnd.nextBoolean()) {
                    connections[i][j].weight *= -1.0f;
                }
            }
        }

        for (int i = mOutputLayer.first; i <= mOutputLayer.last; i++) {
            neurons[i].bias = rnd.nextFloat() * maxWeight;
            if (!rnd.nextBoolean()) {
                neurons[i].bias *= -1.0f;
            }
            for (int j = mHiddenLayer2.first; j <= mHiddenLayer2.last; j++) {
                connections[i][j].weight = rnd.nextFloat() * maxWeight;
                if (!rnd.nextBoolean()) {
                    connections[i][j].weight *= -1.0f;
                }
            }
        }
    }

    /**
     * Updates the bias and the weights pesos.
     */
    private void changeBiasWeights() {

        // optimization
        NeuronTrain[] neurons = mNeuronArray;
        LinkTrain[][] connections = mNeuralConnectionMatrix;

        // compute

        for (int i = mHiddenLayer1.first; i <= mHiddenLayer1.last; i++) {
            for (int j = mInputLayer.first; j <= mInputLayer.last; j++) {
                connections[i][j].weight += connections[i][j].dweight;
            }
            neurons[i].bias += neurons[i].dbias;
        }
        for (int i = mHiddenLayer2.first; i <= mHiddenLayer2.last; i++) {
            for (int j = mHiddenLayer1.first; j <= mHiddenLayer2.last; j++) {
                connections[i][j].weight += connections[i][j].dweight;
            }
            neurons[i].bias += neurons[i].dbias;
        }
        for (int i = mOutputLayer.first; i <= mOutputLayer.last; i++) {
            for (int j = mHiddenLayer2.first; j <= mHiddenLayer2.last; j++) {
                connections[i][j].weight += connections[i][j].dweight;
            }
            neurons[i].bias += neurons[i].dbias;
        }
    }

    /**
     * Modifica, dependendo da variacao do erro total, a "lrate" e o "momentum".
     *
     * @param errtot - erro total atual dos padroes de treinamento.
     * @param lerrtot - erro total anterior dos padroes de treinamento.
     */
    public void changeRates(float errtot, float lerrtot) {

        if (errtot >= lerrtot) {
            momentum = 0.0f;
            lrate /= 2.0f;
        } else {
            lrate *= 1.02f;
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

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

import br.edu.unifei.gpesc.neural.mlp3.core.Function;
import java.util.Random;

/**
 *
 * @author Isaac Caldas Ferreira
 */
public class NextLayer extends FirstLayer {

    private Function mFunction;
    private final FirstLayer mPreviousLayer;
    private final Connection[][] mConnectionMatrix;

    public NextLayer(int length, FirstLayer prevLayer, Function function) {
        super(length);
        mFunction = function;
        mPreviousLayer = prevLayer;
        mConnectionMatrix = new Connection[length][prevLayer.length()];

        // optimization
        int i, j;
        Connection[][] connections = mConnectionMatrix;

        for (i=0; i<connections.length; i++) {
            for (j=0; j<connections[i].length; j++) {
                connections[i][j] = new Connection();
            }
        }
    }

    public void setFunction(Function function) {
        mFunction = function;
    }

    public void genBiasAndWeights(Random rand, double maxWeight) {
        int i, j;

        Neuron[] neurons = mNeuronArray;
        Connection[][] connections = mConnectionMatrix;

        for (i=0; i<neurons.length; i++) {
            neurons[i].bias = rand.nextDouble() * maxWeight;

            if (!rand.nextBoolean()) {
                neurons[i].bias *= -1;
            }

            for (j=0; j<connections[i].length; j++) {
                connections[i][j].weight = rand.nextDouble() * maxWeight;

                if (!rand.nextBoolean()) {
                    connections[i][j].weight *= -1;
                }
            }
        }
    }

    public void changeBiasAndWeights() {
        int i, j;

        Neuron[] neurons = mNeuronArray;
        Connection[][] connections = mConnectionMatrix;

        for (i=0; i<neurons.length; i++) {
            for (j=0; j<connections[i].length; j++) {
                connections[i][j].weight += connections[i][j].dweight;
            }
            neurons[i].bias += neurons[i].dbias;
        }
    }

    public void computeBedAndWedIncrement() {
        int i, j;

        Neuron[] neurons = mNeuronArray;
        Neuron[] prevNeurons = mPreviousLayer.mNeuronArray;
        Connection[][] connections = mConnectionMatrix;

        for (i=0; i<neurons.length; i++) {
            for (j=0; j<prevNeurons.length; j++) {
                connections[i][j].wed += neurons[i].delta * prevNeurons[j].activation;
            }
            neurons[i].bed += neurons[i].delta;
        }
    }

    public void computeBiasAndWeightsDeltas(double lrate, double momentum) {
        int i, j;

        Neuron[] neurons = mNeuronArray;
        Connection[][] connections = mConnectionMatrix;

        for (i=0; i<neurons.length; i++) {
            for (j=0; j<connections[i].length; j++) {
                connections[i][j].dweight = lrate * connections[i][j].wed + momentum * connections[i][j].dweight;
            }
            neurons[i].dbias = lrate * neurons[i].bed + momentum * neurons[i].dbias;
        }
    }

    public void reset() {
        int i, j;

        Neuron[] neurons = mNeuronArray;
        Connection[][] connections = mConnectionMatrix;

        for (i=0; i<neurons.length; i++) {

            for (j=0; j<connections[i].length; j++) {
                connections[i][j].wed = 0.0;
            }

            neurons[i].bed = 0.0;
            neurons[i].netinput = 0.0;
            neurons[i].activation = 0.0;
        }
    }

    public void computeActivation() {
        int i, j;

        Neuron[] neurons = mNeuronArray;
        Neuron[] prevNeurons = mPreviousLayer.mNeuronArray;
        Connection[][] connections = mConnectionMatrix;

        Function function = mFunction;

        for (i=0; i<neurons.length; i++) {
            neurons[i].netinput = neurons[i].bias;

            for (j=0; j<prevNeurons.length; j++) {
                neurons[i].netinput += connections[i][j].weight * prevNeurons[j].activation;
            }

            neurons[i].activation = function.compute(neurons[i].netinput);
        }
    }

    // é invertido!
    public void computeError() {
        int j, i;
        double x;

        Neuron[] neurons = mNeuronArray;
        Connection[][] connections = mConnectionMatrix;

        Neuron[] prevNeurons = mPreviousLayer.mNeuronArray;
        Function function = ((NextLayer) mPreviousLayer).mFunction;

        for (j=0; j<prevNeurons.length; j++) {
            x = 0.0;

            for (i=0; i<neurons.length; i++) {
                x += neurons[i].delta * connections[i][j].weight;
            }

            prevNeurons[j].delta = function.compute(x, prevNeurons[j].activation);
        }
    }

    public static void computeOutputError(FirstLayer trainOutput, NextLayer outputLayer) {
        Neuron[] prevNeurons = outputLayer.mNeuronArray;
        Neuron[] neurons = trainOutput.mNeuronArray;

        Function function = outputLayer.mFunction;

        double x, y;

        for (int j=0; j<prevNeurons.length; j++) {
            y = prevNeurons[j].activation;
            x = neurons[j].activation - y;

            prevNeurons[j].delta = function.compute(x, y);
        }
    }

    public double getError(FirstLayer layer) {
        double error = 0.0;

        Neuron[] neurons = mNeuronArray;
        Neuron[] otherNeurons = layer.mNeuronArray;

        for (int i=0; i<neurons.length; i++) {
            error += Math.abs( otherNeurons[i].activation - neurons[i].activation );
        }

        return error;
    }

    /**
     * The neural connections.
     */
    private static class Connection {

        /**
         * The weight.
         */
        double weight;

        /**
         * The weight's error derivative.
         */
        double wed;

        /**
         * The weight delta.
         */
        double dweight;
    }



    // --------
    // DEBUG
    // --------

    private static void print(double value) {
        if (value >= 0) System.out.print("+");
        System.out.print(String.format("%.18f ", value));
    }

    private void printC() {
        System.out.println("\n\n\tConnections (dweight):");
        for (Connection[] connectionArray : mConnectionMatrix) {
            for (Connection connection : connectionArray) {
                print(connection.dweight);
            }
            System.out.println();
        }
    }

    private void printN() {
        System.out.println("\n\tNeurons (dbias):");
        for (Neuron neuron : mNeuronArray) {
            print(neuron.dbias);
        }
    }

    public void printDebug() {
        printN();
        printC();
    }

}

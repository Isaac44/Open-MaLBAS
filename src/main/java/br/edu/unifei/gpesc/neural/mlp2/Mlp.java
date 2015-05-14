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

/**
 *
 * @author Isaac Caldas Ferreira
 */
public class Mlp {

    /**
     * Trace mode.
     */
    private boolean mTrace = false;

    /**
     * Number of neurons on the input layer.
     */
    private final int mInputLayerLength;

    /**
     * Number of neurons on the first hidden layer.
     */
    private final int mFirstHiddenLayerLength;

    /**
     * Number of neurons on the second hidden layer.
     */
    private final int mSecondHiddenLayerLength;

    /**
     * Number of neurons on the output layer.
     */
    private final int mOutputLayerLength;

    /**
     * First hidden layer transfer function.
     */
    private TransferFunction mFirstLayerFunction;

    /**
     * Second hidden layer transfer function.
     */
    private TransferFunction mSecondLayerFunction;

    /**
     * Ouput layer transfer function.
     */
    private TransferFunction mOutputLayerFunction;

    /**
     * The initial seed used to random generation of the bias and weights.
     */
    private int mRandomizerSeed = 0;

    public Mlp(int input, int firstHidden, int secondHidden, int output) {
        mInputLayerLength = input;
        mFirstHiddenLayerLength = firstHidden;
        mSecondHiddenLayerLength = secondHidden;
        mOutputLayerLength = output;

        mFirstLayerFunction = new TansigTransferFunction();
        mSecondLayerFunction = mFirstLayerFunction;

        mOutputLayerFunction = new LogsigTransferFunction();
    }

    /**
     * Sets the transfer function of the first hidden layer.
     * @param function The transfer function.
     */
    public void setFirstHiddenLayerFunction(TransferFunction function) {
        mFirstLayerFunction = function;
    }

    /**
     * Sets the transfer function of the second hidden layer.
     * @param function The transfer function.
     */
    public void setSecondHiddenLayerFunction(TransferFunction function) {
        mSecondLayerFunction = function;
    }

    /**
     * Sets the transfer function of the output layer.
     * @param function The transfer function.
     */
    public void setOutputLayerFunction(TransferFunction function) {
        mOutputLayerFunction = function;
    }

    /**
     * Sets the trace mode.
     * @param enabled True to enable, false to disable.
     */
    public void setTrace(boolean enabled) {
        mTrace = enabled;
    }

    public void setRandomizerSeed(int seed) {
        mRandomizerSeed = seed;
    }

}

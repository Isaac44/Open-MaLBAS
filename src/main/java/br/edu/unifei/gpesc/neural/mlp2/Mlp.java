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

import static br.edu.unifei.gpesc.neural.mlp2.NeuralLayer.FunctionEnum.*;

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
     * The input layer.
     */
    protected final NeuralLayer mInputLayer;

    /**
     * The first hidden layer.
     */
    protected final NeuralLayer mHiddenLayer1;

    /**
     * The second hidden layer.
     */
    protected final NeuralLayer mHiddenLayer2;

    /**
     * The output layer.
     */
    protected final NeuralLayer mOutputLayer;

    /**
     * Constructs a MLP with layers length. And initializes the both of
     * hidden layers transfer function with
     * {@link TansigTransferFunction} and the output layer with
     * {@link LogsigTransferFunction}.
     *
     * @param inLen The input layer length.
     * @param h1Len The first hidden layer length.
     * @param h2Len The second hidden layer length.
     * @param outLen The output layer length.
     */
    public Mlp(int inLen, int h1Len, int h2Len, int outLen) {
        mInputLayer = new NeuralLayer(0, inLen, NONE);
        mHiddenLayer1 = new NeuralLayer(mInputLayer, h1Len, TANSIG);
        mHiddenLayer2 = new NeuralLayer(mHiddenLayer1, h2Len, TANSIG);
        mOutputLayer = new NeuralLayer(mHiddenLayer2, outLen, LOGSIG);
    }

    /**
     * Sets the transfer function of the first hidden layer.
     * @param function The transfer function.
     */
    public void setFirstHiddenLayerFunction(TransferFunction function) {
        mHiddenLayer1.function = function;
    }

    /**
     * Sets the transfer function of the second hidden layer.
     * @param function The transfer function.
     */
    public void setSecondHiddenLayerFunction(TransferFunction function) {
        mHiddenLayer2.function = function;
    }

    /**
     * Sets the transfer function of the output layer.
     * @param function The transfer function.
     */
    public void setOutputLayerFunction(TransferFunction function) {
        mOutputLayer.function = function;
    }

    /**
     * Sets the trace mode.
     * @param enabled True to enable, false to disable.
     */
    public void setTrace(boolean enabled) {
        mTrace = enabled;
    }
}

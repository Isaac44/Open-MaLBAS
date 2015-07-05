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
 * This class stores the information about a layer of the mlp neural network.
 * @author Isaac Caldas Ferreira
 */
public class NeuralLayer {

    /**
     * Transfer function constants.
     */
    public static enum FunctionEnum {
        /**
         * Constant for none (null) transfer function.
         */
        NONE,

        /**
         * Constant for the linear transfer function.
         * @see LinearTransferFunction.
         */
        LINEAR,

        /**
         * Constant for the logsig transfer function.
         * @see LogsigTransferFunction.
         */
        LOGSIG,

        /**
         * Constant for the tansig transfer function.
         * @see TansigTransferFunction.
         */
        TANSIG;
    }

    /**
     * The quantity of neurons.
     */
    public final int length;

    /**
     * The first neuron.
     */
    public final int first;

    /**
     * The last neuron.
     */
    public final int last;

    /**
     * The transfer function id.
     */
    public FunctionEnum functionId;

    /**
     * The transfer function.
     */
    public TransferFunction function;

    /**
     * Constructor for this layer.
     * @param firstNeuron The first neuron on the array.
     * @param length The quantity of neurons.
     * @param functionId The transfer function id
     * ({@link NeuralLayer#LINEAR}, {@link NeuralLayer#LOGSIG} or
     * {@link NeuralLayer#TANSIG}) for this layer.
     */
    public NeuralLayer(int firstNeuron, int length, FunctionEnum functionId) {
        this.first  = firstNeuron;
        this.length = length;
        this.last   = firstNeuron + length - 1;
        this.functionId = functionId;
    }

    /**
     * This constructor sets this layer to be after the prevLayer.
     * @param prevLayer The previous layer.
     * @param length The quantity of neurons.
     * @param functionId The transfer function id
     * ({@link NeuralLayer#LINEAR}, {@link NeuralLayer#LOGSIG} or
     * {@link NeuralLayer#TANSIG}) for this layer.
     */
    public NeuralLayer(NeuralLayer prevLayer, int length, FunctionEnum functionId) {
        this(prevLayer.last + 1, length, functionId);
    }

    /**
     * Sets the transfer function.
     * @param functionId The transfer function id ({@link NeuralLayer#LINEAR},
     * {@link NeuralLayer#LOGSIG} or {@link NeuralLayer#TANSIG}) .
     */
    public void setTransferFunction(FunctionEnum functionId) {
        this.functionId = functionId;

        switch (functionId) {
            case NONE  : this.function = null;
            case LINEAR: this.function = new LinearTransferFunction(); break;
            case LOGSIG: this.function = new LogsigTransferFunction(); break;
            case TANSIG: this.function = new TansigTransferFunction(); break;
        }
    }

    /**
     * The linear transfer function of the mlp.
     * <br>
     * It only returns the input value, without any modification.
     */
    public static class LinearTransferFunction implements TransferFunction {

        /**
         * Do nothing. Only returns the input value.
         * @param value {@inheritDoc}
         * @return The input value.
         */
        @Override
        public float compute(float value) {
            return value;
        }
    }

    /**
    * This class computes the logsig transfer function: <br>
    * <b>logsig(x) = 1 / (1 + exp(-x))</b>
    */
   public static class LogsigTransferFunction implements TransferFunction {

       /**
        * Computes the logsig transfer function, which is given by the equation:
        * <b>logsig(x) = 1 / (1 + exp(-x))</b>
        * @param value {@inheritDoc}
        * @return The logsig result.
        */
       @Override
       public float compute(float value) {
           return 1f / (1f + (float) Math.exp(-value));
       }
   }

   /**
    * This class omputes the tansig transfer function: <br>
    * <b>tansig(x) = 2 / (1 + exp(-2 * x)) - 1</b>
    *
    * @author Isaac Caldas Ferreira
    */
   public static class TansigTransferFunction implements TransferFunction {

       /**
        * Computes the tansig transfer function, which is given by the equation:
        * <b>tansig(x) = 2 / (1 + exp(-2 * x)) - 1</b>
        * @param value {@inheritDoc}
        * @return The tansig result.
        */
       @Override
       public float compute(float value) {
           return (2f / (1f + (float) Math.exp(-2.0 * value))) - 1f;
       }
   }
}

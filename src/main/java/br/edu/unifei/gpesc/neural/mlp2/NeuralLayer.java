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
     * The transfer function.
     */
    public TransferFunction function;

    /**
     * Constructor for this layer.
     * @param firstNeuron The first neuron on the array.
     * @param length The quantity of neurons.
     * @param function The transfer function for this layer.
     */
    public NeuralLayer(int firstNeuron, int length, TransferFunction function) {
        this.first  = firstNeuron;
        this.length = length;
        this.last   = firstNeuron + length - 1;
        this.function = function;
    }

    /**
     * This constructor sets this layer to be after the prevLayer.
     * @param prevLayer The previous layer.
     * @param length The quantity of neurons.
     * @param function The transfer function for this layer.
     */
    public NeuralLayer(NeuralLayer prevLayer, int length, TransferFunction function) {
        this(prevLayer.last + 1, length, function);
    }
}

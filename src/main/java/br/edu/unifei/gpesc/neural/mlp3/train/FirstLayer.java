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

/**
 *
 * @author Isaac Caldas Ferreira
 */
public class FirstLayer {

    protected Neuron[] mNeuronArray;

    public FirstLayer(double[] neuronArray) {
        mNeuronArray = new Neuron[neuronArray.length];

        // optimization
        Neuron[] neurons = mNeuronArray;

        for (int i=0; i<neurons.length; i++) {
            neurons[i] = new Neuron(neuronArray[i]);
        }
    }

    public FirstLayer(int length) {
        mNeuronArray = new Neuron[length];

        // optimization
        Neuron[] neurons = mNeuronArray;

        for (int i=0; i<neurons.length; i++) {
            neurons[i] = new Neuron();
        }
    }

    public void setNeuron(Neuron[] neuronArray) {
        mNeuronArray = neuronArray;
    }

    public void setNeuron(FirstLayer layer) {
        mNeuronArray = layer.mNeuronArray;
    }

    public int length() {
        return mNeuronArray.length;
    }
}

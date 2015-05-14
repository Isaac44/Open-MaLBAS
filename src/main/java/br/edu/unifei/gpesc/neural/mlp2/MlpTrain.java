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
public class MlpTrain {

    /**
     * The max absolute value of the weight.
     */
    private float mMaxAbsoluteWeight = 0.1f;

    /**
     * The quantity of epochs to be processed.
     */
    private int mNumberOfEpochs = 20;

    /**
     * Random for bias and weight generation.
     */
    private final Random mRandom = new Random();

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
     * @param seed
     */
    public void setRandomizerSeed(long seed) {
        mRandom.setSeed(seed);
    }

}

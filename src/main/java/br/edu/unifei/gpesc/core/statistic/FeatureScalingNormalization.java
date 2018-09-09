/*
 * Copyright (C) 2017 - GEPESC - Universidade Federal de Itajuba
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

package br.edu.unifei.gpesc.core.statistic;

/**
 *
 * @author Isaac C. Ferreira
 */
public class FeatureScalingNormalization implements Normalization {

    /**
     * This method computes the feature scaling normalization.
     * <br>This normalization is used to bring all values into the range [0, 1].
     * This is also called unity-based normalization.
     * <p> The main formula is: N = (V - Vmin) / (Vmax - Vmin)
     *
     * @param dataIn The array with each V value.
     * @param dataOut The array with each N normalization result.
     */
    @Override
    public void normalize(int[] dataIn, double[] dataOut) {
        int smaller = dataIn[0];
        int bigger  = dataIn[0];

        for (int value : dataIn) {
            if (value < smaller) smaller = value;
            else if (value > bigger) bigger = value;
        }

        double divider = bigger - smaller;

        for (int i=0; i<dataIn.length; i++) {
            dataOut[i] = (dataIn[i] - smaller) / divider;
        }
    }
}

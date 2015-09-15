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

package br.edu.unifei.gpesc.core.statistic;

/**
 *
 * @author Isaac Caldas Ferreira
 */
public class Normalization {

    /**
     * This method computes the binary normalization.
     *
     * <p> Method: for each value in dataIn array is applied the follow rule:
     * <br> 1) value != 0, results in 1
     * <br> 2) value == 0, results in 0.
     *
     * <p> If the dataOut is null, then the result is put in dataIn array.
     * This mean that dataOut could be the same as dataIn.
     *
     * @param dataIn The input array.
     * @param dataOut The result array.
     */
    public static void binaryNormalization(int[] dataIn, int[] dataOut) {
        if (dataOut == null) dataOut = dataIn;

        for (int i=0; i<dataIn.length; i++) {
            dataOut[i] = (dataIn[i] != 0) ? 1 : 0;
        }
    }


    /**
     * This method computes the feature scaling normalization.
     * <br>This normalization is used to bring all values into the range [0, 1].
     * This is also called unity-based normalization.
     * <p> The main formula is: N = (V - Vmin) / (Vmax - Vmin)
     *
     * @param dataIn The array with each V value.
     * @param dataOut The array with each N normalization result.
     */
    public static void featureScaling(int[] dataIn, double[] dataOut) {
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

    public static void biggestNormalization(int[] dataIn, double[] dataOut) {
        int bigger = dataIn[0];

        for (int value : dataIn) {
            if (value > bigger) bigger = value;
        }

        double divider = (double) bigger;
        for (int i=0; i<dataIn.length; i++) {
            dataOut[i] = dataIn[i] / divider;
        }
    }

}

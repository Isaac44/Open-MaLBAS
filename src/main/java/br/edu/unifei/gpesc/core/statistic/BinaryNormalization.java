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
public class BinaryNormalization implements Normalization {

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
    @Override
    public void normalize(int[] dataIn, double[] dataOut) {
        for (int i=0; i<dataIn.length; i++) {
            dataOut[i] = (dataIn[i] != 0) ? 1 : 0;
        }
    }
}

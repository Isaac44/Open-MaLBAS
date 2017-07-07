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
public class BiggestNormalization implements Normalization {

    @Override
    public void normalize(int[] dataIn, double[] dataOut) {
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

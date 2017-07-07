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

import java.util.Arrays;

/**
 * This is used to find  what elements are used to create the characterization
 * of this Statistics.
 * @author Isaac Caldas Ferreira
 * @param <T>
 */
public class Characterization<T> {

    /**
     * The characteristic of the statistical element.
     */
    private final Characteristics<T> mCharacteristics;

    /**
     * The result array.
     */
    private final int[] mCharacterizationArray;

    /**
     * Initializes this class with the {@link Characteristics} that
     * will be used to generate the statistical characteristic array for the
     * calculated statistics.
     * @param statisticalVector
     * @see Statistics
     */
    public Characterization(Characteristics<T> statisticalVector) {
        mCharacteristics = statisticalVector;
        mCharacterizationArray = new int[statisticalVector.getObjectCount()];
    }

    /**
     * This method informs the data which belongs to the statistics.
     * <br> If the data is part of the set {@link Characteristics}
     * associated to this class, the position, of data, in the array is
     * incremented.
     * @param data The data of {@linkplain  Statistics}.
     * @see Statistics
     */
    public void insertData(T data) {
        Integer dataIndex = mCharacteristics.getDataIndex(data);
        if (dataIndex != null) {
            mCharacterizationArray[dataIndex]++;
        }
    }

    public void insertData(T data, int quantity) {
        Integer dataIndex = mCharacteristics.getDataIndex(data);
        if (dataIndex != null) {
            mCharacterizationArray[dataIndex] = quantity;
        }
    }

    /**
     * Returns the generated statistical characterization array.
     * <br><b>Important note: modifying this array will change the data of this
     * class.</b>
     * @return The generated statistical characterization array.
     */
    public int[] getCharacterizationArray() {
        return mCharacterizationArray;
    }

    /**
     * This method cleans the statistical characterization array. Use this for
     * recycling process (and avoid unnecessary data allocation).
     */
    public void cleanArray() {
        Arrays.fill(mCharacterizationArray, 0);
    }
}

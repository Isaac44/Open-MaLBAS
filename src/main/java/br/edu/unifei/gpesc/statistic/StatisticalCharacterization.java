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

package br.edu.unifei.gpesc.statistic;

/**
 * This is used to find  what elements are used to create the caracterization
 * of this Statistics.
 * @author Isaac Caldas Ferreira
 * @param <T>
 */
public class StatisticalCharacterization<T> {

    /**
     * The caracteristic of the statistical element.
     */
    private final StatisticalCharacteristic<T> mStatisticalCharacteristic;

    /**
     * The resulted caracteristical array.
     */
    private final int[] mStatisticalCharacterizationArray;

    /**
     * Initializes this class with the {@link StatisticalCharacteristic} that
     * will be used to generate the statistical characteristic array for the
     * calculated statistics.
     * @param statisticalVector
     * @see Statistics
     */
    public StatisticalCharacterization(StatisticalCharacteristic<T> statisticalVector) {
        mStatisticalCharacteristic = statisticalVector;
        mStatisticalCharacterizationArray = new int[statisticalVector.getObjectCount()];
    }

    /**
     * This method informes the data which pertences to the statistics.
     * <br> If the data is part of the set {@link StatisticalCharacteristic}
     * associated to this class, the position, of data, in the array is
     * incremented.
     * @param data The data of {@linkplain  Statistics}.
     * @see Statistics
     */
    public void insertData(T data) {
        Integer dataIndex = mStatisticalCharacteristic.getDataIndex(data);
        if (dataIndex != null) {
            mStatisticalCharacterizationArray[dataIndex]++;
        }
    }

    /**
     * Returns the generated statistical characterization array.
     * <br><b>Important note: modifying this array will change the data of this
     * class.</b>
     * @return The generated statistical characterization array.
     */
    public int[] getStatisticalCharacterizationArray() {
        return mStatisticalCharacterizationArray;
    }
}

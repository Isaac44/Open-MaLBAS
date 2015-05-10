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

import java.util.HashMap;

/**
 * This class will be used by the class {@link StatisticalCharacterization}.
 *
 * @author Isaac Caldas Ferreira
 * @param <T>
 */
public class StatisticalCharacteristic<T> {

    /**
     * The map of the statistical data (element) and an index.
     */
    private final HashMap<T, Integer> mObjectIndexMap = new HashMap<T, Integer>();

    /**
     * The number of diferent elements inserted.
     */
    private int mObjectCount = 0;

    /**
     * Inserts the data giving it an index. This mean that the first element
     * inserted is index 0, the second is index 1, etc.
     * <br>Repeated data will be ignored.
     * @param data The data to be inserted.
     */
    public void insertData(T data) {
        if (mObjectIndexMap.get(data) == null) {
            mObjectIndexMap.put(data, mObjectCount);
            mObjectCount++;
        }
    }

    /**
     * Gets the associated index of data. If there are no index associated, null
     * is returned.
     * @param data The data.
     * @return The index of associated to the data or null if there is none.
     */
    public Integer getDataIndex(T data) {
        return mObjectIndexMap.get(data);
    }

    /**
     * Gets the total of data inserted.
     * @return The total of data inserted.
     */
    public int getObjectCount() {
        return mObjectCount;
    }

}

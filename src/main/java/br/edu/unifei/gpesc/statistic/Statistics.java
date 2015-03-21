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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Isaac Caldas Ferreira
 * @param <T> The data (element) type.
 */
public class Statistics<T> implements Iterable<StatisticalData>{

    /**
     * The statistic hash map used to map the key data-element to statistic data.
     */
    private final HashMap<T, StatisticalData> mStatisticHashMap = new HashMap<T, StatisticalData>();

    /**
     * The size of all sets.
     */
    private final int[] mSetSizeArray;

    /**
     * Creates a Statistic object to manage all data objects.
     * @param numberOfSets
     */
    public Statistics(int numberOfSets) {
        mSetSizeArray = new int[numberOfSets];
    }

    /**
     * Insert a data to the statisc array. If the data already exists, then its
     * value is increased.
     * @param data The data.
     * @param set The set of the data.
     */
    public void insertData(T data, int set) {
        StatisticalData dataStatistic = mStatisticHashMap.get(data);

        if (dataStatistic == null) {
            dataStatistic = new StatisticalData(data, mSetSizeArray.length);
            mStatisticHashMap.put(data, dataStatistic);
        }

        dataStatistic.increment(set);
    }

    /**
     * Increment the size of set.
     * @param set The set.
     */
    public void incrementSetSize(int set) {
        mSetSizeArray[set]++;
    }

    /**
     * Decrement the size of set.
     * @param set The set.
     */
    public void decrementSetSize(int set) {
        mSetSizeArray[set]--;
    }

    /**
     * Gets the size of set.
     * @param set The set.
     * @return The set size.
     */
    public int getSetSize(int set) {
        return mSetSizeArray[set];
    }

    /**
     * Gets the quantity of statistical data stored.
     * @return The quantity of statistical data.
     */
    public int getSize() {
        return mStatisticHashMap.size();
    }

    public Collection<StatisticalData> getStatisticalDataArray() {
        return mStatisticHashMap.values();
    }

    /**
     * Gets the {@link Set} of all entries. An entry is represented by a pair
     * (element, statistical data).
     * @return The {@link Set} of all entries.
     */
    public Set<Map.Entry<T, StatisticalData>> getEntrySet() {
        return mStatisticHashMap.entrySet();
    }

    /**
     * Gets the array with the size of all sets.
     * @return The array with the size of all sets.
     */
    public int[] getSetSizeArray() {
        return mSetSizeArray;
    }

    @Override
    public Iterator<StatisticalData> iterator() {
        return mStatisticHashMap.values().iterator();
    }
}

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
package br.edu.unifei.gpesc.base.statistic;

/**
 * This class stores the statistics of an element in one or more sets.
 * @author Isaac Caldas Ferreira
 * @param <T>
 */
public class StatisticalData<T> {

    /**
     * The element.
     */
    private final T mElement;

    /**
     * The statistic array. This array contains the statistic information about
     * this object in all specified sets.
     */
    private final int[] mStatisticDataArray;

    /**
     * The statistical distribution for this data.
     */
    private double mStatisticalDistribution = 0.0;

    /**
     * Creates a data statistic, setting up the number of existent sets.
     * @param element The element. Use {@link
     * @param numberOfSets The number of sets where statistics are calculated.
     */
    public StatisticalData(T element, int numberOfSets) {
        mElement = element;
        mStatisticDataArray = new int[numberOfSets];
    }

    /**
     * Increment the occurrence of this data in a set.
     * @param set The set where this data was found.
     */
    public void increment(int set) {
        mStatisticDataArray[set]++;
    }

    /**
     * Decrement the occurrence of this data in a set.
     * @param set The set where this data was found.
     */
    public void decrement(int set) {
        mStatisticDataArray[set]--;
    }

    /**
     * Gets the statistic value of this data in a set.
     * @param set The set to get the statistic value.
     * @return The statistic value.
     */
    public int getStatistic(int set) {
        return mStatisticDataArray[set];
    }

    /**
     * Gets the data element of this statistical data.
     * @return The element.
     */
    public T getElement() {
        return mElement;
    }

    /**
     * Sets the statistical distribution for this data. Use
     * {@link StatisticalData#getStatisticalDistribution()}
     * to retrieve this value.
     * @param value The statistical data.
     * @see Census
     */
    public void setStatisticalDistribution(double value) {
        mStatisticalDistribution = value;
    }

    /**
     * Gets the statistical distribution previous setted.
     * @return The statistical distribution.
     * @see StatisticalData#setStatisticalDistribution(double)
     * @see Census
     */
    public double getStatisticalDistribution() {
        return mStatisticalDistribution;
    }

}

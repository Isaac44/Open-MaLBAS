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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author Isaac Caldas Ferreira
 * @param <T>
 */
public class Census<T> {

    /**
     * The statistics that will be used to calculate the {@link Census}
     */
    private final Statistic<T> mStatistics;

    /**
     * This list stores the {@link Data} of the {@link Statistic}. This is
     * used to be easier to sort the {@link Data}.
     */
    private final ArrayList<Data<T>> mDataList;

    /**
     * Sets the statistics for this {@link Census}.
     * @param statistics
     */
    public Census(Statistic<T> statistics) {
        mStatistics = statistics;
        mDataList = new ArrayList<Data<T>>(statistics.getSize());

        for (Data<T> data : statistics) {
            mDataList.add(data);
        }
    }

    /**
     * Computes the {@link Distribution} for all {@link Data}
     * of the {@link Statistic} setted.
     * @param distribution The statistics distribution model to be used.
     */
    public void computeDistribution(Distribution distribution) {
        if (mStatistics != null) {
            computeDistribution(mStatistics, distribution);
        }
    }

    /**
     * Sort the {@link Data}. If wanted the {@link DataComparator}
     * interface can be implemented for a custom sort.
     * @param comparable The comparable method.
     */
    public void sortData(DataComparator comparable) {
        Collections.sort(mDataList, comparable);
    }

    /**
     * Gets the {@link ArrayList} with all statistical data.
     * @return The statistical data list.
     */
    public ArrayList<Data<T>> getStatisticalDataList() {
        return mDataList;
    }

    /**
     * This interface is used to sort the data of this census. It is used
     * with {@link Census#sortData(DataComparator)}.
     * <br> This interface is convenient if is wanted to sort the data by its
     * element ({@link Data#getElement()}).
     * <p> Important note: The generic type (E) must be the same of the
     * {@link Census}, errors can occur if not.
     *
     * @param <E> The type of this comparator.
     */
    public interface DataComparator<E> extends Comparator<Data<E>> {}

    /**
     * This class implementation of {@link DataComparator} sorts the data by its
     * {@link Distribution}.
     * <br> Also sorts by crescent order: The first element is the lowest
     * @see Data#getDistribution()
     */
    public static class CrescentDistributionSort implements DataComparator<Object> {
        @Override
        public int compare(Data<Object> o1, Data<Object> o2) {
            return Double.compare(o1.getDistribution(), o2.getDistribution());
        }
    }

    /**
     * This class implementation of {@link DataComparator} sorts the data by its
     * {@link Distribution}.
     * <br> Also sorts by decrescent order: The first element is the biggest
     * @see Data#getDistribution()
     */
    public static class DecrescentDistributionSort implements DataComparator<Object> {
        @Override
        public int compare(Data<Object> o1, Data<Object> o2) {
            return Double.compare(o2.getDistribution(), o1.getDistribution());
        }
    }

    /**
     * This class implementation of {@link DataComparator} sorts the data by a
     * set.
     * <br> Also sorts by crescent order: The first element is the lowest
     * @see Data#getDistribution()
     */
    public static class CrescentStatisticSetSort implements DataComparator<Object> {

        /**
         * The set used in the sort.
         */
        private final int mmSet;

        /**
         * Initializes this class with the set to used in the sort.
         * @param set The set.
         */
        public CrescentStatisticSetSort(int set) {
            mmSet = set;
        }

        @Override
        public int compare(Data<Object> o1, Data<Object> o2) {
            return compareInt(o1.getStatistic(mmSet), o2.getStatistic(mmSet));
        }
    }

    /**
     * This class implementation of {@link DataComparator} sorts the data by a
     * set.
     * <br> Also sorts by crescent order: The first element is the lowest
     * @see Data#getDistribution()
     */
    public static class DecrescentStatisticSetSort implements DataComparator<Object> {

        /**
         * The set used in the sort.
         */
        private final int mmSet;

        /**
         * Initializes this class with the set to used in the sort.
         * @param set The set.
         */
        public DecrescentStatisticSetSort(int set) {
            mmSet = set;
        }

        @Override
        public int compare(Data<Object> o1, Data<Object> o2) {
            return compareInt(o2.getStatistic(mmSet), o1.getStatistic(mmSet));
        }
    }

    /**
     * Computes the {@link Distribution} for all {@link Data}
     * of the {@link Statistic}. The result values can be getted using
     * {@link Data#getDistribution()}.
     * @param statistics The statiscs.
     * @param distribution The statistics distribution model to be used.
     */
    public static void computeDistribution(Statistic<?> statistics, Distribution distribution) {
        double result;
        for (Data data : statistics) {
            result = distribution.compute(data, statistics);
            data.setStatisticalDistribution(result);
        }
    }

    public static int compareInt(int x, int y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }
}

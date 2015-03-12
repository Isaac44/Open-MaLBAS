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
    private final Statistics<T> mStatistics;

    /**
     * This list stores the {@link Data} of the {@link Statistics}. This is
     * used to be easier to sort the {@link StatisticalData}.
     */
    private final ArrayList<StatisticalData<T>> mDataList;

    /**
     * Sets the statistics for this {@link Census}.
     * @param statistics
     */
    public Census(Statistics<T> statistics) {
        mStatistics = statistics;
        mDataList = new ArrayList<StatisticalData<T>>(statistics.getSize());

        for (StatisticalData<T> data : statistics) {
            mDataList.add(data);
        }
    }

    /**
     * Computes the {@link StatisticalDistribution} for all {@link StatisticalData}
     * of the {@link Statistics} setted.
     * @param distribution The statistics distribution model to be used.
     */
    public void computeDistribution(StatisticalDistribution distribution) {
        if (mStatistics != null) {
            computeDistribution(mStatistics, distribution);
        }
    }

    /**
     * Sort the {@link StatisticalData}. If wanted the {@link DataComparator}
     * interface can be implemented for a custom sort.
     * @param comparable The comparable method.
     */
    public void sortData(DataComparator comparable) {
        Collections.sort(mDataList, comparable);
    }

    public ArrayList<StatisticalData<T>> getStatisticalDataList() {
        return mDataList;
    }

    public interface DataComparator<E> extends Comparator<StatisticalData<E>> {}

    public static class DistributionSort implements DataComparator<Object> {
        @Override
        public int compare(StatisticalData<Object> o1, StatisticalData<Object> o2) {
            return Double.compare(o1.getStatisticalDistribution(), o2.getStatisticalDistribution());
        }
    }

    public static class StatisticSetSort implements DataComparator<Object> {

        private final int mmSet;

        public StatisticSetSort(int set) {
            mmSet = set;
        }

        @Override
        public int compare(StatisticalData<Object> o1, StatisticalData<Object> o2) {
            return Integer.compare(o1.getStatistic(mmSet), o2.getStatistic(mmSet));
        }

    }

    /**
     * Computes the {@link StatisticalDistribution} for all {@link StatisticalData}
     * of the {@link Statistics}. The result values can be getted using
     * {@link StatisticalData#getStatisticalDistribution()}.
     * @param statistics The statiscs.
     * @param distribution The statistics distribution model to be used.
     */
    public static void computeDistribution(Statistics<?> statistics, StatisticalDistribution distribution) {
        double result;
        int[] setSizeArray = statistics.getSetSizeArray();
        for (StatisticalData data : statistics) {
            result = distribution.compute(data, setSizeArray);
            data.setStatisticalDistribution(result);
        }
    }
}

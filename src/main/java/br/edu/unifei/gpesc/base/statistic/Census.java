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
 *
 * @author isaac
 * @param <T> The statistical data type.
 */
public class Census<T> {

    /**
     * The statistics used to calculate the {@link Census}.
     */
    private Statistics<T> mStatistics;

    /**
     * Initializes and calculate the census.
     * @param statistics The statistics to calculate the census.
     */
    public Census(Statistics<T> statistics) {
        setStatistics(statistics);
    }

    /**
     * Sets the statistics for this {@link Census}.
     * @param statistics
     */
    public final void setStatistics(Statistics<T> statistics) {
        mStatistics = statistics;
    }

    /**
     * Computes the {@link StatisticalDistribution} for all {@link StatisticalData}
     * of the {@link Statistics} setted.
     * @param distribution The statistics distribution model to be used.
     */
    public void computeDistribuition(StatisticalDistribution distribution) {
        double value;
        int[] setSizeArray = mStatistics.getSetSizeArray();
        for (StatisticalData data : mStatistics.getStatisticalDataArray()) {
            value = distribution.compute(data, setSizeArray);
            data.setStatisticalDistribution(value);
        }
    }
}
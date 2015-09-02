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
 *
 * @author Isaac Caldas Ferreira
 */
public class SimpleFrequencyDistribution implements Distribution {

    @Override
    public double compute(StatisticalData data, Statistics statistics) {
        double result = 0.0;
        for (int i=0; i<statistics.getSetCount(); i++) {
            result += data.getStatistic(i);
        }
        return result;
    }

}

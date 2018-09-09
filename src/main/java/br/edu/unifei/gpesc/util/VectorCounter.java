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
package br.edu.unifei.gpesc.util;

/**
 *
 * @author isaac
 */
public class VectorCounter {

    private int mZeroedVectors;
    private int mGoodVectors;

    private int mZerosCount;

    public void resetCounters() {
        mZeroedVectors = 0;
        mGoodVectors = 0;
    }

    public void incGoodVectorsCount() {
        mGoodVectors++;
    }

    public void incZeroedVectorsCount() {
        mZeroedVectors++;
    }

    public void incZeroesCount() {
        mZerosCount++;
    }

    public void addZeroesCount(int quantity) {
        mZerosCount += quantity;
    }

    public int getGoodVectorsCount() {
        return mGoodVectors;
    }

    public int getZeroedVectorsCount() {
        return mZeroedVectors;
    }

    public int getTotalVectorsCount() {
        return mGoodVectors + mZeroedVectors;
    }

    public int getZerosCount() {
        return mZerosCount;
    }
}

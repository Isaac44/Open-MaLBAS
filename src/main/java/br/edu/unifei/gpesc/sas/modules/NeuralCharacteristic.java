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
package br.edu.unifei.gpesc.sas.modules;

/**
 *
 * @author isaac
 */
public enum NeuralCharacteristic {

    /**
     * The HAM constant.
     */
    HAM("not spam", 0, 1),

    /**
     * The SPAM constant.
     */
    SPAM("spam", 1, 0),

    /**
     * The error constant.
     */
    ERROR("unknown");

    /**
     * The max value that the minor value may have.
     */
    public static final float MINOR_MAX_VALUE = 0.4f;

    /**
     * The min value that the major value may have.
     */
    public static final float MAJOR_MIN_VALUE = 0.6f;

    /**
     * The name of this enum.
     */
    public final String NAME;

    /**
     * The string value for this enum. This is used to send data to the
     * neural network.
     */
    public final String STR_VALUE;

    /**
     * The int array for this enum. This is used for identify the result of the
     * neural network.
     */
    public final int[] INT_VALUE;

    /**
     * Creates a new enum.
     * @param name The name of this enum.
     * @param firstValue The first value of this enum.
     * @param secondValue The second value of this enum.
     */
    private NeuralCharacteristic(String name, int... values) {
        NAME = name;
        if (values.length > 0) {
            STR_VALUE = values[0] + " " + values[1];
        }
        else {
            STR_VALUE = "0 0";
        }
        INT_VALUE = values;
    }

    /**
     * Find the enum that can be represented by the first and second values.
     * @param firstValue The first value.
     * @param secondValue The second value.
     * @return The enum that is represented by the arguments.
     */
    public static NeuralCharacteristic getCharacteristic(float firstValue, float secondValue) {
        if ((firstValue <= MINOR_MAX_VALUE) && (MAJOR_MIN_VALUE <= secondValue)) {
            return HAM;
        }
        else if ((secondValue <= MINOR_MAX_VALUE) && (MAJOR_MIN_VALUE <= firstValue)) {
            return SPAM;
        }

        return ERROR;
    }
}

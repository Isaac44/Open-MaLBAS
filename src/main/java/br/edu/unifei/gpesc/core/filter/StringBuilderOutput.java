/*
 * Copyright (C) 2017 - GEPESC - Universidade Federal de Itajuba
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

package br.edu.unifei.gpesc.core.filter;

/**
 *
 * @author Isaac C. Ferreira
 */
public class StringBuilderOutput implements FilterOutput {

    private final StringBuilder mStrBuilder;

    public StringBuilderOutput() {
        mStrBuilder = new StringBuilder();
    }


    public StringBuilderOutput(int capacity) {
        mStrBuilder = new StringBuilder(capacity);
    }

    @Override
    public FilterOutput append(String str) {
        mStrBuilder.append(str);
        return this;
    }

    @Override
    public String toString() {
        return mStrBuilder.toString();
    }

}

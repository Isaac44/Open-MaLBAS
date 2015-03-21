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
package br.edu.unifei.gpesc.sas.filter;

/**
 *
 * @author isaac
 */
public class MonetaryWordTextFilter implements TextFilter {

    /**
     * The symbols usually founded in monetary values.
     */
    private static final String[] MONETARY_SYMBOLS = {"$", "%"};

    /**
     * Checks if the input text contains any monetary symbols. See
     * {@link MonetaryWordTextFilter#MONETARY_SYMBOLS}.
     * @param word The word to analysed.
     * @return The value of {@link Mark#MONETARY}, if the word contains any
     * monetary symbol.<br>
     * The input param without modification, else.
     */
    @Override
    public String filter(String word) {

        for (String symbol : MONETARY_SYMBOLS) {
            if (word.contains(symbol)) return Mark.MONETARY.value();
        }

        return word;
    }

}

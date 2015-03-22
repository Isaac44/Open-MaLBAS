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

import java.util.regex.Pattern;

/**
 *
 * @author isaac
 */
public class NumberFilter extends TextFilter {

    /**
     * The "have at least one number" pattern.
     */
    private static final Pattern NUMBER_PATTERN = Pattern.compile(".*\\d.*");


    /**
     * Checks if the input text contains at least one number.
     * @param text The input text to analysed.
     * @return The value of {@link TextMark#NUMBER}, if the word contains at
     * least one number.<br>
     * The input param without modification, otherwise.
     */
    @Override
    public String filter(String text) {
        if (NUMBER_PATTERN.matcher(text).find()) {
            setResult(Result.BREAK);
            return TextMark.NUMBER.value();
        }
        else {
            setResult(Result.CONTINUE);
            return text;
        }
    }

}

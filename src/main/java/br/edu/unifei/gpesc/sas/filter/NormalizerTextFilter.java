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

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 *
 * @author isaac
 */
public class NormalizerTextFilter extends TextFilter {

    /**
     * The ascii pattern. This is used to remove all non-ascii characters for
     * the text.
     */
    private static final Pattern NON_ASCII_PATTERN = Pattern.compile("[^\\p{ASCII}]");

    /**
     * Constructs this class setting the {@link TextFilter#mFilterResult} to
     * {@link Result#CONTINUE}. This mean that all filter classes, in the executor,
     * can be processed after this one.
     */
    public NormalizerTextFilter() {
        setResult(Result.CONTINUE);
    }

    /**
     * Normalizes the input text. <br>
     * This method converts all characters non-ASCII  (accents, for example)
     * to ASCII and than puts all to lower case.
     * @param text The input text.
     * @return The normalized and lower case text.
     */
    @Override
    public String filter(String text) {
        text = Normalizer.normalize(text, Normalizer.Form.NFD);
        return NON_ASCII_PATTERN.matcher(text).replaceAll("").toLowerCase();
    }

}

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
package br.edu.unifei.gpesc.core.filter;

import java.util.regex.Pattern;

/**
 *
 * @author Isaac Caldas Ferreira
 */
public class UrlFilter extends TextFilter {

    /**
     * URL pattern.
     */
    private static final Pattern URL_PATTERN = Pattern.compile("\\.\\w{2}");

    /**
     * HTTP symbol.
     */
    private static final String HTTP_SYMBOL = "http";

    /**
     * Checks if the input text contain "http" or "." followed by 2 words
     * (example: ".com" and ".aa" are valids).
     * @param text The input text.
     * @return If the input appears to be url, {@link TextMark#URL} is returned.
     * <br>Else the input is returned.
     */
    @Override
    public String filter(String text) {

        if (text.contains(HTTP_SYMBOL) || URL_PATTERN.matcher(text).find()) {
            setResult(Result.BREAK);
            return TextMark.URL.value();
        }

        setResult(Result.CONTINUE);
        return text;
    }

}

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

import org.unbescape.html.HtmlEscape;

/**
 * This "broken html" text filter is used when a HTML, processed by JSoup,
 * still contains any "text-coded charset" (escaped HTML).
 * <br>
 * Basically, if you have to use this filter is because has something wrong with
 * the input HTML.
 * <p> Well, I'm here to help with the mess of others! :) </p>
 *
 * @author Isaac Caldas Ferreira
 */
public class UnescapeTextFilter extends TextFilter {

    /**
     * The ampersand constant.
     */
    private static final String AMPERSAND = "&";

    /**
     * The ampsersand text-coded pattern contant.
     */
    private static final String AMPERSAND_ESCAPE = "&amp";

    /**
     * Default constructor. Sets the result of this class to be always
     * {@link Result#CONTINUE}.
     */
    public UnescapeTextFilter() {
        setResult(Result.CONTINUE);
    }

    /**
     * Process the input string and if its contains
     * {@link UnescapeTextFilter#AMPERSAND_ESCAPE}, its returned an unescaped
     * html processed by {@link HtmlEscape#unescapeHtml(String)}.
     * @param text The input text.
     * @return The string unescaped, if its contain the espace pattern, or
     * the input without modifications.
     */
    @Override
    public String filter(String text) {
        if (text.contains(AMPERSAND)) {
            String result =  HtmlEscape.unescapeHtml(text.replaceAll(AMPERSAND_ESCAPE, AMPERSAND));
            return result;
        }
        else {
            return text;
        }
    }
}

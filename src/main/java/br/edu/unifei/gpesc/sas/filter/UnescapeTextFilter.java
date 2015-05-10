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
 *
 * @author isaac
 */
public class UnescapeTextFilter extends TextFilter {

    private static final String AMPERSAND = "&";
    private static final String AMPERSAND_ESCAPE = "&amp";

    public UnescapeTextFilter() {
        setResult(Result.CONTINUE);
    }

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

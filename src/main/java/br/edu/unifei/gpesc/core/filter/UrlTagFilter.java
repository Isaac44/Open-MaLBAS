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

import org.jsoup.nodes.Element;

/**
 * This tag filter verifies if the current {@link Element} contains the
 * attribute "href". If so, the {@link TextMark#URL} will be appended to the
 * output.
 *
 * @author Isaac Caldas Ferreira
 */
public class UrlTagFilter implements TagFilter {

    /**
     * Checks if the current element argument if a "url Element", which means
     * that this Element contains the attribute "href".
     * If this is true, {@link TextMark#URL} will be appended to strBuilder.
     *
     * @param element {@inheritDoc}
     * @param strBuilder {@inheritDoc}
     * @return Will always be {@link Result#CONTINUE}.
     */
    @Override
    public Result filter(Element element, FilterOutput strBuilder) {
        if (!element.attr("href").isEmpty()) {
            strBuilder.append(TextMark.URL.value()).append(" ");
        }
        return Result.CONTINUE;
    }

}

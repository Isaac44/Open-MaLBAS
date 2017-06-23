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
 * The interface for the Tag Filter.
 * <br>
 * It is used by the {@link FilterExecutor} to process tag elements of the HTML.
 *
 * @author Isaac Caldas Ferreira
 */
public interface TagFilter {

    /**
     * Filters an Element.
     * <br>
     * Every output token should be placed on the strBuilder argument output.
     * @param element The Element with the current tag and attributes.
     * @param output Where the processed data goes.
     * @return The Result information for the FilterExecutor.
     */
    public Result filter(Element element, FilterOutput output);
}

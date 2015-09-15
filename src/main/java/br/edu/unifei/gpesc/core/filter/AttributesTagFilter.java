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

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;

/**
 * This Tag Filter appends all internal attributes of the current Element.
 * <br>
 * It uses the suffix "!_in_attr", where attr is the attribute.
 * 
 * @author Isaac Caldas Ferreira
 */
public class AttributesTagFilter implements TagFilter {

    /**
     * This filter goes to every attribute, of the element argument, and
     * appends it (the token) to the strBuilder argument, using the
     * syntax "!_in_attr", where attr is the attribute.
     * <br>
     * Every token appended will be separated by an space.
     *
     * @param element The Element with the current tag and attributes.
     * @param strBuilder The output StringBuilder.
     * @return The return will always be {@link Result#CONTINUE}.
     */
    @Override
    public Result filter(Element element, StringBuilder strBuilder) {
        Attributes attributes = element.attributes();

        if (attributes.size() > 0) {
            strBuilder.append("!_in_").append(element.tagName()).append(" ");
            for (Attribute attribute : attributes) {
                strBuilder.append(attribute.getKey()).append(" ");
            }
        }

        return Result.CONTINUE;
    }
}
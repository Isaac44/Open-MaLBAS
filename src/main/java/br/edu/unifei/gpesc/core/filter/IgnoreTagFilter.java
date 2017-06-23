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
import org.jsoup.parser.Tag;

/**
 * This Tag Filter ignores the tags in the IGNORE_TAG_ARRAY. This mean that
 * the process will skip all the content in this tag and append "!_ignore_tag",
 * where tag is the tag ignored.
 *
 * @author Isaac Caldas Ferreira
 */
public class IgnoreTagFilter implements TagFilter {

    /**
     * The ignore tag array.
     */
    private final Tag[] mIgnoreTagArray;

    /**
     * The constructor of the ignore tag array.
     * @param tagNames The array with the name of the tags to be ignored.
     */
    public IgnoreTagFilter(String... tagNames) {
        mIgnoreTagArray = new Tag[tagNames.length];

        for (int i=0; i<tagNames.length; i++) {
            mIgnoreTagArray[i] = Tag.valueOf(tagNames[i]);
        }
    }

    /**
     * Checks if the current tag is present in the ignore tag array.
     * @param element The Element with the tag and attributes.
     * @param output {@inheritDoc}
     * @return {@link Result#SKIP_TAG} if the tag is in the ignore array or
     * {@link Result#CONTINUE} otherwise.
     */
    @Override
    public Result filter(Element element, FilterOutput output) {
        Tag tag = element.tag();

        for (Tag ignoreTag : mIgnoreTagArray) {
            if (ignoreTag.equals(tag)) {
                output.append("!_ignore_").append(ignoreTag.getName());
                return Result.SKIP_TAG;
            }
        }

        return Result.CONTINUE;
    }
}

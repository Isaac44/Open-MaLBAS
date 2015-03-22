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

import java.util.Scanner;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author isaac
 */
public class FilterExecutor {

    private final TagFilter[] mTagFilterArray = {new IgnoreTagFilter(), new UrlTagFilter()};
    private final TextFilterExecutor mTextFilterExecutor = new TextFilterExecutor();

    public String filterHtml(Elements elements) {
        // variables
        Result tagResult;

        Element element;
        int index = 0;

        String text;

        boolean forceNextLoop = false;
        boolean forceEndWhile = false;

        // result
        StringBuilder strBuilder = new StringBuilder();

        // process
        do {
            element = elements.get(index);
            index++;

            // process tag
            for (TagFilter tagFilter : mTagFilterArray) {
                tagResult = tagFilter.filter(element, strBuilder);

                switch (tagResult)
                {
                    case SKIP_TAG: {
                        index = getNextElementSiblingIndex(elements, element);
                        if (index == -1) forceEndWhile = true;
                        else forceNextLoop = true;
                        break;
                    }
                }
            }

            if (forceEndWhile) break;
            if (forceNextLoop) continue;

            // process text
            text = element.ownText();
            if (!text.isEmpty()) {
                filterText(text, strBuilder);
            }

        } while (index < elements.size());

        return strBuilder.toString();
    }

    /**
     * This method filters all input text by
     * @param text
     * @param strBuilder
     */
    public void filterText(String text, StringBuilder strBuilder) {
        Scanner scanner = new Scanner(text);
        String resultText;

        while (scanner.hasNext()) {
            resultText = mTextFilterExecutor.filter(scanner.next());
            strBuilder.append(resultText).append(" ");
        }
        strBuilder.append("\n");
    }

    /**
     * This method finds the index sibling's index of element.
     * @param elements The html tree.
     * @param element The element.
     * @return The sibling's index of element or -1 if there is none.
     */
    private int getNextElementSiblingIndex(Elements elements, Element element) {
        int nextSibling = element.siblingIndex() + 1;
        Element parent = element.parent();
        if (parent != null) {
            Elements children = parent.children();
            if (nextSibling < children.size()) {
                Element sibling = children.get(nextSibling);
                if (sibling != null) {
                    return elements.indexOf(sibling);
                }
            }
        }
        return -1;
    }

}

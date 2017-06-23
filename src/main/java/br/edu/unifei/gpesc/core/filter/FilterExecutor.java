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

import java.util.Scanner;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * This class process all filters ({@link TextFilter} and {@link TagFilter}).
 * <br>
 * The execution of the filters is determinated by the order of it in the array.
 * After a filter is processed its returns a {@link Result} that informs how
 * this executor should proceed.
 *
 * @author Isaac Caldas Ferreira
 */
public class FilterExecutor {

    /**
     * The Tag Filter array.
     */
    private final TagFilter[] mTagFilterArray;

    /**
     * The Text Filter array.
     */
    private final TextFilter[] mTextFilterArray;

    /**
     * The default executor.
     */
    public FilterExecutor() {
        mTagFilterArray = new TagFilter[] {
            new IgnoreTagFilter("script"), new UrlTagFilter(), new ImageTagFilter(),
            new AttributesTagFilter()};

        mTextFilterArray = new TextFilter[] {
            new MonetaryTextFilter(), new UrlFilter(),
            new UnescapeTextFilter(),
            new NumberFilter(), new PunctuationTextFilter(),
            new SmallBigWordTextFilter(), new NormalizerTextFilter()};
    }

    /**
     * Initializes this executor with filters arrays.
     * <p><b> Important: be careful with the order of the filters!</b></p>
     * @param tagFilterArray The tag filter array.
     * @param textFilterArray The text filter array.
     */
    public FilterExecutor(TagFilter[] tagFilterArray, TextFilter[] textFilterArray) {
        mTagFilterArray = tagFilterArray;
        mTextFilterArray = textFilterArray;
    }

    /**
     * Filters a HTML parsed by the JSoup.
     *
     * @param elements The root element array.
     * @param output Where all the elements are appended after applied the filters.
     */
    public void filterHtml(Elements elements, FilterOutput output) {
        // variables
        Result tagResult;

        Element element;
        String text;

        // Process
        int nextElement = 0;

        while (nextElement < elements.size()) {
            element = elements.get(nextElement);
            nextElement++;

            // process tag
            for (TagFilter tagFilter : mTagFilterArray) {
                tagResult = tagFilter.filter(element, output);

                if (tagResult == Result.SKIP_TAG) {
                    nextElement += getChildrenCount(element);
                    break;
                }
            }

            // Process text
            text = element.ownText();
            if (!text.isEmpty()) {
                filterText(text, output);
            }
        }
    }

    /**
     * This method filters all input text by
     * @param text
     * @param output
     */
    public void filterText(String text, FilterOutput output) {
        Scanner scanner = new Scanner(text);
        String resultText;

        while (scanner.hasNext()) {
            resultText = filterText(scanner.next());
            output.append(resultText).append(" ");
        }
        output.append("\n");
    }

    /**
     * Filters the input text by all the setted {@link TextFilter}.
     * @param text The input text.
     * @return The filtered text.
     */
    public String filterText(String text) {
        for (TextFilter textFilter : mTextFilterArray) {
            text = textFilter.filter(text);
            if (textFilter.getResult() == Result.BREAK) break;
        }
        return text;
    }

    public int getChildrenCount(Element parent) {
        Elements children = parent.children();
        int sum = children.size();

        for (Element child : children) {
            sum += getChildrenCount(child);
        }

        return sum;
    }
}

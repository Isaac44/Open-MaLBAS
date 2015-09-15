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
package test.app;

import br.edu.unifei.gpesc.core.filter.FilterExecutor;
import java.io.File;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 *
 * @author isaac
 */
public class HtmlFilterTest {

    public static void main(String[] args) throws IOException {
        Document document = Jsoup.parse(new File("/home/isaac/Unifei/Mestrado/SAS/Jsoup/soup_test.html"), "ASCII");
        Elements elements = document.body().getAllElements();

        FilterExecutor hfe = new FilterExecutor();
        String htmlFiltered = hfe.filterHtml(elements);

        System.out.println(htmlFiltered);
    }

}

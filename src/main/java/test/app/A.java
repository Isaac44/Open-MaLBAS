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

import br.edu.unifei.gpesc.core.filter.UrlFilter;

/**
 *
 * @author isaac
 */
public class A {
    public static void main(String[] args)  {
        UrlFilter urlFilter = new UrlFilter();
        System.out.println(urlFilter.filter("a.com"));
        System.out.println(urlFilter.filter("a.c"));
        System.out.println(urlFilter.filter("a."));
        System.out.println(urlFilter.filter("aadsf.om"));

    }
}

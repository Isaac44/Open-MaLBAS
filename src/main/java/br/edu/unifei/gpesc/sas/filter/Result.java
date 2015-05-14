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

/**
 * Constants for the {@link FilterExecutor}.
 * @author Isaac Caldas Ferreira
 */
public enum Result {

    /**
     * Informs that the processing of the current
     * {@link org.jsoup.nodes.Element} should continue.
     */
    CONTINUE,

    /**
     * Informs that the processing of the current (text or
     * {@link org.jsoup.nodes.Element}) should be stopped.
     */
    BREAK,

    /**
     * Informs that the processing of the current
     * {@link org.jsoup.nodes.Element} must skip all its childs and got to the
     * next {@link org.jsoup.nodes.Element} simbling.
     */
    SKIP_TAG;
}

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

/**
 * The abstract class used by the {@link FilterExecutor} to process text.
 * @author Isaac Caldas Ferreira
 */
public abstract class TextFilter {

    /**
     * Informs what the executor should do after filter the text.
     */
    private Result mFilterResult;

    /**
     * Filters the input text.
     * @param text The input text.
     * @return The filtered text.
     */
    public abstract String filter(String text);

    /**
     * Gets result of the filter.
     * @return The result of the filter.
     */
    public Result getFilterResult() {
        return mFilterResult;
    }

    /**
     * Implementations of this class shoud use this method to set the result
     * of the filter.
     * @param result The result.
     * @see TextFilter#getFilterResult()
     */
    protected void setResult(Result result) {
        mFilterResult = result;
    }
}

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
 *
 * @author isaac
 */
public enum TextMark {

    /**
     * Small word constant.
     */
    SMALL_WORD("!_SMALL_WORD"),

    /**
     * Big word constant.
     */
    BIG_WORD("!_BIG_WORD"),

    /**
     * Monetary constant.
     */
    MONETARY("!_MONETARY"),

    /**
     * URL constant.
     */
    URL("!_URL");

    /**
     * The constant mark. This is used by the filters class.
     */
    private final String mMark;

    /**
     * Constructs an enumerator with a mark.
     *
     * @param mark
     */
    private TextMark(String mark) {
        mMark = mark;
    }

    /**
     * Gets the mark value associated with this enumerator.
     *
     * @return The associated mark value.
     */
    public String value() {
        return mMark;
    }
}

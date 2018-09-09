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
 * Checks the size of the input text. If is bigger or equals that the specified
 * big length, {@link TextMark#BIG_WORD} is returned. Else, if the length is
 * small or equals to the specified small length, {@link TextMark#SMALL_WORD} is
 * returned. Otherwise, the input is returned.
 *
 * @author Isaac Caldas Ferreira
 */
public class SmallBigWordTextFilter extends TextFilter {

    /**
     * The default size that determines a big word.
     */
    public static final int DEFAULT_BIG_WORD_SIZE = 20;

    /**
     * The default size that determines a small word.
     */
    public static final int DEFAULT_SMALL_WORD_SIZE = 3;

    /**
     * The size that determines a big word.
     */
    private final int mBigWordSize;

    /**
     * The size that determines a small word.
     */
    private final int mSmallWordSize;

    /**
     * Initializes this class with the default values for the big and small word sizes.
     *
     * @see SmallBigWordTextFilter#DEFAULT_BIG_WORD_SIZE
     * @see SmallBigWordTextFilter#DEFAULT_SMALL_WORD_SIZE
     */
    public SmallBigWordTextFilter() {
        mBigWordSize = DEFAULT_BIG_WORD_SIZE;
        mSmallWordSize = DEFAULT_SMALL_WORD_SIZE;
    }

    /**
     * Initializes this class and sets the values for the big and small word sizes.
     *
     * @param smallWordSize The small word size
     * @param bigWordSize The big word size.
     */
    public SmallBigWordTextFilter(int smallWordSize, int bigWordSize) {
        mBigWordSize = bigWordSize;
        mSmallWordSize = smallWordSize;
    }

    /**
     * Checks if the word size fits in the determined sizes.
     * <br>
     * If the string size is big or small, this filter informs that the current
     * processing should be stopped.
     *
     * @param word The word to be analyzed.
     * @return The value of {@link TextMark#SMALL_WORD}, if the word size is
     * less or equals {@link SmallBigWordTextFilter#mSmallWordSize}.<br>
     * The value of {@link TextMark#BIG_WORD}, if the word size is
     * higher or equals {@link SmallBigWordTextFilter#mSmallWordSize}.<br>
     * The input param without modification, else.
     */
    @Override
    public String filter(String word) {
        int wordSize = word.length();

        if (wordSize <= mSmallWordSize) {
            setResult(Result.BREAK);
            return TextMark.SMALL_WORD.value();
        }

        if (mBigWordSize <= wordSize) {
            setResult(Result.BREAK);
            return TextMark.BIG_WORD.value();
        }

        setResult(Result.CONTINUE);
        return word;
    }
}

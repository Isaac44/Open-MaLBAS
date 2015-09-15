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

import java.util.regex.Pattern;

/**
 * Find and removes all punctuation symbols.
 * @author Isaac Caldas Ferreira
 */
public class PunctuationTextFilter extends TextFilter {

    /**
     * The punctuation pattern. This is used to remove all punctuation for the
     * text. The punctuation symbols: [,.;!?(){}\\[\\]<>%]
     */
    private static final Pattern PUNCTUATION_PATTERN = Pattern.compile("[\\p{Punct}]");

    /**
     * Constructs this class setting the {@link TextFilter#mFilterResult} to
     * {@link Result#CONTINUE}. This mean that all filter classes, in the executor,
     * can be processed after this one.
     */
    public PunctuationTextFilter() {
        setResult(Result.CONTINUE);
    }

    /**
     * This method filters the input text by removing all punctuation symbols.
     * <br> The punctuation symbols that will be removed are:
     * <b>! " # $ % & ' * + , - . / : ; < = > ? @ [ ] ^ _ ` { | } ~</b>
     * <p> Note: be careful on use this filter. It removes all
     * punctuation and, because of that, others filter may not work properly.
     * @param text The input text.
     * @return The text without all punctuation symbols.
     */
    @Override
    public String filter(String text) {
        return PUNCTUATION_PATTERN.matcher(text).replaceAll("");
    }

}

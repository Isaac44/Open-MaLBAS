/*
 * Copyright (C) 2015 - GEPESC - Universidade Federal de Itajuba
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
package br.edu.unifei.gpesc.core.antispam;

import java.io.File;
import java.io.InputStream;

/**
 *
 * @author Isaac Caldas Ferreira
 */
public interface AntiSpam {

    public static enum Result {

        /**
         * The Not-Spam Class
         */
        HAM,

        /**
         * The Spam Class
         */
        SPAM,

        /**
         * When an e-mail cannot be classified as {@link #HAM} nor {@link #SPAM}
         */
        UNKNOWN
    }

    /**
     * Process an e-mail file.
     *
     * @param mailFile The input file
     * @return {@link Result#HAM}, {@link Result#SPAM} or {@link Result#UNKNOWN}
     */
    public Result processMail(File mailFile);

    /**
     * Process an e-mail from stream.
     *
     * @param mailStream The input file
     * @return {@link Result#HAM}, {@link Result#SPAM} or {@link Result#UNKNOWN}.
     */
    public Result processMail(InputStream mailStream);
}

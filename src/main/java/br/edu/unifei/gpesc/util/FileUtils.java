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
package br.edu.unifei.gpesc.util;

import java.io.File;
import java.io.FileFilter;

/**
 *
 * @author isaac
 */
public class FileUtils {

    private static FileFilter sFileFilter;

    public static FileFilter getFileFilter() {
        if (sFileFilter == null) {
            sFileFilter = new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.isFile();
                }
            };
        }
        return sFileFilter;
    }

}

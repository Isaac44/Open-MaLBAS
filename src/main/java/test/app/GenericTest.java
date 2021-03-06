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

import br.edu.unifei.gpesc.util.FileUtils;
import java.io.File;

/**
 *
 * @author isaac
 */
public class GenericTest {

    public static void main(String[] args) {

        for (File f : new File("/home/isaac/Unifei/Mestrado/SAS/Statistics/DataSample/spam/").listFiles(new FileUtils.IsFileFilter())) {
            System.out.println("s = " + f.getAbsolutePath());
        }

    }

}

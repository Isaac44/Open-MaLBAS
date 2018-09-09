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

import br.edu.unifei.gpesc.core.modules.Filter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author isaac
 */
public class SASFilterTest {

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        Filter filter = new Filter();

        String path = "/home/isaac/Unifei/Mestrado/SAS/Mail_Test/TESTES/TREC-2005-2006-2007/01_raw_data/ham/";
        String file = "trec_2005_183_203.eml";

//        String out = filter.filterMail(path + file);
//        System.out.println("out = " + out);

//        FileOutputStream fileStream = new FileOutputStream(path + "cleanned/" + file);
//        fileStream.write(out.getBytes("ASCII"));
//        fileStream.close();
    }

}

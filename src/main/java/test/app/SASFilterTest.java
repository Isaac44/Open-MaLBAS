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

import br.edu.unifei.gpesc.sas.modules.SASFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author isaac
 */
public class SASFilterTest {

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        SASFilter filter = new SASFilter();

        String path = "/home/isaac/Unifei/Mestrado/SAS/Mail/test/";
        String file = "smtp_1377305710_0x7fd6dc02b830_835.eml";

        String out = filter.filterMail(path + "raw/" + file);

        FileOutputStream fileStream = new FileOutputStream(path + "cleanned/" + file);
        fileStream.write(out.getBytes("ASCII"));
        fileStream.close();
    }

}

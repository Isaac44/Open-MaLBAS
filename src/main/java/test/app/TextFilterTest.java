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

import br.edu.unifei.gpesc.sas.filter.TextFilterExecutor;
import java.util.Scanner;

/**
 *
 * @author isaac
 */
public class TextFilterTest {

    public static void main(String[] args) {
        TextFilterExecutor textFilter = new TextFilterExecutor();

        String str = "money$ ultrabigstringarraytobecatchonthefilterhasitis sm ml                ajksfçslfkasmc áá eŕsfgcźxcvrŕŕŕŕãs dasẽẽ©ŋæßðđŋħ®ħ®đđß";

        Scanner scan = new Scanner(str);
        StringBuilder strBuilder = new StringBuilder();

        String result;

        for (int i=0; i<100000; i++) {
            while (scan.hasNext()) {
                result = textFilter.filter(scan.next());
                strBuilder.append(result).append(" ");
            }
            scan = new Scanner(str);
            strBuilder.append("\n");
        }


//        System.out.println("result=" + strBuilder.toString());

    }

}

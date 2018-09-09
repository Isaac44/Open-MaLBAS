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
package test.auxiliar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 * @author Isaac Caldas Ferreira
 */
public class StatisticsAnalyzer {

    private static final String LINE_PATTERN = "\t%-15s: %02d\n";
//    private static final String LINE_PATTERN = "%s %02d\n";

    private static final String[] TOKENS = {"macaco", "ferragem", "conselho", "serra", "aparelho", "trem", "banda", "listras", "ovelha"};
    static {
        Arrays.sort(TOKENS);
    }

    private static HashMap<String, Integer> sTotalHashMap;
    private static HashMap<String, Integer> sHashMap;

    private static void processFile(File file, StringBuilder strBuilder) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);

        String token;

        while (scanner.hasNext()) {
            token = scanner.next();
            sHashMap.put(token, sHashMap.get(token) + 1);
            sTotalHashMap.put(token, sTotalHashMap.get(token) + 1);
        }

        strBuilder.append(file.getName()).append("\n");

        for (String s : TOKENS) {
            strBuilder.append(String.format(LINE_PATTERN, s, sHashMap.get(s)));
        }
        strBuilder.append("\n\n");
    }

    private static void processFolder(File folder, StringBuilder strBuilder) throws FileNotFoundException {
        File[] files = folder.listFiles();
        Arrays.sort(files);

        for (File file : files) {
            clearHash();
            processFile(file, strBuilder);
        }
    }

    private static void clearHash() {
        sHashMap = new HashMap<String, Integer>();
        for (String s : TOKENS) {
            sHashMap.put(s, 0);
        }
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        sTotalHashMap = new HashMap<String, Integer>();
        for (String s : TOKENS) {
            sTotalHashMap.put(s, 0);
        }

        String path = "/home/isaac/Unifei/Mestrado/SAS/Statistics/MethodsData2/";

        StringBuilder strBuilder = new StringBuilder();

        strBuilder.append("HAM").append("\n\n");

        processFolder(new File(path, "ham"), strBuilder);

        // count for HAM
        strBuilder.append("\n\n>> Count HAM").append("\n\n");
        Integer[] hams = new Integer[TOKENS.length];
        int i=0;

        for (String s : TOKENS) {
            hams[i++] = sTotalHashMap.get(s);
            strBuilder.append(String.format(LINE_PATTERN, s, sTotalHashMap.get(s)));
        }

        // result
        strBuilder.append("\n\n").append("SPAM").append("\n\n");
        processFolder(new File(path, "spam"), strBuilder);

        // count for SPAM
        strBuilder.append("\n\n>> Count SPAM").append("\n\n");
        i=0;

        for (String s : TOKENS) {
            strBuilder.append(String.format(LINE_PATTERN, s, sTotalHashMap.get(s) - hams[i++]));
        }

        // total
        strBuilder.append("TOTAL\n\n");
        for (String s : TOKENS) {
            strBuilder.append(String.format(LINE_PATTERN, s, sTotalHashMap.get(s)));
        }

        // save
        FileOutputStream fileOut = new FileOutputStream(new File(path, "estatisticas2"));
        fileOut.write(strBuilder.toString().getBytes());
        fileOut.close();

    }

}

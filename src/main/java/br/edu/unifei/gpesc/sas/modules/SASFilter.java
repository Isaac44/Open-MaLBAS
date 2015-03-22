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
package br.edu.unifei.gpesc.sas.modules;

import br.edu.unifei.gpesc.sas.filter.FilterExecutor;
import br.edu.unifei.gpesc.util.FileUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

/**
 *
 * @author isaac
 */
public class SASFilter {

    private final MailProcessor mMailProcessor = new MailProcessor();
    private final FilterExecutor mFilterExecutor = new FilterExecutor();

    public String filterFile(String inputPath) {
        boolean processed = mMailProcessor.processMail(inputPath);

        String out = null;

        if (processed) {
            String content = mMailProcessor.getContent();
            if (mMailProcessor.isText()) {
                StringBuilder strBuilder = new StringBuilder();
                mFilterExecutor.filterText(content, strBuilder);
                out = strBuilder.toString();
            }
            else if (mMailProcessor.isHtml()) {
                Elements allElements = Jsoup.parse(content).getAllElements();
                out = mFilterExecutor.filterHtml(allElements);
            }
        }

        return out;
    }

    public void filterFolder(File folderIn, File folderOut) {
        File[] files = folderIn.listFiles(FileUtils.getFileFilter());
        if (files != null) {
            String result;
            for (File file : files) {
                result = filterFile(file.getAbsolutePath());
                saveTo(result, new File(folderOut, file.getName()));
            }
        }
    }

    public void saveTo(String text, File file) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            // TESTES APENAS!!!
            writer.append("\n");
            // FIM


            writer.append(text);
            writer.close();
        } catch (IOException ex) {
            System.out.println("IOException (write): " + file.getName());
        }
    }

}

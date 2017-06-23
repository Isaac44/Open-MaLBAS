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
package br.edu.unifei.gpesc.core.modules;

import br.edu.unifei.gpesc.core.filter.FilterExecutor;
import br.edu.unifei.gpesc.core.filter.FilterOutput;
import br.edu.unifei.gpesc.core.filter.StringBuilderOutput;
import br.edu.unifei.gpesc.core.filter.WriterOutput;
import br.edu.unifei.gpesc.util.ConsoleProgress;
import br.edu.unifei.gpesc.util.FileUtils;
import br.edu.unifei.gpesc.util.VectorCounter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

/**
 *
 * @author isaac
 */
public class Filter {

    private final Mail mMailProcessor = new Mail();
    private final FilterExecutor mFilterExecutor = new FilterExecutor();

    private final VectorCounter mFolderProcessLog = new VectorCounter();

    private String filter() {
        StringBuilderOutput output = new StringBuilderOutput();
        filter(output);
        return output.toString();
    }

    private void filter(FilterOutput output) {
        String content = mMailProcessor.getContent();

        if (mMailProcessor.isText()) {
            mFilterExecutor.filterText(content, output);
        }
        else if (mMailProcessor.isHtml()) {
            Elements allElements = Jsoup.parse(content).getAllElements();
            mFilterExecutor.filterHtml(allElements, output);
        }

    }

    public String filterMail(InputStream inputStream) {
        boolean processed = mMailProcessor.processMail(inputStream);
        return (processed) ? filter() : null;
    }

    public String filterMail(String inputPath) {
        boolean processed = mMailProcessor.processMail(inputPath);
        return (processed) ? filter() : null;
    }

    public void filterFolder(File folderIn, File folderOut) {
        System.out.println("USANDO O FILTRO DIRETO");
        mFolderProcessLog.resetCounters();

        File[] files = folderIn.listFiles(new FileUtils.IsFileFilter());
        if (files != null) {
            ConsoleProgress progress = ConsoleProgress.getGlobalInstance(files.length);

            int k = 0;

            for (File file : files) {
                progress.setValue(k++);

                // Process and save at the same time
                try {
                    File fileOut = new File(folderOut, file.getName());

                    if (mMailProcessor.processMail(file.getAbsolutePath())) {
                        WriterOutput output = new WriterOutput(new BufferedWriter(new FileWriter(fileOut)));
                        filter(output);
                        output.close();
                        mFolderProcessLog.incGoodVectorsCount();
                    }
                    else {
                        mFolderProcessLog.incZeroedVectorsCount();
                    }
                }
                catch (IOException e) {
                    System.out.println("IOException (write): " + file.getName());
                }
            }

            progress.end();
        }
    }

    public VectorCounter getFolderProcessLog() {
        return mFolderProcessLog;
    }

    public void saveTo(String text, File file) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.append(text);
            writer.close();
        } catch (IOException ex) {
            System.out.println("IOException (write): " + file.getName());
        }
    }

}

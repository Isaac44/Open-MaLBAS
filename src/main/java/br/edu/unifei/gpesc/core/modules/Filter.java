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

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import br.edu.unifei.gpesc.core.filter.FilterExecutor;
import br.edu.unifei.gpesc.core.filter.OccurrencesMap;
import br.edu.unifei.gpesc.evaluation.TimeMark;
import br.edu.unifei.gpesc.util.ConsoleProgress;
import br.edu.unifei.gpesc.util.FileUtils;
import br.edu.unifei.gpesc.util.TraceLog;
import br.edu.unifei.gpesc.util.VectorCounter;

/**
 *
 * @author isaac
 */
public class Filter {

    /**
     * The JavaMail interface which is responsible for extracting the content of the e-mail to be
     * processed by the filters.
     */
    private final Mail mMailProcessor = new Mail();

    /**
     * All the filters to be applied on the e-mail.
     */
    private final FilterExecutor mFilterExecutor = new FilterExecutor();

    private final OccurrencesMap mOccurrencesMap = new OccurrencesMap(1024 * 10);

    /**
     * A statistics log.
     */
    private final VectorCounter mFolderProcessLog = new VectorCounter();

    public OccurrencesMap getOccurrencesMap() {
        return mOccurrencesMap;
    }

    private void doFilter() {
        mOccurrencesMap.clear(); // keep the structure for optimization

        String content = mMailProcessor.getContent();

        if (mMailProcessor.isText()) {
            mFilterExecutor.filterText(content, mOccurrencesMap);
            TimeMark.mark("4.T. Filtragem de Texto");
        }
        else if (mMailProcessor.isHtml()) {
            Elements allElements = Jsoup.parse(content).getAllElements();
            mFilterExecutor.filterHtml(allElements, mOccurrencesMap);
            TimeMark.mark("4.H. Filtragem de HTML");
        } else {
            TimeMark.mark("4.N. Filtragem não usada");
        }
    }

    public boolean filterMail(InputStream inputStream) {
        boolean processed = mMailProcessor.processMail(inputStream);
        TimeMark.mark("3. JavaMail");

        if (processed) {
            doFilter();
        }

        return processed;
    }

    public boolean filterMail(File file) {
        boolean processed = mMailProcessor.processMail(file);

        if (processed) {
            doFilter();
        }

        return processed;
    }

    public void filterFolder(File folderIn, File folderOut) {
        TimeMark.init("TimeMark_filter.log");

        mFolderProcessLog.resetCounters();

        File[] files = folderIn.listFiles(new FileUtils.IsFileFilter());
        if (files != null) {
            ConsoleProgress progress = ConsoleProgress.getGlobalInstance(files.length);

            int k = 0;

            for (File file : files) {
                TimeMark.start();
                progress.setValue(k++);

                try {
                    File fileOut = new File(folderOut, file.getName());

                    if (filterMail(file)) {
                        mOccurrencesMap.toFile(fileOut);
                        mFolderProcessLog.incGoodVectorsCount();
                    }
                    else {
                        mFolderProcessLog.incZeroedVectorsCount();
                    }
                }
                catch (Exception e) {
                    TraceLog.logE(e);
                    System.out.println("Exception: " + file.getName());
                }
                
                TimeMark.finish((int) file.length());
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

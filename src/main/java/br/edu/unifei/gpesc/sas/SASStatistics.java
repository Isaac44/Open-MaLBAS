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
package br.edu.unifei.gpesc.sas;

import br.edu.unifei.gpesc.base.statistic.FileStatistics;
import br.edu.unifei.gpesc.util.FileUtils;
import java.io.File;

/**
 *
 * @author isaac
 */
public class SASStatistics {

    /**
     * HAM type.
     */
    public static final int HAM_SET = 0;

    /**
     * SPAM type.
     */
    public static final int SPAM_SET = 1;


    /**
     * The statistics for the 2 sets: ham and spam.
     */
    private final FileStatistics mFileStatistic = new FileStatistics(2);

    /**
     * This method process all files in the folder, incrementing the
     * statistical data for the specified set.
     * @param folder The folder to process.
     * @param set The set. Use {@link SASStatistics#HAM_SET} or {@link SASStatistics#SPAM_SET}
     */
    public void processFolder(File folder, int set) {
        for (File file : folder.listFiles(FileUtils.getFileFilter())) {
            mFileStatistic.processFile(file, set);
        }
    }

    /**
     * This method is a convenient way to use {@link SASStatistics#processFolder(java.io.File, int)}
     *  for the both sets.
     * @param hamFolder The ham folder.
     * @param spamFolder The spam folder.
     */
    public void processSpamAndHam(File hamFolder, File spamFolder) {
        processFolder(hamFolder, HAM_SET);
        processFolder(spamFolder, SPAM_SET);
    }
}

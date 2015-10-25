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
package br.edu.unifei.gpesc.app;

import static br.edu.unifei.gpesc.app.Messages.*;
import br.edu.unifei.gpesc.core.modules.Filter;
import br.edu.unifei.gpesc.core.modules.Statistics;
import static br.edu.unifei.gpesc.core.modules.Vector.doVectorization;
import br.edu.unifei.gpesc.core.statistic.Census;
import br.edu.unifei.gpesc.core.statistic.ChiSquared;
import br.edu.unifei.gpesc.core.statistic.MutualInformation;
import br.edu.unifei.gpesc.core.statistic.Characteristics;
import br.edu.unifei.gpesc.core.statistic.Data;
import br.edu.unifei.gpesc.core.statistic.Distribution;
import br.edu.unifei.gpesc.core.statistic.Frequency;
import br.edu.unifei.gpesc.util.ProcessLog;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * The train mode module. This is a main class type. <br>
 * It contains all "do" methods for the execution of this Anti-Spam System.
 *
 * @author Isaac Caldas Ferreira
 */
public class TrainModule {

    /**
     * The AntiSpam filter. <b>TODO: add filter selection. With this, the user
     * can select which filter will be used by the application. </b>
     */
    private Filter mFilter;

    /**
     * The AntiSpam Statistics.
     */
    private Statistics mStatistics;

    /**
     * Asserts if an file or folder exists.
     * @param file The file or folder to be tested.
     * @throws IllegalArgumentException if the file or folder does not exists.
     */
    private void assertExists(File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException(i18n("TrainMode.IllegalArgument.FileNotExists", file.getAbsolutePath()));
        }
    }

    /**
     * Asserts if this file argument exists and is a folder.
     * @param file The folder to be tested.
     * @throws IllegalArgumentException if the folder does not exists or its
     * not a folder.
     */
    private void assertDirectory(File folder) {
        assertExists(folder);
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException(i18n("TrainMode.IllegalArgument.FileIsNotDirectory", folder.getAbsolutePath()));
        }
    }

    /**
     * Creats all folders. This mean that the folder denoted by the argument
     * folder is created and all its parents.
     * @param folder The folder to be created (and all its parents if they don't
     * exists).
     * @throws IllegalArgumentException if an error occurs during the creation
     * of the folders. Important note, some folders can be created. See
     * {@link File#mkdirs()}.
     */
    private void createDirs(File folder) {
        if (!folder.exists()) {
            if (!folder.mkdirs()) throw new IllegalArgumentException(i18n("TrainMode.IllegalArgument.CannotCreateDirectory", folder.getAbsolutePath()));
        }
    }

    /**
     * This do-method process a folder denoted by inPath argument and
     * stores on the folder denoted by outPath the filtered files.
     * <p> All files processed must contain the extension ".eml".
     * @param inPath The input folder path.
     * @param outPath The output folder path.
     * @throws IllegalArgumentException if the input folder does not exists
     * or if the output path cannot be created or this application doesn't
     * have permission to write in.
     */
    public void doFilter(String inPath, String outPath) {
        if (mFilter == null) mFilter = new Filter();

        File inFolder = new File(inPath);
        assertDirectory(inFolder); // the first arg must be a directory

        File outFolder = new File(outPath);
        createDirs(outFolder); // if the second arg doesn't exists, create it.

        // all ok. Process.
        printlnLog("TrainMode.ProcessStart", inPath);
        printlnLog("TrainMode.Processing");
        mFilter.filterFolder(inFolder, outFolder);

        // log process.
        ProcessLog log = mFilter.getFolderProcessLog();
        printlnLog("TrainMode.Filter.ProcessResult", log.total(), log.sucess(), log.error());

    }

    /**
     * Gets the {@link Distribution} for the input string.
     * <li>MI: {@link MutualInformation}. </li>
     * @param method The method name, as shown above.
     * @return The Statistical Distribution selected.
     * @throws IllegalArgumentException if the method argument is invalid.
     */
    private Distribution getDistribution(String method) {
        if (method.contains("MI")) {
            return new MutualInformation();
        }
        else if (method.contains("CHI2")) {
            return new ChiSquared();
        }
        else if (method.contains("FD")) {
            return new Frequency();
        }
        else {
            throw new IllegalArgumentException(i18n("TrainMode.Statistics.IllegalArgument.InvalidMethod", method));
        }
    }

    /**
     * This do-method process all filtered email files in the not spam folder
     * (denoted by hamPath) and the spam folder (denoted by spamPath) and
     * creates the statistcs for all text words finded.
     * <br>
     * After the statistics is generated is calculated the statistical
     * distribution (denoted by the method argument), then this last is save
     * at the statistics folder (denoted by statisticsPath).
     *
     * @param hamPath The not spam input folder path.
     * @param spamPath The spam input folder path.
     * @param outFile The statistics output file path.
     * @param method The statistical distribution method.
     * See {@link TrainMode#getStatisticalDistribution(String)}.
     * @throws IOException if any input/output error occurs.
     * @throws IllegalArgumentException if any input folder does not exists
     * or the statistics file cannot be created or the selected method is
     * invalid.
     */
    public void doStatistics(String hamPath, String spamPath,
            String outFile, String method) throws IOException
    {
        if (mStatistics == null) mStatistics = new Statistics();

        File hamFolder = new File(hamPath);
        assertDirectory(hamFolder);

        File spamFolder = new File(spamPath);
        assertDirectory(spamFolder);

        File statisticsFile = new File(outFile);
        createDirs(statisticsFile.getParentFile());

        // statistical distribution
        Distribution distribution = getDistribution(method);

        // generate statistics
        printlnLog("TrainMode.ProcessStart", hamPath);
        mStatistics.processFolder(hamFolder, Statistics.HAM_SET);

        printlnLog("TrainMode.ProcessStart", spamPath);
        mStatistics.processFolder(spamFolder, Statistics.SPAM_SET);

        // compute distribution
        printlnLog("TrainMode.Statistics.ComputingDistribution");
        Census<String> census = new Census<String>(mStatistics.getStatistics());
        census.computeDistribution(distribution);
        census.sortData(new Census.DecrescentDistributionSort());

        // store statistical file
        printlnLog("TrainMode.Statistics.SavingStatisticsFile", statisticsFile.getAbsolutePath());
        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(statisticsFile));
        fileWriter.write(method + "\n");

        for (Data<String> data : census.getStatisticalDataList()) {
            fileWriter
                    .append(data.getElement())
                    .append("\t")
                    .append(Double.toString(data.getDistribution()))
                    .append("\n");
        }
        fileWriter.close();
    }

    /**
     * This do-method generated de MLP vectors. Its processes the filtered
     * emails in the not spam folder (denoted by hamPath) and in the spam folder
     * (denoted by spamPath), using the statistics file (denoted by
     * statisticsPath).
     * <br>
     * The MLP vectors will have the length denoted by inputLayerLength argument
     * and will be stored inside the output path (denoted by outPath).
     * <br>
     * The file "ham" will be created for the not spam vectors and the file
     * "spam" for the spam vectors.
     * @param hamPath The not spam input folder path.
     * @param spamPath the spam input folder path.
     * @param statisticsPath The statistics file path.
     * @param inLayerLen The length of the MLP input layer.
     * @param outPath The output path.
     * @throws IOException if any input/output error occurs.
     * @throws IllegalArgumentException if any input folder/file does not exists
     * or if the "ham" or "spam" files cannot be created.
     */
    public void doMlpVector(String hamPath, String spamPath, String statisticsPath, int inLayerLen, String outPath) throws IOException {
        File hamFolder = new File(hamPath);
        assertDirectory(hamFolder);

        File spamFolder = new File(spamPath);
        assertDirectory(spamFolder);

        File statisticsFile = new File(statisticsPath);
        assertExists(statisticsFile);

        File outHamFile = new File(outPath, "ham");
        createDirs(outHamFile.getParentFile());

        File outSpamFile = new File(outPath, "spam");
        createDirs(outSpamFile.getParentFile());

        // open statistics file
        Characteristics<String> characteristic = new Characteristics<String>();

        Scanner scanner = new Scanner(statisticsFile);
        scanner.nextLine(); // ignore the method
        for (int i=0; i<inLayerLen; i++) {
            if (scanner.hasNext()) {
                characteristic.insertData(scanner.next());
                scanner.nextLine();
            } else {
                throw new IllegalArgumentException(i18n("TrainMode.IllegalArgument.StatisticsDoesNotContainInputLayerLength", inLayerLen));
            }
        }

        printlnLog("TrainMode.ProcessStart", hamPath);
        ProcessLog hamLog = doVectorization(characteristic, hamFolder, outHamFile);

        printlnLog("TrainMode.ProcessStart", spamPath);
        ProcessLog spamLog = doVectorization(characteristic, spamFolder, outSpamFile);

        // logs

        println("");
        printLog("Application.NotSpam"); println(": ");
        print("\t"); printlnLog("TrainMode.MlpVector.AddedPatterns", hamLog.sucess());
        print("\t"); printlnLog("TrainMode.MlpVector.ZeroedFiles", hamLog.error());

        printLog("Application.Spam"); println(": ");
        print("\t"); printlnLog("TrainMode.MlpVector.AddedPatterns", spamLog.sucess());
        print("\t"); printlnLog("TrainMode.MlpVector.ZeroedFiles", spamLog.error());
    }
}

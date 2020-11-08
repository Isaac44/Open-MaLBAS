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
import static br.edu.unifei.gpesc.core.modules.VectorHelper.doVectorization;
import static br.edu.unifei.gpesc.core.modules.VectorHelper.simulateVectorization;
import br.edu.unifei.gpesc.core.statistic.*;
import br.edu.unifei.gpesc.util.VectorCounter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
     * Creates all folders. This mean that the folder denoted by the argument
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
        VectorCounter log = mFilter.getFolderProcessLog();
        printlnLog("TrainMode.Filter.ProcessResult", log.getTotalVectorsCount(), log.getGoodVectorsCount(), log.getZeroedVectorsCount());

    }

    public static void main(String[] args) {
        TrainModule tm = new TrainModule();
        tm.doFilter("/home/isaac/Unifei/Mestrado/Dissertacao/Slides/Mestrado/Slides/mail/files", "/home/isaac/Unifei/Mestrado/Dissertacao/Slides/Mestrado/Slides/mail/");
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

        Characteristics<String> characteristics = CharacteristicsHelper.fromFile(statisticsFile, inLayerLen);
        if (characteristics == null) {
            throw new IllegalArgumentException(i18n("TrainMode.IllegalArgument.StatisticsDoesNotContainInputLayerLength", inLayerLen));
        }

        printlnLog("TrainMode.ProcessStart", hamPath);
        VectorCounter hamR = doVectorization(characteristics, hamFolder, outHamFile);

        printlnLog("TrainMode.ProcessStart", spamPath);
        VectorCounter spamR = doVectorization(characteristics, spamFolder, outSpamFile);

        // logs
        println("");
        printLog("Application.NotSpam"); println(": ");
        print("\t"); printlnLog("TrainMode.MlpVector.AddedPatterns", hamR.getGoodVectorsCount());
        print("\t"); printlnLog("TrainMode.MlpVector.ZeroedFiles", hamR.getZeroedVectorsCount());

        printLog("Application.Spam"); println(": ");
        print("\t"); printlnLog("TrainMode.MlpVector.AddedPatterns", spamR.getGoodVectorsCount());
        print("\t"); printlnLog("TrainMode.MlpVector.ZeroedFiles", spamR.getZeroedVectorsCount());
    }

    // ---------------------------------------------------------------------------------------------
    // Vectores with Percent
    // ---------------------------------------------------------------------------------------------

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
     * @throws IOException if any input/output error occurs.
     * @throws IllegalArgumentException if any input folder/file does not exists
     * or if the "ham" or "spam" files cannot be created.
     *
     * @return The percentage of zeroed vetors
     */
    private float simulatePercent(String hamPath, String spamPath, String statisticsPath, int inLayerLen) throws IOException {
        File hamFolder = new File(hamPath);
        assertDirectory(hamFolder);

        File spamFolder = new File(spamPath);
        assertDirectory(spamFolder);

        File statisticsFile = new File(statisticsPath);
        assertExists(statisticsFile);

        Characteristics<String> characteristics = CharacteristicsHelper.fromFile(statisticsFile, inLayerLen);
        if (characteristics == null) {
            throw new IllegalArgumentException(i18n("TrainMode.IllegalArgument.StatisticsDoesNotContainInputLayerLength", inLayerLen));
        }

        //printlnLog("TrainMode.ProcessStart", hamPath);
        VectorCounter hamR = simulateVectorization(characteristics, hamFolder);

        //printlnLog("TrainMode.ProcessStart", spamPath);
        VectorCounter spamR = simulateVectorization(characteristics, spamFolder);

        return computeZeroedPercent(hamR, spamR);
    }

    private float computeZeroedPercent(VectorCounter hamR, VectorCounter spamR) {
        int zeroed = hamR.getZeroedVectorsCount() + spamR.getZeroedVectorsCount();
        int total = hamR.getTotalVectorsCount() + spamR.getTotalVectorsCount();
        return zeroed / (float) total;
    }

    public void createVectorsByPercent(String hamPath, String spamPath, String statisticsPath, float zerosPercent, String outPath) throws IOException {
        int minInput = 1;
        int maxInput = 100;
        float zeros;

        // find the max number of features
        while (true) {
            System.out.println("minInput = " + minInput + " maxInput = " + maxInput);

            zeros = simulatePercent(hamPath, spamPath, statisticsPath, maxInput);

            System.out.println("zeros = " + (zeros*100) + " | target: " + (zerosPercent*100));


            // if less or equal, the maxInput was fouded (less zeroed vectors)
            if (zeros <= zerosPercent) {
                break;
            }
            // otherwise, try with a larger number
            else {
                minInput = maxInput;
                maxInput *= 10;
            }

            System.out.println();
        }

        System.out.println();
        System.out.println("ETAPA 2");

        // find the best match
        int middle = 1;
        int oldMiddle;
        while (true) {
            System.out.println("\n-----------------------------------------------------------------\n");

            System.out.println("min = " + minInput + ", max = " + maxInput);

            oldMiddle = middle;
            middle = Math.round((maxInput + minInput) / 2f); // div 2

            if (oldMiddle == middle) {
                break;
            }


            zeros = simulatePercent(hamPath, spamPath, statisticsPath, middle);
            System.out.println("middle = " + middle + " | zeros = " + (zeros*100) + " | target: " + (zerosPercent*100));

            // less zeroed vectors -> decrease the number of features
            if (zeros < zerosPercent) {
                maxInput = middle;
            }

            // more zeroed vectors -> increase the number of features
            else if (zeros > zerosPercent) {
                minInput = middle;
            }
        }

        // Vectorization
        System.out.println("middle = " + middle);
        doMlpVector(hamPath, spamPath, statisticsPath, middle, outPath);

    }

    // ---------------------------------------------------------------------------------------------
    // Vectors with Zeros Count
    // ---------------------------------------------------------------------------------------------

    private float simulateAverage(String hamPath, String spamPath, String statisticsPath, int inLayerLen) throws IOException {
        File hamFolder = new File(hamPath);
        assertDirectory(hamFolder);

        File spamFolder = new File(spamPath);
        assertDirectory(spamFolder);

        File statisticsFile = new File(statisticsPath);
        assertExists(statisticsFile);

        // open statistics file
        Characteristics<String> characteristics = CharacteristicsHelper.fromFile(statisticsFile, inLayerLen);
        if (characteristics == null) {
            return 0;
        }

        //printlnLog("TrainMode.ProcessStart", hamPath);
        VectorCounter hamR = simulateVectorization(characteristics, hamFolder);

        //printlnLog("TrainMode.ProcessStart", spamPath);
        VectorCounter spamR = simulateVectorization(characteristics, spamFolder);

        return computeZerosAverage(hamR, spamR);
    }

    private float computeZerosAverage(VectorCounter hamR, VectorCounter spamR) {
        int zerosCount = hamR.getZerosCount() + spamR.getZerosCount();
        int totalVectors = hamR.getTotalVectorsCount() + spamR.getTotalVectorsCount();
        return zerosCount / (float) totalVectors;
    }

    public void createVectorsByAverage(String hamPath, String spamPath, String statisticsPath, float average, String outPath) throws IOException {
        float avg = simulateAverage(hamPath, spamPath, statisticsPath, (int) average);
        System.out.println("for " + average + " the average is " + avg);
    }
}

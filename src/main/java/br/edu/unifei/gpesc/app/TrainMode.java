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

import static br.edu.unifei.gpesc.app.Messages.log;
import static br.edu.unifei.gpesc.app.Messages.printlnLog;
import br.edu.unifei.gpesc.sas.modules.SASFilter;
import br.edu.unifei.gpesc.sas.modules.SASStatistics;
import static br.edu.unifei.gpesc.sas.modules.SASVectorization.doVectorization;
import br.edu.unifei.gpesc.statistic.Census;
import br.edu.unifei.gpesc.statistic.MutualInformationDistribution;
import br.edu.unifei.gpesc.statistic.StatisticalCharacteristic;
import br.edu.unifei.gpesc.statistic.StatisticalData;
import br.edu.unifei.gpesc.statistic.StatisticalDistribution;
import br.edu.unifei.gpesc.util.ProcessLog;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author isaac
 */
public class TrainMode {

    private SASFilter mFilter;
    private SASStatistics mStatistics;

    public void doFilter(String inPath, String outPath) {
        if (mFilter == null) mFilter = new SASFilter();

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

    private void assertExists(File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException(log("TrainMode.IllegalArgument.FileNotExists", file.getAbsolutePath()));
        }
    }

    private void assertDirectory(File folder) {
        assertExists(folder);
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException(log("TrainMode.IllegalArgument.FileIsNotDirectory", folder.getAbsolutePath()));
        }
    }

    private void createDirs(File folder) {
        if (!folder.exists()) {
            if (!folder.mkdirs()) throw new IllegalArgumentException(log("TrainMode.IllegalArgument.CannotCreateDirectory", folder.getAbsolutePath()));
        }
    }

    private StatisticalDistribution getStatisticalDistribution(String method) {
        if (method.equals("MI")) return new MutualInformationDistribution();
        throw new IllegalArgumentException(log("TrainMode.Statistics.IllegalArgument.InvalidMethod", method));
    }

    public void doStatistics(String hamPath, String spamPath, String statisticsPath, String method) throws IOException {
        if (mStatistics == null) mStatistics = new SASStatistics();

        File hamFolder = new File(hamPath);
        assertDirectory(hamFolder);

        File spamFolder = new File(spamPath);
        assertDirectory(spamFolder);

        File statisticsFile = new File(statisticsPath, "statistics");

        // statistical distribution
        StatisticalDistribution distribution = getStatisticalDistribution(method);

        // generate statistics
        printlnLog("TrainMode.ProcessStart", hamPath);
        mStatistics.processFolder(hamFolder, SASStatistics.HAM_SET);

        printlnLog("TrainMode.ProcessStart", spamPath);
        mStatistics.processFolder(spamFolder, SASStatistics.SPAM_SET);

        // compute distribution
        printlnLog("TrainMode.Statistics.ComputingDistribution");
        Census<String> census = new Census<String>(mStatistics.getStatistics());
        census.computeDistribution(distribution);
        census.sortData(new Census.DecrescentDistributionSort());

        // store statistical file
        printlnLog("TrainMode.Statistics.SavingStatisticsFile", statisticsFile.getAbsolutePath());
        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(statisticsFile));
        fileWriter.write(method + "\n");
        for (StatisticalData<String> data : census.getStatisticalDataList()) {
            fileWriter.append(data.getElement()).append("\t").append(Double.toString(data.getStatisticalDistribution())).append("\n");
        }
        fileWriter.close();
    }

    public void doMlpVector(String hamPath, String spamPath, String statisticsPath, int inputLayerLength, String outPath) throws IOException {
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
        StatisticalCharacteristic<String> characteristic = new StatisticalCharacteristic<String>();

        Scanner scanner = new Scanner(statisticsFile);
        scanner.nextLine(); // ignore the method
        for (int i=0; i<inputLayerLength; i++) {
            if (scanner.hasNext()) {
                characteristic.insertData(scanner.next());
                scanner.nextLine();
            } else {
                throw new IllegalArgumentException(log("TrainMode.IllegalArgument.StatisticsDoesNotContainInputLayerLength", inputLayerLength));
            }
        }

        ProcessLog log;
        log = doVectorization(characteristic, hamFolder, outHamFile);
        System.out.println("error="+log.error() + " sucess="+log.sucess());
        log = doVectorization(characteristic, spamFolder, outSpamFile);
        System.out.println("error="+log.error() + " sucess="+log.sucess());
    }
}

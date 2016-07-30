/*
 * Copyright (C) 2015 GPESC - Universidade Federal de Itajuba
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

import br.edu.unifei.gpesc.core.neural.NeuralModule;
import br.edu.unifei.gpesc.core.neural.TestBuilder;
import static br.edu.unifei.gpesc.app.Messages.*;
import br.edu.unifei.gpesc.app.sas.Client;
import br.edu.unifei.gpesc.app.sas.Server;
import br.edu.unifei.gpesc.util.Configuration;
import br.edu.unifei.gpesc.util.TraceLog;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;


/**
 *
 * @author Isaac Caldas Ferreira
 */
public class Application {

    private static final String ARG_SERVER = "server";
    private static final String ARG_CLIENT = "client";

    private static final String ARG_FILTER = "filter";
    private static final String ARG_STATISTICS = "statistics";
    private static final String ARG_MLP_VECTOR = "vector";
    private static final String ARG_MLP_NEURAL = "mlp";

    private static Configuration sMainConfig;
    private static Configuration sNeuralConfig;
    private static TrainModule sTrainModule;
    private static NeuralModule sNeuralModule;

    private static void runFilter() {
        Configuration c = getMainConfig();

        String hamInPath = c.getProperty("RAW_NOT_SPAM_FOLDER");
        String hamOutPath = c.getProperty("PROCESSED_NOT_SPAM_FOLDER");
        String spamInPath = c.getProperty("RAW_SPAM_FOLDER");
        String spamOutPath = c.getProperty("PROCESSED_SPAM_FOLDER");

        TrainModule tm = getTrainModule();
        tm.doFilter(hamInPath, hamOutPath);
        tm.doFilter(spamInPath, spamOutPath);
    }

    private static void runStatistics() throws IOException {
        Configuration c = getMainConfig();

        String spamPath = c.getProperty("PROCESSED_SPAM_FOLDER");
        String hamPath = c.getProperty("PROCESSED_NOT_SPAM_FOLDER");
        String statisticsFile = c.getProperty("STATISTICS_FILE");
        String method = c.getProperty("STATISTICS_METHOD");

        getTrainModule().doStatistics(hamPath, spamPath, statisticsFile, method);
    }

    private static void runVector() throws IOException {
        Configuration c = getMainConfig();

        String spamPath = c.getProperty("PROCESSED_SPAM_FOLDER");
        String hamPath = c.getProperty("PROCESSED_NOT_SPAM_FOLDER");
        String statisticsFile = c.getProperty("STATISTICS_FILE");
        Integer length = c.getIntegerProperty("VECTOR_LENGTH");
        String outFolder = c.getProperty("VECTOR_OUT_FOLDER");

        getTrainModule().doMlpVector(hamPath, spamPath, statisticsFile, length, outFolder);
    }

    private static void setupLogger() {
        // Log on File
        String logFolder = sMainConfig.getProperty("LOG_FOLDER", null);

        if (logFolder != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss_zzz");
            File file = new File(logFolder, "SAS_" + sdf.format(System.currentTimeMillis()) + ".log");
            file.getParentFile().mkdirs();

            TraceLog.setLogFile(file.getAbsolutePath());
        }

        // Log on Console
        boolean logConsole = sMainConfig.getBooleanProperty("LOG_CONSOLE", false);
        TraceLog.setConsoleLogEnable(logConsole);
    }

    private static Configuration getMainConfig() {
        if (sMainConfig == null) {
            try {
                sMainConfig = new Configuration("config" + File.separator + "main.properties");
                setupLogger();
            } catch (IOException ex) {
                ex.printStackTrace(System.err);
                System.exit(1);
            }
        }
        return sMainConfig;
    }

    private static TrainModule getTrainModule() {
        if (sTrainModule == null) {
            sTrainModule = new TrainModule();
        }
        return sTrainModule;
    }

    private static void runMlp() throws IOException {
        if (sNeuralModule != null) {
            sNeuralModule.stopProcess();
        } else {
            sNeuralModule = new NeuralModule("config" + File.separator + "neural.properties");
        }

        sNeuralModule.setUp();
        sNeuralModule.start();
    }

    private static void process(String module) throws IOException {
        if (module.equals(ARG_FILTER))
        {
            printlnLog("Application.TrainMode.SelectedFilter");
            runFilter();
        }

        else if (module.equals(ARG_STATISTICS))
        {
            printlnLog("Application.TrainMode.SelectedStatistics");
            runStatistics();
        }

        else if (module.equals(ARG_MLP_VECTOR))
        {
            printlnLog("Application.TrainMode.SelectedMlpVector");
            runVector();
        }
        else if (module.equals(ARG_MLP_NEURAL))
        {
            printlnLog("Application.TrainMode.SelectedMlpNeural");
            runMlp();
        }

        else {
            System.out.println("SAS - Incorrect Use");
        }
    }

    public static void main(String... args) throws IOException {
        if (args.length == 0) {
            System.out.println("SAS - Incorrect use");
//            printlnLog("Application.TrainMode.All");
//            System.out.println();
//            process(ARG_FILTER);
//            process(ARG_STATISTICS);
//            process(ARG_MLP_VECTOR);
        }

        else {
            String module = args[0].toLowerCase();

            if ("run".equals(module)) {
                TestBuilder.main(args);
            }

            if (ARG_CLIENT.equals(module)) {
                System.exit(Client.run(args[1]));
            }
            else if (ARG_SERVER.equals(module)) {
                new Server().start();
            }
            else {
                process(module);
            }
        }
    }
}

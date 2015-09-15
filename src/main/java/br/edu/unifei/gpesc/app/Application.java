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
import br.edu.unifei.gpesc.app.sas.Client;
import br.edu.unifei.gpesc.app.sas.Server;
import java.io.IOException;


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

    private static Configuration sConfig;
    private static TrainModule sTrainModule;
    private static NeuralModule sNeuralModule;

    private static void runFilter() {
        String hamInPath = sConfig.getProperty("RAW_NOT_SPAM_FOLDER");
        String hamOutPath = sConfig.getProperty("PROCESSED_NOT_SPAM_FOLDER");
        String spamInPath = sConfig.getProperty("RAW_SPAM_FOLDER");
        String spamOutPath = sConfig.getProperty("PROCESSED_NOT_SPAM_FOLDER");

        sTrainModule.doFilter(hamInPath, hamOutPath);
        sTrainModule.doFilter(spamInPath, spamOutPath);
    }

    private static void runStatistics() throws IOException {
        String spamPath = sConfig.getProperty("PROCESSED_SPAM_FOLDER");
        String hamPath = sConfig.getProperty("PROCESSED_NOT_SPAM_FOLDER");
        String statisticsFile = sConfig.getProperty("STATISTICS_FILE");
        String method = sConfig.getProperty("STATISTICS_METHOD");

        sTrainModule.doStatistics(hamPath, spamPath, statisticsFile, method);
    }

    private static void runVector() throws IOException {
        String spamPath = sConfig.getProperty("PROCESSED_SPAM_FOLDER");
        String hamPath = sConfig.getProperty("PROCESSED_NOT_SPAM_FOLDER");
        String statisticsFile = sConfig.getProperty("STATISTICS_FILE");
        Integer length = sConfig.getIntegerProperty("VECTOR_LENGTH");
        String outFolder = sConfig.getProperty("VECTOR_OUT_FOLDER");

        sTrainModule.doMlpVector(hamPath, spamPath, statisticsFile, length, outFolder);
    }

    private static void runMlp() throws IOException {
        if (sNeuralModule != null) {
            sNeuralModule.stopProcess();
        } else {
            sNeuralModule = new NeuralModule();
        }

        sNeuralModule.load();
        sNeuralModule.start();
    }

    private static void process(String module) throws IOException {
        // init
        sConfig = Configuration.getInstance();
        sTrainModule = new TrainModule();

        // process
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
            printlnLog("Application.TrainMode.SelectedMlpVector");
            runMlp();
        }
    }

    public static void main(String... args) throws IOException {
        if (args.length == 0) {
            System.out.println("SAS - Incorret use");
//            printlnLog("Application.TrainMode.All");
//            System.out.println();
//            process(ARG_FILTER);
//            process(ARG_STATISTICS);
//            process(ARG_MLP_VECTOR);
        }

        else {
            String module = args[0].toLowerCase();

            if (ARG_CLIENT.equals(module)) {
                System.exit(Client.run());
            }
            else if (ARG_SERVER.equals(module)) {
                new Server().run();
            }
            else {
                process(module);
            }
        }
    }


//    public static void main(String[] args) throws IOException {
//        ProcessMailApp.main(args);
//    }

//    public static void main(String[] args) throws IOException {
//        main2(ARG_MLP_NEURAL);

//        main2(args);
//        main2("--filter", "/home/isaac/Unifei/Mestrado/SAS/Mail_Test/Febuary/base/ham", "/home/isaac/Unifei/Mestrado/SAS/Mail_Test/May/clean/ham");
//        main2("--filter", "/home/isaac/Unifei/Mestrado/SAS/Mail_Test/Febuary/base/spam", "/home/isaac/Unifei/Mestrado/SAS/Mail_Test/May/clean/spam");

//        String path = "/home/isaac/Unifei/Mestrado/SAS/Mail_Test/May/clean/";
//        main2("--statistics", "MI", path+"ham", path+"spam", path);

//        String path = "/home/isaac/Unifei/Mestrado/SAS/Mail_Test/May/clean/";
//        String pathVector = "/home/isaac/Unifei/Mestrado/SAS/Mail_Test/May/vector/";
//        main2("--mlp-vector", "2000", path+"ham", path+"spam", path+"statistics", pathVector);

//        String path = "/home/isaac/Unifei/Mestrado/SAS/Mail_Test/Febuary/clean/";
//        String pathVector = "/home/isaac/Unifei/Mestrado/SAS/Mail_Test/August/vector_chi2/";
//        main2("--mlp-vector", "2000", path+"ham", path+"spam", path+"statistics", pathVector);

//        genVectors();
//    }

//    private static void genVectors() throws IOException {
//        String[] quantities = {"500", "1000", "1500", "2000", "2500"};
//        String[] methods = {"mi", "chi2", "df"};
//
//        String cleanPath = "/home/isaac/Unifei/Mestrado/SAS/Mail_Test/Febuary/clean/";
//        String vectorPath = "/home/isaac/Unifei/Mestrado/SAS/Mail_Test/August/vectors/vector";
//        String statisticsPath = "/home/isaac/Unifei/Mestrado/SAS/Mail_Test/August/statistics/statistics";
//
//        for (String quantity : quantities) {
//            System.out.println("quantity = " + quantity);
//            for (String method : methods) {
//                System.out.println("method = " + method);
//                genVectors(quantity, cleanPath, statisticsPath, method, vectorPath);
//            }
//        }
//    }

//    private static void genVectors(String quantity, String cleanPath, String statisticsPath, String method, String vectorPath) throws IOException {
//        main2("--mlp-vector", quantity, cleanPath + "ham", cleanPath + "spam", statisticsPath + "_" + method, vectorPath + "_" + method + "_" + quantity);
//    }

}

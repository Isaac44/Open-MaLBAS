/*
 * Copyright (C) 2015 - GPESC - Universidade Federal de Itajuba
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
package br.edu.unifei.gpesc.app.neural;

import br.edu.unifei.gpesc.app.Configuration;
import br.edu.unifei.gpesc.app.Messages;
import br.edu.unifei.gpesc.core.mlp.Function;
import br.edu.unifei.gpesc.core.mlp.Linear;
import br.edu.unifei.gpesc.core.mlp.LogSig;
import br.edu.unifei.gpesc.core.mlp.Mlp;
import br.edu.unifei.gpesc.core.mlp.PatternLayer;
import br.edu.unifei.gpesc.core.mlp.TanSig;
import br.edu.unifei.gpesc.core.mlp.TrainMlp;
import br.edu.unifei.gpesc.debug.MailSender;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 *
 * @author Isaac Caldas Ferreira
 */
public class NeuralModule {

    /**
     * Threads to execute the trains.
     */
    private ExecutorService mTrainExecutor;

    /**
     * Thread to save the train log.
     */
    private final ExecutorService mSaveLogExecutor = Executors.newFixedThreadPool(1);

    /**
     * Train builder.
     */
    private TrainBuilder mTrainBuilder;

    /**
     * File configuration properties.
     */
    private final Configuration mCfg;

    /**
     * The output folder to store the result weights.
     */
    private String mWeightsFolder;

    /**
     * The first hidden layer length
     */
    private int mFirstHiddenLength;

    /**
     * The second hidden layer length
     */
    private int mSecondHiddenLength;

    /**
     * The number of epochs
     */
    private int mEpochs;

    /**
     * The initial momentum rate.
     */
    private double mMomentum;

    /**
     * The initial learn rate.
     */
    private double mLearnRate;

    /**
     * The default seed.
     */
    private long mSeed;

    private int mQuantityOfTrains;
    private int mNumberOfActiveThreads;

    private String mFirstHiddenFunction;
    private String mSecondHiddenFunction;
    private String mOutputFunction;

    private PatternLayer[] mTestPatterns;

    private Messages mMessages;

    public TrainExecutor() throws IOException {
        mCfg = new Configuration("neural.properties");
    }

    public void stopProcess() {
        if (mTrainExecutor != null) {
            mTrainExecutor.shutdownNow();
            mTrainExecutor = null;
        }
    }

    public void setUp() throws IOException {
        mQuantityOfTrains = mCfg.getIntegerProperty("NUMBER_OF_TRAINS");
        if (mQuantityOfTrains <= 0) {
            throw new IllegalArgumentException(mMessages.i18n("NeuralModule.IllegalArgument.NumberOfTrainsInferior"));
        }

        mWeightsFolder = mCfg.getProperty("WEIGHTS_FOLDER");
        mFirstHiddenLength = mCfg.getIntegerProperty("DEFAULT_FIRST_HIDDEN_LENGTH");
        mSecondHiddenLength = mCfg.getIntegerProperty("DEFAULT_SECOND_HIDDEN_LENGTH");
        mFirstHiddenFunction = mCfg.getProperty("DEFAULT_FIRST_HIDDEN_FUNCTION");
        mSecondHiddenFunction = mCfg.getProperty("DEFAULT_SECOND_HIDDEN_FUNCTION");
        mOutputFunction = mCfg.getProperty("DEFAULT_OUTPUT_FUNCTION");
        mEpochs = mCfg.getIntegerProperty("DEFAULT_EPOCHS");
        mMomentum = mCfg.getDoublePropertie("DEFAULT_MOMENTUM");
        mLearnRate = mCfg.getDoublePropertie("DEFAULT_LEARN_RATE");
        mSeed = getSeed(mCfg.getProperty("DEFAULT_RANDOMIZER_SEED"));
        mNumberOfActiveThreads = mCfg.getIntegerProperty("NUMBER_OF_ACTIVES_THREADS");

        String vectorFolder = mCfg.getProperty("VECTOR_FOLDER");
        Double validationPercent = mCfg.getDoublePropertie("VALIDATION_PERCENT");

        File hamFile = new File(vectorFolder, "ham");
        File spamFile = new File(vectorFolder, "spam");
        mTrainBuilder = new TrainBuilder(hamFile, spamFile, validationPercent);

        openTestPatterns(new File(vectorFolder));
    }

    private void openTestPatterns(File folder) {
        try {
            mTestPatterns = TestBuilder.loadLayers(folder);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void start() {
        stopProcess();
        mTrainExecutor = Executors.newFixedThreadPool(mNumberOfActiveThreads);

        for (int train=1; train <= mQuantityOfTrains; train++) {
            mTrainExecutor.execute(new TrainMlpRunnable(train));
        }
    }

    private Function functionForName(String name) {
        name = name.toUpperCase();
        if ("TANSIG".equals(name)) {
            return new TanSig();
        }
        else if ("LOGSIG".equals(name)) {
            return new LogSig();
        }
        else if ("LINEAR".equals(name)) {
            return new Linear();
        }
        else return null;
    }

    private int mSeedArray = 0;

    private long getSeed(String key, long defaultValue) {
//        if (key == null) {
//            return defaultValue;
//        }
//
//        key = key.toUpperCase();
//        final long seed;
//
//        if ("PRIME".equals(key)) {
//            switch (mSeedArray) {
//                case 1: seed = PrimeNumber.getRandomPrimeNumber2(); break;
//                case 2: seed = PrimeNumber.getRandomPrimeNumber3(); break;
//                default: seed = PrimeNumber.getRandomPrimeNumber(); break;
//            }
//            mSeedArray = (mSeedArray + 1) % 3; // "improove" random
//        }
//        else if ("0".equals(key)) {
//            seed = (long) (Math.random() * Long.MAX_VALUE);
//        }
//        else {
//            seed = Long.parseLong(key);
//        }

//        return seed;

        return (long) (Math.random() * Long.MAX_VALUE);
    }

    private long getSeed(String key) {
        return getSeed(key, 7);
    }

    private final Object TRAIN_FINISH_LOCK = new Object();

    private void trainFinished() {
        synchronized (TRAIN_FINISH_LOCK) {
            mQuantityOfTrains--;
            if (mQuantityOfTrains <= 0) {
                mTrainExecutor.shutdown();
                mSaveLogExecutor.shutdown();
            }
        }
    }

    private class TrainMlpRunnable implements Runnable {
        private final String mTag;
        private final int mId;

        public TrainMlpRunnable(int id) {
            mId = id;
            mTag = "TRAIN_" + id + "_";
        }

        private void setUp(Configuration cfg, TrainMlp mlp, AsyncLogger logger, int h1Len, int h2Len) {
            // layers
            String h1F = cfg.getProperty(mTag + "FIRST_HIDDEN_FUNCTION", mFirstHiddenFunction);
            String h2F = cfg.getProperty(mTag + "SECOND_HIDDEN_FUNCTION", mSecondHiddenFunction);
            String outF = cfg.getProperty(mTag + "OUTPUT_FUNCTION", mOutputFunction);

            mlp.setLayerFunction(Mlp.Layer.HIDDEN_1, functionForName(h1F));
            mlp.setLayerFunction(Mlp.Layer.HIDDEN_2, functionForName(h2F));
            mlp.setLayerFunction(Mlp.Layer.OUTPUT, functionForName(outF));

            // train
            int epochs = cfg.getIntegerProperty(mTag + "EPOCHS", mEpochs);
            double momentum = cfg.getDoubleProperty(mTag + "MOMENTUM", mMomentum);
            double learnRate = cfg.getDoubleProperty(mTag + "LEARN_RATE", mLearnRate);

            mlp.setEpochs(epochs);
            mlp.setMomentum(momentum);
            mlp.setLearnRate(learnRate);

            // seed
            long seed = getSeed(cfg.getProperty(mTag + "RANDOMIZER_SEED", null), mSeed);
            mlp.setPrimeSeed(seed);

            // print log head
            logger.logTrainHead(h1Len, h2Len, h1F, h2F, outF, seed, epochs, momentum, learnRate);
        }

        @Override
        public void run() {
            try {
                // time
                long startTime = System.currentTimeMillis();

                // config
                int h1Len = mCfg.getIntegerProperty(mTag + "FIRST_HIDDEN_LENGTH", mFirstHiddenLength);
                int h2len = mCfg.getIntegerProperty(mTag + "SECOND_HIDDEN_LENGTH", mSecondHiddenLength);

                // build mlp
                TrainMlp mlp = mTrainBuilder.buildWith(h1Len, h2len);

                // save file
                String file = mWeightsFolder + File.separator + mCfg.getProperty(mTag + "FILE", "train_" + mId + ".dat");

                File saveFile = new File(file);
                if (!saveFile.isFile()) {
                    saveFile.getParentFile().mkdirs();
                }

                // save log file
                File logFile = new File(file.replace(".dat", ".log"));
                AsyncLogger logger = new AsyncLogger(mSaveLogExecutor, logFile);
                mlp.setLogger(logger);

                // setUp
                setUp(mCfg, mlp, logger, h1Len, h2len);

                // start training
                mMessages.printlnLog("TrainMode.Neural.Start", mId);
                mlp.runTrainByEpoch();

                // save results
                mMessages.printlnLog("TrainMode.Neural.Saving", mId, saveFile.getAbsolutePath());
                mlp.saveMlp(saveFile);

                // run test
                logger.logSeparator();
                float result = mlp.runTestSup(mTestPatterns);

                // log end
                logger.close();
                mMessages.printlnLog("TrainMode.Neural.Finished", mId);

                // send mail
                MailSender.send(mTag, h1Len, h2len, startTime, result * 100f, saveFile.getAbsolutePath(), logFile.getAbsolutePath());

                // notify
                trainFinished();
            }
            catch (IOException ex) {
                mMessages.printlnLog("TrainMode.Neural.Error", mId, ex.getMessage());
            }
        }

    }

}

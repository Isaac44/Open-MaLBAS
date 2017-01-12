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
package br.edu.unifei.gpesc.core.neural;

import br.edu.unifei.gpesc.app.Messages;
import br.edu.unifei.gpesc.debug.MailSender;
import br.edu.unifei.gpesc.mlp.Mlp;
import br.edu.unifei.gpesc.mlp.TrainMlp;
import br.edu.unifei.gpesc.mlp.layer.PatternLayer;
import br.edu.unifei.gpesc.mlp.log.MlpLogger;
import br.edu.unifei.gpesc.mlp.math.Function;
import br.edu.unifei.gpesc.mlp.math.Linear;
import br.edu.unifei.gpesc.mlp.math.LogSig;
import br.edu.unifei.gpesc.mlp.math.TanSig;
import br.edu.unifei.gpesc.util.Configuration;
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

    /**
     * The quantity of trains that will be executed.
     */
    private int mQuantityOfTrains;

    /**
     * The quantity of threads executing (in parallel).
     */
    private int mNumberOfActiveThreads;

    /**
     * The first hidden layer funcion name.
     */
    private String mFirstHiddenFunction;

    /**
     * The second hidden layer funcion name.
     */
    private String mSecondHiddenFunction;

    /**
     * The outuput layer funcion name.
     */
    private String mOutputFunction;

    /**
     * The test patterns. They will be tested after the train and the result
     * will be stored in the log file.
     */
    private PatternLayer[] mTestPatterns;

    /**
     * Initializes this, loading the configuration of the file.
     * @param configFile The configuration file path.
     * @throws IOException
     */
    public NeuralModule(String configFile) throws IOException {
        mCfg = new Configuration(configFile);
    }

    /**
     * Force the trains to finish.
     */
    public void stopProcess() {
        if (mTrainExecutor != null) {
            mTrainExecutor.shutdownNow();
            mTrainExecutor = null;
        }
    }

    /**
     * Setup this module. <br />
     * All configuration are readed from the properties file.
     * @throws IOException
     * @throws IllegalArgumentException if any argument not optional is missing.
     */
    public void setUp() throws IOException {
        mQuantityOfTrains = mCfg.getIntegerProperty("NUMBER_OF_TRAINS");
        if (mQuantityOfTrains <= 0) {
            throw new IllegalArgumentException(Messages.i18n("NeuralModule.IllegalArgument.NumberOfTrainsInferior"));
        }

        mWeightsFolder = mCfg.getProperty("WEIGHTS_FOLDER");
        mFirstHiddenLength = mCfg.getIntegerProperty("DEFAULT_FIRST_HIDDEN_LENGTH");
        mSecondHiddenLength = mCfg.getIntegerProperty("DEFAULT_SECOND_HIDDEN_LENGTH");
        mFirstHiddenFunction = mCfg.getProperty("DEFAULT_FIRST_HIDDEN_FUNCTION");
        mSecondHiddenFunction = mCfg.getProperty("DEFAULT_SECOND_HIDDEN_FUNCTION");
        mOutputFunction = mCfg.getProperty("DEFAULT_OUTPUT_FUNCTION");
        mEpochs = mCfg.getIntegerProperty("DEFAULT_EPOCHS");
        mMomentum = mCfg.getDoubleProperty("DEFAULT_MOMENTUM");
        mLearnRate = mCfg.getDoubleProperty("DEFAULT_LEARN_RATE");
        mSeed = getSeed(mCfg.getProperty("DEFAULT_RANDOMIZER_SEED"));
        mNumberOfActiveThreads = mCfg.getIntegerProperty("NUMBER_OF_ACTIVES_THREADS");

        String vectorFolder = mCfg.getProperty("VECTOR_FOLDER");
        Double validationPercent = mCfg.getDoubleProperty("VALIDATION_PERCENT");

        File hamFile = new File(vectorFolder, "ham");
        File spamFile = new File(vectorFolder, "spam");
        mTrainBuilder = new TrainBuilder(hamFile, spamFile, validationPercent);

        openTestPatterns(new File(vectorFolder));
    }

    /**
     * Loads the test patterns
     * @param folder The test patterns folder.
     */
    private void openTestPatterns(File folder) {
        try {
            mTestPatterns = TestBuilder.loadLayers(folder);
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }

    /**
     * Start the trains execution.
     */
    public void start() {
        stopProcess();
        mTrainExecutor = Executors.newFixedThreadPool(mNumberOfActiveThreads);

        for (int train=1; train <= mQuantityOfTrains; train++) {
            mTrainExecutor.execute(new TrainMlpRunnable(train));
        }
    }

    /**
     * Converts the function name to the related implementation.
     * @param name The funtion name.
     * @return The function implementation.
     */
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

    /**
     * The finish lock object to avoid concurrency problems.
     */
    private final Object TRAIN_FINISH_LOCK = new Object();

    /**
     * When a train finishes, this is called. When all trains are finisheds,
     * the train executor is shutdown.
     */
    private void trainFinished() {
        synchronized (TRAIN_FINISH_LOCK) {
            mQuantityOfTrains--;
            if (mQuantityOfTrains <= 0) {
                mTrainExecutor.shutdown();
                mSaveLogExecutor.shutdown();
            }
        }
    }

    /**
     * Train MLP class. <br />
     */
    private class TrainMlpRunnable implements Runnable {

        /**
         * The tag of this train.
         */
        private final String mTag;

        /**
         * The id of this train.
         */
        private final int mId;

        /**
         * Initializes this train with an id.
         * @param id
         */
        public TrainMlpRunnable(int id) {
            mId = id;
            mTag = "TRAIN_" + id + "_";
        }

        /**
         * Set up the configurations of this train.
         * @param c The configuration properties.
         * @param mlp The mlp
         * @param logger The logger.
         * @param h1Len The first hidden layer length.
         * @param h2Len The second hidden layer length.
         */
        private void setUp(Configuration c, TrainMlp mlp, MlpLogger logger, int h1Len, int h2Len) {
            // layers
            String h1F = c.getProperty(mTag + "FIRST_HIDDEN_FUNCTION", mFirstHiddenFunction);
            String h2F = c.getProperty(mTag + "SECOND_HIDDEN_FUNCTION", mSecondHiddenFunction);
            String outF = c.getProperty(mTag + "OUTPUT_FUNCTION", mOutputFunction);

            mlp.setLayerFunction(Mlp.Layer.HIDDEN_1, functionForName(h1F));
            mlp.setLayerFunction(Mlp.Layer.HIDDEN_2, functionForName(h2F));
            mlp.setLayerFunction(Mlp.Layer.OUTPUT, functionForName(outF));

            // train
            int epochs = c.getIntegerProperty(mTag + "EPOCHS", mEpochs);
            double momentum = c.getDoubleProperty(mTag + "MOMENTUM", mMomentum);
            double learnRate = c.getDoubleProperty(mTag + "LEARN_RATE", mLearnRate);

            mlp.setEpochs(epochs);
            mlp.setMomentum(momentum);
            mlp.setLearnRate(learnRate);

            // seed
            long seed = getSeed(c.getProperty(mTag + "RANDOMIZER_SEED", null), mSeed);
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
                MlpLogger logger = new MlpLogger(mSaveLogExecutor, logFile);
                mlp.setLogger(logger);

                // setUp
                setUp(mCfg, mlp, logger, h1Len, h2len);

                // start training
                Messages.printlnLog("TrainMode.Neural.Start", mId);
                mlp.runTrainByEpoch();

                // save results
                Messages.printlnLog("TrainMode.Neural.Saving", mId, saveFile.getAbsolutePath());
                mlp.saveMlp(saveFile);

                // run test
                logger.logSeparator();
                float result = mlp.runTestSup(mTestPatterns);

                // log end
                logger.close();
                Messages.printlnLog("TrainMode.Neural.Finished", mId);

                // send mail
                MailSender.send(mTag, h1Len, h2len, startTime, result * 100f, saveFile.getAbsolutePath(), logFile.getAbsolutePath());

                // notify
                trainFinished();
            }
            catch (IOException ex) {
                Messages.printlnLog("TrainMode.Neural.Error", mId, ex.getMessage());
            }
        }
    }
}
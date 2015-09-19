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

import br.edu.unifei.gpesc.core.mlp.Mlp;
import br.edu.unifei.gpesc.core.mlp.TrainMlp;
import br.edu.unifei.gpesc.core.mlp.Function;
import br.edu.unifei.gpesc.core.mlp.Linear;
import br.edu.unifei.gpesc.core.mlp.LogSig;
import br.edu.unifei.gpesc.core.mlp.TanSig;
import br.edu.unifei.gpesc.log.MlpLogger;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Isaac Caldas Ferreira
 */
public class NeuralModule {

    private ExecutorService mTrainExecutor;
    private final ExecutorService mSaveLogExecutor = Executors.newFixedThreadPool(1);
    private TrainBuilder mTrainBuilder;

    private String mNeuralFolder;

    private int mFirstHiddenLength;
    private int mSecondHiddenLength;

    private int mEpochs;
    private double mMomentum;
    private double mLearnRate;

    private long mSeed;

    private int mQuantityOfTrains;
    private int mNumberOfActiveThreads;

    private String mFirstHiddenFunction;
    private String mSecondHiddenFunction;
    private String mOutputFunction;

    public void stopProcess() {
        if (mTrainExecutor != null) {
            mTrainExecutor.shutdownNow();
            mTrainExecutor = null;
        }
    }

    public void setUp() throws IOException {
        Configuration cfg = Configuration.getInstance();

        mNeuralFolder = cfg.getProperty("NEURAL_FOLDER");
        mFirstHiddenLength = cfg.getIntegerProperty("DEFAULT_FIRST_HIDDEN_LENGTH");
        mSecondHiddenLength = cfg.getIntegerProperty("DEFAULT_SECOND_HIDDEN_LENGTH");
        mFirstHiddenFunction = cfg.getProperty("DEFAULT_FIRST_HIDDEN_FUNCTION");
        mSecondHiddenFunction = cfg.getProperty("DEFAULT_SECOND_HIDDEN_FUNCTION");
        mOutputFunction = cfg.getProperty("DEFAULT_OUTPUT_FUNCTION");
        mEpochs = cfg.getIntegerProperty("DEFAULT_EPOCHS");
        mMomentum = cfg.getDoublePropertie("DEFAULT_MOMENTUM");
        mLearnRate = cfg.getDoublePropertie("DEFAULT_LEARN_RATE");
        mSeed = getSeed(cfg.getProperty("DEFAULT_RANDOMIZER_SEED"));
        mQuantityOfTrains = cfg.getIntegerProperty("NUMBER_OF_TRAINS");
        mNumberOfActiveThreads = cfg.getIntegerProperty("NUMBER_OF_ACTIVES_THREADS");

        String vectorFolder = cfg.getProperty("VECTOR_FOLDER");
        Double validationPercent = cfg.getDoublePropertie("VALIDATION_PERCENT");

        mTrainBuilder = new TrainBuilder(new File(vectorFolder, "ham"), new File(vectorFolder, "spam"), validationPercent);
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

    private class TrainMlpRunnable implements Runnable {
        private final String mTag;
        private final int mId;

        public TrainMlpRunnable(int id) {
            mId = id;
            mTag = "TRAIN_" + id + "_";
        }

        private void setUp(Configuration cfg, TrainMlp mlp, MlpLogger logger, int h1Len, int h2Len) {
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
            System.out.println("Train_" + mId + " seed = " + seed);

            // print log head
            logger.logHead(h1Len, h2Len, h1F, h2F, outF, seed, epochs, momentum, learnRate);
        }

        @Override
        public void run() {
            try {
                Configuration cfg = Configuration.getInstance();
                int h1Len = cfg.getIntegerProperty(mTag + "FIRST_HIDDEN_LENGTH", mFirstHiddenLength);
                int h2len = cfg.getIntegerProperty(mTag + "SECOND_HIDDEN_LENGTH", mSecondHiddenLength);

                // build mlp
                TrainMlp mlp = mTrainBuilder.buildWith(h1Len, h2len);

                // save file
                String file = mNeuralFolder + File.separator + cfg.getProperty(mTag + "FILE", "train_" + mId + ".dat");

                File saveFile = new File(file);
                if (!saveFile.isFile()) {
                    saveFile.getParentFile().mkdirs();
                }

                // save log file
                File logFile = new File(file.replace(".dat", ".log"));
                MlpLogger logger = new MlpLogger(mSaveLogExecutor, logFile);
                mlp.setLogger(logger);

                // setUp
                setUp(cfg, mlp, logger, h1Len, h2len);

                // start training
                Messages.printlnLog("TrainMode.Neural.Start", mId);
                double error = mlp.runTrainByEpoch();
                System.out.println("Train_"+mId + " error = " + error);

                // save results
                Messages.printlnLog("TrainMode.Neural.Saving", mId, saveFile.getAbsolutePath());

                mlp.saveMlp(saveFile);

                Messages.printlnLog("TrainMode.Neural.Finished", mId);
            }
            catch (IOException ex) {
                Messages.printlnLog("TrainMode.Neural.Error", mId, ex.getMessage());
            }
        }

    }

}

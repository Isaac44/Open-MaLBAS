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

import br.edu.unifei.gpesc.neural.mlp3.train.Mlp;
import br.edu.unifei.gpesc.neural.mlp3.train.TrainMlp;
import br.edu.unifei.gpesc.neural.mlp3.util.Function;
import br.edu.unifei.gpesc.neural.mlp3.util.Linear;
import br.edu.unifei.gpesc.neural.mlp3.util.LogSig;
import br.edu.unifei.gpesc.neural.mlp3.util.PrimeNumber;
import br.edu.unifei.gpesc.neural.mlp3.util.TanSig;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Isaac Caldas Ferreira
 */
public class NeuralModule {

    private ExecutorService mThreadPool;
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

    private Function mFirstHiddenFunction;
    private Function mSecondHiddenFunction;
    private Function mOutputFunction;

    public void stopProcess() {
        if (mThreadPool != null) {
            mThreadPool.shutdownNow();
            mThreadPool = null;
        }
    }

    public void load() throws IOException {
        Configuration cfg = Configuration.getInstance();

        mNeuralFolder = cfg.getProperty("NEURAL_FOLDER");
        mFirstHiddenLength = cfg.getIntegerProperty("DEFAULT_FIRST_HIDDEN_LENGTH");
        mSecondHiddenLength = cfg.getIntegerProperty("DEFAULT_SECOND_HIDDEN_LENGTH");
        mFirstHiddenFunction = functionForName(cfg.getProperty("DEFAULT_FIRST_HIDDEN_FUNCTION"));
        mSecondHiddenFunction = functionForName(cfg.getProperty("DEFAULT_SECOND_HIDDEN_FUNCTION"));
        mOutputFunction = functionForName(cfg.getProperty("DEFAULT_OUTPUT_FUNCTION"));
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
        mThreadPool = Executors.newFixedThreadPool(mNumberOfActiveThreads);

        for (int train=1; train <= mQuantityOfTrains; train++) {
            mThreadPool.execute(new TrainMlpRunnable(train));
        }
    }

    private Function functionForName(String name, Function function) {
        if (name == null) {
            return function;
        }
        else {
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
    }

    private Function functionForName(String name) {
        return functionForName(name, null);
    }

    private int mSeedArray = 0;

    private long getSeed(String key, long defaultValue) {
        if (key == null) {
            return defaultValue;
        }

        key = key.toUpperCase();
        final long seed;

        if ("PRIME".equals(key)) {
            switch (mSeedArray) {
                case 1: seed = PrimeNumber.getRandomPrimeNumber2(); break;
                case 2: seed = PrimeNumber.getRandomPrimeNumber3(); break;
                default: seed = PrimeNumber.getRandomPrimeNumber(); break;
            }
            mSeedArray = (mSeedArray + 1) % 3; // "improove" random
        }
        else if ("0".equals(key)) {
            seed = new Random().nextLong();
        }
        else {
            seed = Long.parseLong(key);
        }

        return seed;
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

        @Override
        public void run() {
            Configuration cfg = Configuration.getInstance();
            int h1Len = cfg.getIntegerProperty(mTag + "FIRST_HIDDEN_LENGTH", mFirstHiddenLength);
            int h2len = cfg.getIntegerProperty(mTag + "SECOND_HIDDEN_LENGTH", mSecondHiddenLength);

            // build mlp
            TrainMlp mlp = mTrainBuilder.buildWith(h1Len, h2len);
            mlp.setLayerFunction(Mlp.Layer.HIDDEN_1, functionForName(cfg.getProperty(mTag + "FIRST_HIDDEN_FUNCTION", null), mFirstHiddenFunction));
            mlp.setLayerFunction(Mlp.Layer.HIDDEN_2, functionForName(cfg.getProperty(mTag + "SECOND_HIDDEN_FUNCTION", null), mSecondHiddenFunction));
            mlp.setLayerFunction(Mlp.Layer.OUTPUT, functionForName(cfg.getProperty(mTag + "OUTPUT_FUNCTION", null), mOutputFunction));
            mlp.setPrimeSeed(getSeed(cfg.getProperty(mTag + "RANDOMIZER_SEED", null), mSeed));
            mlp.setEpochs(cfg.getIntegerProperty(mTag + "EPOCHS", mEpochs));
            mlp.setMomentum(cfg.getDoubleProperty(mTag + "MOMENTUM", mMomentum));
            mlp.setLearnRate(cfg.getDoubleProperty(mTag + "LEARN_RATE", mLearnRate));

            // save file
            File saveFile = new File(mNeuralFolder, cfg.getProperty(mTag + "FILE", "train_" + mId + ".dat"));
            if (!saveFile.isFile()) {
                saveFile.getParentFile().mkdirs();
            }

            // start training
            Messages.printlnLog("TrainMode.Neural.Start", mId);
            mlp.runTrainByEpoch();

            // save results
            Messages.printlnLog("TrainMode.Neural.Saving", mId, saveFile.getAbsolutePath());
            try {
                mlp.saveMlp(saveFile);
            } catch (IOException ex) {
                Messages.printlnLog("TrainMode.Neural.Error", mId, ex.getMessage());
            }

            Messages.printlnLog("TrainMode.Neural.Finished", mId);

        }

    }

}

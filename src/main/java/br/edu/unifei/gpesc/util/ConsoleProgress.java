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
package br.edu.unifei.gpesc.util;

/**
 *
 * @author isaac
 */
public class ConsoleProgress {

    private static final int WIDTH = 50;

    private int mMaximum;
    private int mValue;

    public ConsoleProgress() {
    }

    public ConsoleProgress(int maximum) {
        mMaximum = maximum;
    }

    private void updateProgress(double progressPercentage) {
        System.out.printf("\r%d%% [", (int) (progressPercentage * 100));
        int i = 0;
        for (; i <= (int) (progressPercentage * WIDTH); i++) {
            System.out.print(".");
        }
        for (; i < WIDTH; i++) {
            System.out.print(" ");
        }
        System.out.printf("] %d/%d", mValue, mMaximum);
    }

    public void setMaximum(int maximum) {
        mMaximum = maximum;
    }

    public void setValue(int value) {
        mValue = value;
        updateProgress(value / (float) mMaximum);
    }

    public void end() {
        setValue(mMaximum);
        System.out.println();
    }
}

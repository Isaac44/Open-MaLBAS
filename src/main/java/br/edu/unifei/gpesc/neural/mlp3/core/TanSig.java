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
package br.edu.unifei.gpesc.neural.mlp3.core;

/**
 * This class omputes the tansig transfer function: <br>
 * <b>tansig(x) = 2 / (1 + exp(-2 * x)) - 1</b>
 *
 * @author Isaac Caldas Ferreira
 */
public class TanSig implements Function {

    /**
     * Computes the tansig transfer function, which is given by the equation:
     * <b>tansig(x) = 2 / (1 + exp(-2 * x)) - 1</b>
     * @param value {@inheritDoc}
     * @return The tansig result.
     */
    @Override
    public double compute(double value) {
        return (2.0 / (1.0 + Math.exp(-2.0 * value))) - 1.0;
    }

    @Override
    public double compute(double x, double y) {
        return x * (-1.0 * (Math.pow(y, 2.0) - 1.0));
    }

    public static void main(String[] args) {
//        double x = 0.009184076354426375;
//        double y = 0.07006445315680354;

//        double x = 0.009022470232416853;
//        double y = 0.07006444823243618;

        double x=0.009022470232416853, y=0.07006444823243618;

        System.out.println("delta="+new LogSig().compute(x, y));

        double delta_old = x * y * (1.0f - y);
        System.out.println("delta=" + delta_old);

    }

}

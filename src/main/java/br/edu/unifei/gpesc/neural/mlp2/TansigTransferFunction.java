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
package br.edu.unifei.gpesc.neural.mlp2;

/**
 * This class omputes the tansig transfer function: <br>
 * <b>tansig(x) = 2 / (1 + exp(-2 * x)) - 1</b>
 *
 * @author Isaac Caldas Ferreira
 */
public class TansigTransferFunction implements TransferFunction {

    /**
     * Computes the tansig transfer function, which is given by the equation:
     * <b>tansig(x) = 2 / (1 + exp(-2 * x)) - 1</b>
     * @param value {@inheritDoc}
     * @return The tansig result.
     */
    @Override
    public float compute(float value) {
        return (2f / (1f + (float) Math.exp(-2.0 * value))) - 1f;
    }
}

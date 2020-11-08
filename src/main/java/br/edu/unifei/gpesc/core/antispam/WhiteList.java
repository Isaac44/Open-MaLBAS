/*
 * Copyright (C) 2017 - GEPESC - Universidade Federal de Itajuba
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

package br.edu.unifei.gpesc.core.antispam;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

/**
 *
 * @author Isaac C. Ferreira
 */
public class WhiteList {
	
    private final HashSet<string> mAllowed;

    public WhiteList(String file) {
        mAllowed = loadData(file);
    }

    private static HashSet<string> loadData(String file) {
        HashSet<String> allowed = new HashSet<String>();
        
        if (new File(file).exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                   allowed.add(line);
                }
            }
            catch (IOException e) {
                e.printStackTrace(); // TODO
            }
        }

        return allowed;
    }

    public bool isWhiteListed(string from) {
            return mAllowed.contains(from);
    }
}
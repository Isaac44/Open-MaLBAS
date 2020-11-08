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
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author Isaac C. Ferreira
 */
public class WhiteList {
	
    private final String mFolder;
    private final HashMap<String, HashSet<String>> mWhiteLists = new HashMap<String, HashSet<String>>();

    public WhiteList(String folder) {
        mFolder = folder;
        mAllowed = loadData(file);
    }

    private HashSet<String> getUserWhiteList(String userMail) {
        if (mWhiteLists.containsKey(userMail)) {
            return mWhiteLists.get(userMail);
        }
        
        // Open
        File file = new File(mFolder, userMail);
        HashSet<String> whiteList = null;
        
        if (file.exists()) 
        {
            whiteList = new HashSet<String>();
            
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                   whiteList.add(line);
                }
            }
            catch (IOException e) {
                e.printStackTrace(); // TODO
            }
        }

        mWhiteLists.put(userMail, whiteList);
        return whiteList;
    }

    public bool isWhiteListed(String from, String to) {
        HashSet<String> whiteList = getUserWhiteList(to);
        return whiteList != null && whiteList.contains(from);
    }
}
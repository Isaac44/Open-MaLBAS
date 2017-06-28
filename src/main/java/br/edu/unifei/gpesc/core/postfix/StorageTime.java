/*
 * Copyright (C) 2016 - GEPESC - Universidade Federal de Itajuba
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

package br.edu.unifei.gpesc.core.postfix;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 *  Esta tem por objetivo atualizar o horario nos arquivos de log das Storages.
 * Com isto, o Modulo de Notificacao pode determinar quais e-mails devem ser enviados e quais nao.
 *
 * @author Isaac C. Ferreira
 */
public class StorageTime {

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static void startService(final Storage storage) {
        Calendar today = Calendar.getInstance();
        today.add(Calendar.DAY_OF_MONTH, 1);

        final Date tomorrow = today.getTime();


        new Timer("storage-timer").schedule(new TimerTask() {
            @Override
            public void run() {
                String time = DATE_FORMAT.format(tomorrow);
//                storage.setLogFileName(time);
                startService(storage);
            }
        }, tomorrow);
    }
}

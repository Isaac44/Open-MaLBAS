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

import br.edu.unifei.gpesc.core.antispam.AntiSpam;
import br.edu.unifei.gpesc.util.TraceLog;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.RejectException;
import org.subethamail.smtp.TooMuchDataException;
import org.subethamail.smtp.client.SmartClient;

/**
 *
 * @author isaac
 */
public class old_DataHandler implements MessageHandler {

    /**
     * Destiny.
     */
    private String mTo;

    /**
     * Origin.
     */
    private String mFrom;

    /**
     * The Backup and Spam storage machanism.
     */
    private final Storage mStorage;

    /**
     * The Anti-Spam Classificator
     */
    private final AntiSpam mClassificator;

    /**
     * The mail data.
     */
    private final byte[] mData;

    private final Callback mCallback;

    /**
     * The mail data length.
     */
    private int mDataLen;

    /**
     * Creates a Mail Handler with the maximum size of the mail body.
     * @param storage
     * @param classificator
     * @param maxLen The max data length.
     * @param callback The callback for the finish notify.
     */
    public old_DataHandler(Storage storage, AntiSpam classificator, int maxLen, Callback callback) {
        mStorage = storage;
        mClassificator = classificator;
        mData = new byte[maxLen];
        mCallback = callback;
    }

    @Override
    public void from(String from) throws RejectException {
        mFrom = from;
    }

    @Override
    public void recipient(String rcpt) throws RejectException {
        mTo = rcpt;
    }

    @Override
    public void data(InputStream in) throws RejectException, TooMuchDataException, IOException {
        mDataLen = in.read(mData);
    }

    /**
     * Sends the email back to Postfix.
     */
    private void returnMail() {
        try {
            SmartClient client = new SmartClient("localhost", 10026, "localhost");

            client.from(mFrom);
            client.to(mTo);

            client.dataStart();
            client.dataWrite(mData, mDataLen);
            client.dataEnd();

            System.out.println("mail relayed.");

        } catch (IOException ex) {
            TraceLog.logE(ex);
        }
    }

    /**
     * Send the received e-mail to Storage.
     * @param isSpam
     */
    private void storeMail(boolean isSpam) {
//        mStorage.store(mTo, new String(mData, 0, mDataLen), isSpam);
    }

    @Override
    public void done() {
        // Process mail
        AntiSpam.Result result = mClassificator.processMail(new ByteArrayInputStream(mData, 0, mDataLen));

        // DEBUG
        System.out.println("result = " + result);

        boolean isSpam = result == AntiSpam.Result.SPAM;
        if (!isSpam) {
            returnMail();
        }

        // Backup or store spam
        storeMail(isSpam);

        // Callback
        mCallback.handlerFinished(this);
    }

    public interface Callback {
        void handlerFinished(old_DataHandler handler);
    }
}
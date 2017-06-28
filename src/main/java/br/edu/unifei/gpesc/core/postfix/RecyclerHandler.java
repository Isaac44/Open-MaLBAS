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
package br.edu.unifei.gpesc.core.postfix;

import br.edu.unifei.gpesc.util.TransactionalInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.RejectException;
import org.subethamail.smtp.TooMuchDataException;

/**
 *
 * @author Isaac C. Ferreira
 */
public abstract class RecyclerHandler implements MessageHandler {

    private final TransactionalInputStream mTransitionInput = new TransactionalInputStream(1204 * 1024, 8 * 1024);

    /**
     * Destiny.
     */
    private String mmTo;

    /**
     * Origin.
     */
    private String mmFrom;

    private final RecyclerHandlerFactory mFactory;

    public RecyclerHandler(RecyclerHandlerFactory factory) {
        mFactory = factory;
    }

    @Override
    public void from(String from) throws RejectException {
        mmFrom = from;
    }

    @Override
    public void recipient(String rcpt) throws RejectException {
        mmTo = rcpt;
    }

    @Override
    public void data(InputStream in) throws RejectException, TooMuchDataException, IOException {
        mTransitionInput.copyData(in);
    }

    @Override
    public final void done() {
        onDataReceived(mmFrom, mmTo, mTransitionInput);
        mFactory.recycleHandler(this);
    }

    protected abstract void onDataReceived(String from, String to, TransactionalInputStream in);
}

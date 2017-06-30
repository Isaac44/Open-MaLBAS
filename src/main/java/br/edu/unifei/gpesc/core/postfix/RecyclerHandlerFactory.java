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

import java.util.LinkedList;
import java.util.List;
import org.subethamail.smtp.MessageContext;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.MessageHandlerFactory;

/**
 *
 * @author Isaac C. Ferreira
 */
public abstract class RecyclerHandlerFactory implements MessageHandlerFactory {

    /**
     * The synchronism monitor.
     */
    private final Object LOCK = new Object();

    /**
     * Recycler to minimize the use of memory.
     */
    private final List<MessageHandler> mSyncReclycer = new LinkedList<MessageHandler>();

    /**
     * Gets a recycled DataHandler or creates a new one.
     *
     * @return A ready to use DataHandler
     */
    private MessageHandler getHandler() {
        if (mSyncReclycer.isEmpty()) {
            return createHandler();
        } else {
            return mSyncReclycer.remove(0);
        }
    }

    protected abstract MessageHandler createHandler();

    public void recycleHandler(MessageHandler handler) {
        synchronized (LOCK) {
            mSyncReclycer.add(handler);
        }
    }

    @Override
    public MessageHandler create(MessageContext mc) {
        synchronized (LOCK) {
            return getHandler();
        }
    }
}

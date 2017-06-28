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
package br.edu.unifei.gpesc.core.modules;

import br.edu.unifei.gpesc.util.TraceLog;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author isaac
 */
public class Mail {

    /**
     * Flag that indicates that the content type should be processed as HTML.
     */
    private static final int HTML = 0;

    /**
     * Flag that indicates that the content type should be processed has TEXT.
     */
    private static final int TEXT = 1;

    /**
     * The default mail session.
     */
    private static final Session MAIL_SESSION;

    static {
        Properties props = System.getProperties();
        props.setProperty("mail.mime.multipart.ignoreexistingboundaryparameter", "true");
        MAIL_SESSION = Session.getInstance(props);
    }

    /**
     * The current openned mail type.
     */
    private int mMessageType;

    /**
     * The current oppened mail Part.
     */
    private String mContent;

    /**
     * Process the email file.
     * @param mailpath The path to the email file.
     * @return True if no errors ocurred.<br>False otherwise.
     */
    public boolean processMail(String mailpath) {
        try {
            Part mailPart = getProcessableMailContent(mailpath);

            if (mailPart != null) {
                if (isTextHtml(mailPart)) mMessageType = HTML;
                else mMessageType = TEXT;
                mContent = (String) mailPart.getContent();
                return true;
            }
        }
        catch (IOException ex) {
            TraceLog.logE("Processing \"" + mailpath + "\"", ex);
        }
        catch (MessagingException ex) {
            TraceLog.logE("Processing \"" + mailpath + "\"", ex);
        }

        return false;
    }

    /**
     * Process the email from the {@link InputStream}.
     * @param mailInputStream The stream of the email.
     * @return True if no errors ocurred.<br>False otherwise.
     */
    public boolean processMail(InputStream mailInputStream) {
        try {
            Part mailPart = getProcessableMailContent(mailInputStream);

            if (mailPart != null) {
                if (isTextHtml(mailPart)) mMessageType = HTML;
                else mMessageType = TEXT;
                mContent = (String) mailPart.getContent();
                return true;
            }

        }
        catch (IOException ex) {
            TraceLog.logE(ex);
        }
        catch (MessagingException ex) {
            TraceLog.logE(ex);
        }

        return false;
    }

    /**
     * Checks if the last processed email should be processed as text.
     * @return True if is text or False if not.
     */
    public boolean isText() {
        return (mMessageType == TEXT);
    }

    /**
     * Checks if the last processed email should be processed as html.
     * @return True if is text or False if not.
     */
    public boolean isHtml() {
        return (mMessageType == HTML);
    }

    /**
     * Gets the content of the last processed email.
     * @return The last email content.
     */
    public String getContent() {
        return mContent;
    }

    /**
     * This static method process a mail file and get the first valid content to
     * be processed by the anti-spam filter.
     * <br> For more infomation of how this is done, see
     * {@link MailProcessor#getProcessablePart(javax.mail.Part)}
     * @param mailpath The mail file path.
     * @return The first processable {@link Part} of the input mail.
     * @throws MessagingException If any error at the mail processing occurs.
     * @throws FileNotFoundException If the file do not exists.
     * @throws IOException If was not possible read the file.
     */
    public static Part getProcessableMailContent(String mailpath) throws MessagingException, IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(mailpath));
        MimeMessage message = new MimeMessage(MAIL_SESSION, in);
        Part part = getProcessablePart(message);
        in.close();
        return part;
    }

    public static Part getProcessableMailContent(InputStream in) throws MessagingException, IOException {
        MimeMessage message = new MimeMessage(MAIL_SESSION, in);
        return getProcessablePart(message);
    }

    /**
     * This static method checks if the part is plain text.
     * @param part The input part.
     * @return True if is plain text, false otherwise.
     * @throws MessagingException
     */
    public static boolean isTextPlain(Part part) throws MessagingException {
        return part.isMimeType("text/plain");
    }

    /**
     * This static method checks if the part is html text.
     * @param part The input part.
     * @return True if is html text, false otherwise.
     * @throws MessagingException
     */
    public static boolean isTextHtml(Part part) throws MessagingException {
        return part.isMimeType("text/html");
    }

    /**
     * This static method checks if the part is multipart.
     * @param part The input part.
     * @return True if is multipart, false otherwise.
     * @throws MessagingException
     */
    public static boolean isMultipart(Part part) throws MessagingException {
        return part.isMimeType("multipart/*");
    }

    /**
     * This static method recursive traverse the {@link Part} that represents
     * an email, looking for the first sub {@link Part} that can be processed
     * by the anti-spam filter. In other words, it looks for the first sub
     * {@link Part} which its {@link Part#getContent()} is instance of
     * {@link String}.
     * @param part
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    private static Part getProcessablePart(Part part) throws MessagingException, IOException {

        // processable
        if (isTextPlain(part) || isTextHtml(part)) {
            return part;
        }

        // multipart, go further!
        else if (isMultipart(part)) {
            Multipart mp = (Multipart) part.getContent();
            int count = mp.getCount();
            Part subPart;
            for (int i = 0; i < count; i++) {
                subPart = getProcessablePart(mp.getBodyPart(i));
                if (subPart != null) return subPart;
            }
        }

        // unknown type...
        else {
            // ... but is processable!
            if (part.getContent() instanceof String) {
                return part;
            }
        }

        return null;
    }
}
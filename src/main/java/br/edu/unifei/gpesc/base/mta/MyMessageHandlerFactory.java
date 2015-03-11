//package br.edu.unifei.gpesc.base.mta;
//
//import org.subethamail.smtp.*;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//
//public class MyMessageHandlerFactory implements MessageHandlerFactory {
//
//    @Override
//    public MessageHandler create(MessageContext ctx) {
//        return new Handler(ctx);
//    }
//
//    class Handler implements MessageHandler {
//        MessageContext ctx;
//
//        public Handler(MessageContext ctx) {
//                this.ctx = ctx;
//        }
//
//        @Override
//        public void from(String from) throws RejectException {
//                System.out.println("FROM:"+from);
//        }
//
//        @Override
//        public void recipient(String recipient) throws RejectException {
//                System.out.println("RECIPIENT:"+recipient);
//        }
//
//        @Override
//        public void data(InputStream data) throws IOException {
//                System.out.println("MAIL DATA");
//                System.out.println("= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =");
//                System.out.println(convertStreamToString(data));
//                System.out.println("= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =");
//        }
//
//        @Override
//        public void done() {
//                System.out.println("Finished");
//        }
//
//        public String convertStreamToString(InputStream is) {
//                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//                StringBuilder sb = new StringBuilder();
//
//                String line = null;
//                try {
//                        while ((line = reader.readLine()) != null) {
//                                sb.append(line + "\n");
//                        }
//                } catch (IOException e) {
//                        e.printStackTrace();
//                }
//                return sb.toString();
//        }
//
//    }
//}

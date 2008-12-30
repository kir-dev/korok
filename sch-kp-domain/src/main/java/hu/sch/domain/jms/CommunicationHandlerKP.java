/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.domain.jms;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

/**
 *
 * @author major
 */
public class CommunicationHandlerKP {

    private static String senderQueueName = null;
    private static String receiverQueueName = null;
    private static Context jndiContext = null;
    private static QueueConnectionFactory queueConnectionFactory = null;
    private static QueueConnection queueConnection = null;
    private static QueueSession queueSession = null;
    private static Queue senderQueue = null;
    private static Queue receiverQueue = null;
    private static QueueSender queueSender = null;
    private static QueueReceiver queueReceiver = null;
    private static ReceiverListenerKP receiverListener = null;
    private static volatile CommunicationHandlerKP INSTANCE;

    protected CommunicationHandlerKP() {
        try {
            jndiContext = new InitialContext();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            queueConnectionFactory = (QueueConnectionFactory) jndiContext.lookup("schConnectionFactory");
            senderQueue = (Queue) jndiContext.lookup("profileQueue");
            receiverQueue = (Queue) jndiContext.lookup("schkpQueue");
        } catch (Exception e) {
            System.out.println("nem sikerült a névfeloldás");
            System.out.println(e.getMessage());
        }
        try {
            queueConnection = queueConnectionFactory.createQueueConnection();
            queueSession = queueConnection.createQueueSession(false, Session.CLIENT_ACKNOWLEDGE);
            queueSender = queueSession.createSender(senderQueue);
            queueReceiver = queueSession.createReceiver(receiverQueue);
            receiverListener = new ReceiverListenerKP();
            queueReceiver.setMessageListener(receiverListener);
            queueConnection.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            closeConnection();
            INSTANCE = null;
        } finally {
        }
    }

    private static synchronized CommunicationHandlerKP tryCreateInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CommunicationHandlerKP();
        }
        return INSTANCE;
    }

    public static CommunicationHandlerKP getInstance() {
        CommunicationHandlerKP ch = INSTANCE;
        if (ch == null) {
            ch = tryCreateInstance();
        }
        return ch;
    }

    public void sendMessage(String message) {
        try {
            TextMessage textMessage = queueSession.createTextMessage();
            textMessage.setText(message);
            System.out.println("Sending message: " + textMessage.getText());
            queueSender.send(textMessage);
        //queueSender.send(queueSession.createMessage());
        } catch (JMSException e) {
            System.out.println(e.getMessage());
            closeConnection();
            INSTANCE = null;
        }
    }

    public void closeConnection() {
        if (queueConnection != null) {
            try {
                queueConnection.close();
                INSTANCE = null;
            } catch (JMSException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}

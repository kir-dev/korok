/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.domain.jms;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 *
 * @author major
 */
public class ReceiverListenerKP implements MessageListener {

    public void onMessage(Message receivedMessage) {
        if (receivedMessage instanceof TextMessage) {
            try {
                TextMessage message = (TextMessage) receivedMessage;
                System.out.println("KP kapott egy üzit " + message.getText());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("nem Text message érkezett a KP-nak");
        }
    }
}
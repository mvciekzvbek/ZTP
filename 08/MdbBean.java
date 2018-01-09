import pl.jrj.mdb.IMdbManager;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.jms.ConnectionFactory;
import javax.jms.Topic;
import javax.jms.Connection;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * @author Maciej Ząbek
 * @version 1.0
 */
@MessageDriven(mappedName = "jms/MyQueue", activationConfig = {
        @ActivationConfigProperty(
                propertyName = "destinationType",
                propertyValue = "javax.jms.Queue"
        ),
        @ActivationConfigProperty(
                propertyName = "acknowledgeMode",
                propertyValue = "Auto-acknowledge"
        )
})
public class MdbBean implements MessageListener {
    private double counter = 0;
    private double errorCounter = 0;
    private String sessionId = null;
    private boolean isStarted = false;


    /**
     * Listen for message from JMC
     * @param message
     */
    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                TextMessage tm = (TextMessage) message;
                String text = tm.getText();

                if ("start".equals(text)) {
                    start();
                } else if ("stop".equals(text)) {
                    stop();
                } else if ("val".equals(text)) {
                    sendMessage("124640" + "/" + counter);
                } else if ("err".equals(text)) {
                    sendMessage("124640" + "/" + errorCounter);
                } else if ("inc".equals(text)) {
                    incrementCounter(1);
                } else if ("dec".equals(text)) {
                    decrementCounter(1);
                } else if (text.startsWith("inc/")) {
                    incrementCounter(Double.parseDouble(
                            text.substring(text.lastIndexOf("/") + 1)));
                } else if (text.startsWith("dec/")){
                    decrementCounter(Double.parseDouble(
                            text.substring(text.lastIndexOf("/") + 1)));
                } else {
                    errorCounter++;
                }
            }
        } catch (Exception e) {}
    }

    /**
     * start counter or count error if counter is already started
     */
    private void start() {
        if (isStarted) {
            errorCounter++;
        } else {
            if (sessionId != null) {
                isStarted = true;
            } else {
                startSession();
            }
        }
    }

    /**
     * start session and set start flag as true - started
     */
    private void startSession(){
        retrieveSessionId("124640");
        if (sessionId != null) {
            isStarted = true;
        }
    }

    /**
     * retrieve session id using JNDI
     * @param album
     */
    private void retrieveSessionId(String album){
        try {
            IMdbManager mdb = (IMdbManager) new InitialContext().
                    lookup("java:global/ejb-project/" +
                            "MdbManager!pl.jrj.mdb.IMdbManager");
            sessionId = mdb.sessionId(album);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * set start flag as false - not started
     */
    private void stop() {
        if (!isStarted) {
            errorCounter++;
        } else {
            isStarted = false;
        }
    }

    /**
     * increment counter with a given value
     * @param value
     */
    private void incrementCounter(double value) {
        if (isStarted) {
            counter += value;
        } else {
            errorCounter++;
        }
    }

    /**
     * decrement counter with a given value
     * @param value
     */
    private void decrementCounter(double value) {
        if (isStarted) {
            counter -= value;
        } else {
            errorCounter++;
        }
    }

    /**
     * Send message
     * @param message string
     * @throws JMSException
     * @throws NamingException
     */
    public void sendMessage(String message)
            throws JMSException, NamingException{
            InitialContext context = new InitialContext();
            ConnectionFactory connectionFactory = (ConnectionFactory)
                    context.lookup("jms/ConnectionFactory");
            Topic topic = (Topic) context.lookup("jms/MyTopic");
            Connection connection = connectionFactory.createConnection();
            Session session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            TextMessage textMessage = session.createTextMessage();
            textMessage.setText(message);
            MessageProducer messageProducer = session.createProducer(topic);
            messageProducer.send(textMessage);
    }
}

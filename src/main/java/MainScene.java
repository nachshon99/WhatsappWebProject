import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import javax.swing.*;
import java.awt.*;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;



public class MainScene extends JPanel {

    private LocalDateTime contemporaryTime;
    private ChromeDriver driver;
    private JButton openWhatsappWebButton;
    private JTextField enterPhoneNumberTextField;
    private JTextField messageToSendTextField;
    private JFrame frame = new JFrame();
    private ImageIcon background;
    private JLabel backgroundLabel;
    private JLabel enterPhoneNumberText;
    private JLabel enterMessageText;
    private JLabel statusMessage;
    private JTextField statusTextField;
    private JPanel connectionSucceedPanel = new JPanel();
    private JLabel connectionLabel = new JLabel(Constants.CONNECTION_LABEL);
    private JPanel sendMessageSucceedPanel = new JPanel();
    private JLabel sendMessageLabel = new JLabel(Constants.SEND_MESSAGE_LABEL);

    private boolean tryToConnect = true;
    private boolean update = true, v1 = true, v2 = true;
    private boolean checkMessage = true;

    public MainScene() {
        this.setSize(Window.WINDOW_WIDTH, Window.WINDOW_HEIGHT);
        this.setLayout(null);
        this.setBackground(null);
        this.setDoubleBuffered(true);

        connectionSucceedPanel.setBounds(Constants.X_PANEL, Constants.Y_PANEL, Constants.WIDTH_PANEL, Constants.HEIGHT_PANEL);
        connectionSucceedPanel.setBackground(Color.BLUE);
        connectionLabel.setBounds(connectionSucceedPanel.getX() + Constants.HEIGHT_PANEL,
                connectionSucceedPanel.getY(),
                connectionSucceedPanel.getWidth(),
                connectionSucceedPanel.getHeight());
        connectionLabel.setFont(new Font("arial", Font.BOLD, Constants.SIZE_TEXT));
        connectionLabel.setForeground(Color.YELLOW);
        connectionSucceedPanel.add(connectionLabel);
        connectionSucceedPanel.setVisible(false);
        this.add(connectionSucceedPanel);

        sendMessageSucceedPanel.setBounds(Constants.X_PANEL, Constants.Y_PANEL + Constants.HEIGHT_PANEL, Constants.WIDTH_PANEL, Constants.HEIGHT_PANEL);
        sendMessageSucceedPanel.setBackground(Color.BLUE);
        sendMessageLabel.setBounds(sendMessageSucceedPanel.getX() + 20,
                sendMessageSucceedPanel.getY(),
                sendMessageSucceedPanel.getWidth(),
                sendMessageSucceedPanel.getHeight());
        sendMessageLabel.setFont(new Font("arial", Font.BOLD, Constants.SIZE_TEXT));
        sendMessageLabel.setForeground(Color.YELLOW);
        sendMessageSucceedPanel.add(sendMessageLabel);
        sendMessageSucceedPanel.setVisible(false);
        this.add(sendMessageSucceedPanel);

        //WhatsappWeb button
        openWhatsappWebButton = new JButton(Constants.OPEN_WEB_BUTTON);
        openWhatsappWebButton.setBounds(Constants.X_BUTTON, Constants.Y_BUTTON, 200, 60);
        openWhatsappWebButton.setFont(new Font("arial", Font.BOLD, Constants.SIZE_BUTTON));
        openWhatsappWebButton.setVisible(true);
        this.add(openWhatsappWebButton);
        //ClickButton
        openWhatsappWebButton.addActionListener((event) -> {
            if (enterPhoneNumberTextField.getText().length() == Constants.INITIALIZE) {
                JOptionPane.showConfirmDialog(frame, Constants.ERROR_ENTER_PHONE, "Error", JOptionPane.CLOSED_OPTION);
            } else {
                if (!checkPhoneNumberFormat(enterPhoneNumberTextField.getText())) {
                    JOptionPane.showConfirmDialog(frame, Constants.ERROR_INVALID_NUMBER, "Error", JOptionPane.CLOSED_OPTION);
                } else {
                    if (messageToSendTextField.getText().length() == Constants.INITIALIZE) {
                        JOptionPane.showConfirmDialog(frame, Constants.ERROR_ENTER_MESSAGE, "Error", JOptionPane.CLOSED_OPTION);
                    } else {
                        openWhatsappWebButton.setEnabled(false);
                        System.setProperty(Constants.CHROME_DRIVER, Constants.PATH_TO_CHROME_DRIVER);
                        this.driver = new ChromeDriver();
                        driver.get(Constants.URL_WEB);
                        driver.manage().window().maximize();
                        if (tryConnect(driver)) {
                            connectionSucceedPanel.setVisible(true);
                            connectToPhoneNumber(enterPhoneNumberTextField.getText());
                        }
                        if (tryToSendMessage()) {
                            sendMessageSucceedPanel.setVisible(true);
                            contemporaryTime = LocalDateTime.now();
                            new Thread(() -> {
                                while (update) {
                                    if (checkV(this.driver, Constants.STATUS_SENT, contemporaryTime) && v1) {
                                        statusTextField.setText(Constants.V);
                                        v1 = false;
                                    }
                                    if (checkV(this.driver, Constants.STATUS_DELIVERED, contemporaryTime) && v2) {
                                        statusTextField.setText(Constants.VV);
                                        v2 = false;
                                    }
                                    if (checkV(this.driver, Constants.STATUS_READ, contemporaryTime) && update) {
                                        statusTextField.setText(Constants.VV);
                                        statusTextField.setDisabledTextColor(Color.BLUE);

                                        String message = waitingForMessage(Thread.currentThread(),driver,messageToSendTextField);
                                        JLabel messageLabel = new JLabel(message);
                                        messageLabel.setBounds(openWhatsappWebButton.getX(),openWhatsappWebButton.getY() + openWhatsappWebButton.getHeight() , 500,50);
                                        this.add(messageLabel);
                                        update = false;
                                    }
                                }
                            }).start();
                        }
                        //in the end
                        //driver.close();
                    }
                }
            }
        });

        createUI(this);

        background =new ImageIcon(this.getClass().getResource(Constants.PATH_RESOURCE));
        backgroundLabel =new JLabel(background);
        backgroundLabel.setSize(Window.WINDOW_WIDTH,Window.WINDOW_HEIGHT);
        this.add(backgroundLabel);

        this.setVisible(true);

    }

    public String waitingForMessage(Thread thread, ChromeDriver driver, JTextField textField){
        WebElement messageElement = null;
        WebElement checkMessageFromMe = null;
        while (checkMessage){
            try {
                System.out.println("no message");
                contemporaryTime = LocalDateTime.now();
                List<WebElement> messages = driver.findElements(By.cssSelector("div[class=\"Nm1g1 _22AX6\"]"));
                for (WebElement webElement: messages){
                    try {
                        checkMessageFromMe = webElement.findElement(By.cssSelector("span[aria-label=\"את/ה:\"]"));
                    }catch (Exception e){
                    }
                    //WebElement textElement = webElement.findElement(By.cssSelector("div[class=\"_1Gy50\"]"));
                    messageElement = webElement.findElement(By.cssSelector("span[class=\"f804f6gw ln8gz9je\"]"));
                    WebElement timeMessage = webElement.findElement(By.cssSelector("span[class=\"l7jjieqr fewfhwl7\"]"));
                    LocalDateTime messageTime = LocalDateTime.parse(timeMessage.getText());
                    if(!textField.getText().equals(messageElement.getText()) && checkMessageFromMe ==null && contemporaryTime.isBefore(messageTime)){
                        System.out.println("have a new message");
                        System.out.println(messageElement.getText());
                        checkMessage = false;
                        break;
                    }
                    thread.sleep(Constants.SLEEP_TIME);
                }
            }catch (InterruptedException | DateTimeException e){
            }
        }
        return messageElement.getText();
    }
    public static boolean checkV(ChromeDriver driver, String arialLabel, LocalDateTime time) {
        boolean check = false;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(Constants.TIME_FORMAT);
        try {
            List<WebElement> messagesElements = driver.findElements(By.cssSelector("div[class=\"_22Msk\"]"));
            for (WebElement webElement : messagesElements) {
                WebElement timeElement = webElement.findElement(By.cssSelector("div[class=\"_1beEj\"]"));
                if (timeElement.getText().equals(dateTimeFormatter.format(time))) {
                    WebElement vElement = webElement.findElement(By.cssSelector("div[class=\"do8e0lj9 l7jjieqr k6y3xtnu\"]"));
                    try {
                        vElement.findElement(By.cssSelector("span[aria-label=\"" + arialLabel + "\"]"));
                        check = true;
                        break;
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e) {
            checkV(driver, arialLabel, time);
        }
        return check;
    }
    public boolean tryToSendMessage() {
        boolean tryToSend = true;
        while (tryToSend) {
            try {
                WebElement textBox = driver.findElement(By.cssSelector(Constants.CSS_SELECTOR_TRY_SEND_MESSAGE));
                textBox.sendKeys(messageToSendTextField.getText());
                textBox.sendKeys(Keys.ENTER);
            } catch (Exception e) {
                tryToSendMessage();
            }
            tryToSend = false;
        }

        return true;
    }
    public void connectToPhoneNumber(String phoneNumber) {
        String phoneNumberWithoutZero = phoneNumber.substring(1);
        this.driver.get(Constants.URL_TO_CHAT + phoneNumberWithoutZero);
    }
    public static boolean checkPhoneNumberFormat(String phoneNumber) {
        boolean isValid = false;
        if (phoneNumber.length() == Constants.LENGTH_TEN_DIGITS) {
            if (phoneNumber.charAt(0) == Constants.ZERO_CHAR) {
                if (phoneNumber.charAt(1) == Constants.FIVE_CHAR) {
                    for (int i = Constants.START_FOR_2; i < phoneNumber.length(); i++) {
                        if (Character.isDigit(phoneNumber.charAt(i))) {
                            isValid = true;
                        } else {
                            isValid = false;
                            break;
                        }
                    }
                }
            }
        } else if (phoneNumber.length() == Constants.LENGTH_TWELVE_DIGITS) {
            if (phoneNumber.charAt(0) == Constants.NINE_CHAR && phoneNumber.charAt(1) == Constants.SEVEN_CHAR && phoneNumber.charAt(2) == Constants.TWO_CHAR && phoneNumber.charAt(3) == Constants.FIVE_CHAR) {
                for (int i = Constants.START_FOR_4; i < phoneNumber.length(); i++) {
                    if (Character.isDigit(phoneNumber.charAt(i))) {
                        isValid = true;
                    } else {
                        isValid = false;
                        break;
                    }
                }
            }
        }
        return isValid;
    }
    public JTextField createTextField(int x, int y, int width, int height) {
        JTextField textField = new JTextField();
        textField.setBounds(x, y, width, height);
        textField.setFont(new Font("arial", Font.BOLD, Constants.SIZE_TEXT_FIELD));
        textField.setForeground(Color.BLUE);
        textField.setBackground(Color.lightGray);
        textField.setVisible(true);

        return textField;
    }
    public JLabel createJLabel(String text, int x, int y, int width, int height) {
        JLabel jLabel = new JLabel(text);
        jLabel.setBounds(x, y, width, height);
        jLabel.setFont(new Font("arial", Font.BOLD, Constants.SIZE_TEXT));
        jLabel.setVisible(true);

        return jLabel;
    }
    public void createUI(JPanel panel) {
        //Phone number text
        enterPhoneNumberTextField = createTextField(Constants.X_BUTTON, Window.WINDOW_HEIGHT / 5, Constants.WIDTH_TEXT_FIELD, Constants.HEIGHT_PANEL);
        panel.add(enterPhoneNumberTextField);

        //TextField to enter phone number
        enterPhoneNumberText = createJLabel("Enter phone number: ", enterPhoneNumberTextField.getX(), enterPhoneNumberTextField.getY() - Constants.HEIGHT_PANEL, Constants.WIDTH_TEXT_FIELD, Constants.HEIGHT_PANEL);
        panel.add(enterPhoneNumberText);

        //TextField to enter message
        messageToSendTextField = createTextField(Constants.X_BUTTON, Window.WINDOW_HEIGHT - 400, Constants.WIDTH_TEXT_FIELD, Constants.HEIGHT_PANEL);
        panel.add(messageToSendTextField);

        //Message text
        enterMessageText = createJLabel("Enter a message: ", messageToSendTextField.getX(), messageToSendTextField.getY() - Constants.HEIGHT_PANEL, Constants.WIDTH_TEXT_FIELD, Constants.HEIGHT_PANEL);
        panel.add(enterMessageText);

        //Status message text
        statusMessage = createJLabel("Status: ", Window.WINDOW_WIDTH / 10, Window.WINDOW_HEIGHT / 3, Constants.WIDTH_TEXT_FIELD, Constants.HEIGHT_PANEL);
        statusMessage.setForeground(Color.BLACK);
        this.add(statusMessage);

        //Status Text Field
        statusTextField = createTextField(statusMessage.getX(), statusMessage.getY() + 40, statusMessage.getHeight(), statusMessage.getHeight());
        statusTextField.setBackground(Color.black);
        statusTextField.setDisabledTextColor(Color.GRAY);
        statusTextField.setEnabled(false);
        statusTextField.setText("");
        this.add(statusTextField);
    }
    public boolean tryConnect(ChromeDriver driver) {
        boolean isConnect = false;
        while (tryToConnect) {
            try {
                driver.findElement(By.cssSelector(Constants.CSS_SELECTOR_TRY_CONNECT));
            } catch (Exception e) {
                tryConnect(driver);
            }
            tryToConnect = false;
            isConnect = true;
        }
        return isConnect;
    }
}

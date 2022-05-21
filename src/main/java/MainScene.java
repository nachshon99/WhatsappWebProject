import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.zip.DataFormatException;


public class MainScene extends JPanel {
    public static final String OPEN_WEB_BUTTON = "Whatsapp Web";
    public static final String URL_WEB = "https://web.whatsapp.com/";
    public static final String URL_TO_CHAT = "https://web.whatsapp.com/send?phone=";
    public static final String V = "V";
    public static final String VV = "VV";
    public static final int SIZE_BUTTON = 23;
    public static final int X_BUTTON = Window.WINDOW_WIDTH / 2 - 100;
    public static final int Y_BUTTON = Window.WINDOW_HEIGHT - 250;
    public static final int SIZE_TEXT_FIELD = 24;
    public static final int SIZE_TEXT = 18;
    public static final int LENGTH_TEN_DIGITS = 10;
    public static final int LENGTH_TWELVE_DIGITS = 12;
    public static final int SLEEP_TIME = 1000;

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
    private JLabel connectionLabel = new JLabel("Connection Completed Successfully!");
    private JPanel sendMessageSucceedPanel = new JPanel();
    private JLabel sendMessageLabel = new JLabel("The send succeeded!");

    private boolean tryToConnect = true;
    private boolean update = true, v1 = true, v2 = true;

    public MainScene() {
        this.setSize(Window.WINDOW_WIDTH, Window.WINDOW_HEIGHT);
        this.setLayout(null);
        this.setBackground(null);
        this.setDoubleBuffered(true);

        connectionSucceedPanel.setBounds(Window.WINDOW_WIDTH / 4, Window.WINDOW_HEIGHT / 2, 350, 50);
        connectionSucceedPanel.setBackground(Color.BLUE);
        connectionLabel.setBounds(connectionSucceedPanel.getX() + 50,
                connectionSucceedPanel.getY(),
                connectionSucceedPanel.getWidth(),
                connectionSucceedPanel.getHeight());
        connectionLabel.setFont(new Font("arial", Font.BOLD, 20));
        connectionLabel.setForeground(Color.YELLOW);
        connectionSucceedPanel.add(connectionLabel);
        connectionSucceedPanel.setVisible(false);
        this.add(connectionSucceedPanel);

        sendMessageSucceedPanel.setBounds(Window.WINDOW_WIDTH / 4, Window.WINDOW_HEIGHT / 2 + 50, 350, 50);
        sendMessageSucceedPanel.setBackground(Color.BLUE);
        sendMessageLabel.setBounds(sendMessageSucceedPanel.getX() + 50,
                sendMessageSucceedPanel.getY(),
                sendMessageSucceedPanel.getWidth(),
                sendMessageSucceedPanel.getHeight());
        sendMessageLabel.setFont(new Font("arial", Font.BOLD, 20));
        sendMessageLabel.setForeground(Color.YELLOW);
        sendMessageSucceedPanel.add(sendMessageLabel);
        sendMessageSucceedPanel.setVisible(false);
        this.add(sendMessageSucceedPanel);

        //WhatsappWeb button
        openWhatsappWebButton = new JButton(OPEN_WEB_BUTTON);
        openWhatsappWebButton.setBounds(X_BUTTON, Y_BUTTON, 200, 60);
        openWhatsappWebButton.setFont(new Font("arial", Font.BOLD, SIZE_BUTTON));
        openWhatsappWebButton.setVisible(true);
        this.add(openWhatsappWebButton);
        //ClickButton
        openWhatsappWebButton.addActionListener((event) -> {
            if (enterPhoneNumberTextField.getText().length() == 0) {
                JOptionPane.showConfirmDialog(frame, "Enter phone number please", "Error", JOptionPane.CLOSED_OPTION);
            } else {
                if (!checkPhoneNumberFormat(enterPhoneNumberTextField.getText())) {
                    JOptionPane.showConfirmDialog(frame, "The phone number is invalid!", "Error", JOptionPane.CLOSED_OPTION);
                } else {
                    if (messageToSendTextField.getText().length() == 0) {
                        JOptionPane.showConfirmDialog(frame, "Enter message please", "Error", JOptionPane.CLOSED_OPTION);
                    } else {
                        System.setProperty("webdriver.chrome.driver", "C:\\Users\\kedar\\IdeaProjects\\chromedriver_win32\\chromedriver.exe");
                        this.driver = new ChromeDriver();
                        driver.get(URL_WEB);
                        driver.manage().window().maximize();
                        if (tryConnect(driver)) {
                            connectionSucceedPanel.setVisible(true);
                            connectToPhoneNumber(enterPhoneNumberTextField.getText());
                        }
                        if (tryToSendMessage()) {
                            sendMessageSucceedPanel.setVisible(true);
                            //JOptionPane.showConfirmDialog(frame, "The send succeeded!", "Send Message", JOptionPane.CLOSED_OPTION);
                            contemporaryTime = LocalDateTime.now();
                            new Thread(() -> {
                                while (update) {
                                    if (checkV(this.driver, " נשלחה ", contemporaryTime) && v1) {
                                        statusTextField.setText(V);
                                        v1 = false;
                                    }
                                    if (checkV(this.driver, " נמסרה ", contemporaryTime) && v2) {
                                        statusTextField.setText(VV);
                                        v2 = false;
                                    }
                                    if (checkV(this.driver, " נקראה ", contemporaryTime) && update) {
                                        statusTextField.setText(VV);
                                        statusTextField.setDisabledTextColor(Color.BLUE);
                                        update = false;
                                    }
                                    /*while (true){
                                        if(statusTextField.getText().equals(VV) && statusTextField.getDisabledTextColor().equals(Color.BLUE)){
                                            try {
                                                System.out.println("no message");
                                                contemporaryTime = LocalDateTime.now();
                                                List<WebElement> messages = driver.findElements(By.cssSelector("span[class=\"22Msk\"]"));
                                                for (WebElement webElement: messages){
                                                    System.out.println("A: ");
                                                    WebElement messageElement = webElement.findElement(By.cssSelector("span[class=\"f804f6gw ln8gz9je\"]"));
                                                    System.out.println("M: " + messageElement.getText());
                                                    if(checkV(driver," נקראה ", contemporaryTime) && messageToSendTextField.getText() != messageElement.getText()){
                                                        System.out.println("have a new message");
                                                        break;
                                                    }
                                                }
                                                Thread.sleep(10000);
                                            }catch (InterruptedException e){
                                                e.printStackTrace();
                                            }
                                        }
                                    }*/
                                }

                            }).start();
                        }

                        /*try {
                            Thread.sleep(SLEEP_TIME);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        driver.close();*/
                    }
                }
            }


    });

    createUI(this);

    background =new ImageIcon(this.getClass().getResource("/whatsapp-web.jpg"));
    backgroundLabel =new JLabel(background);
    backgroundLabel.setSize(Window.WINDOW_WIDTH,Window.WINDOW_HEIGHT);
    this.add(backgroundLabel);

    this.setVisible(true);

}

    public static boolean checkV(ChromeDriver driver, String arialLabel, LocalDateTime time) {
        boolean check = false;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("H:mm");
        try {
            List<WebElement> messagesElements = driver.findElements(By.cssSelector("div[class=\"_22Msk\"]"));
            for (WebElement webElement : messagesElements) {
                WebElement timeElement = webElement.findElement(By.cssSelector("div[class=\"_1beEj\"]"));
                System.out.println("A: "+dateTimeFormatter.format(time));
                System.out.println("B: "+timeElement.getText());
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
                WebElement textBox = driver.findElement(By.cssSelector("div[title=\"הקלדת ההודעה\"]"));
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
        this.driver.get(URL_TO_CHAT + phoneNumberWithoutZero);
    }

    public static boolean checkPhoneNumberFormat(String phoneNumber) {
        boolean isValid = false;
        if (phoneNumber.length() == LENGTH_TEN_DIGITS) {
            if (phoneNumber.charAt(0) == '0') {
                if (phoneNumber.charAt(1) == '5') {
                    for (int i = 2; i < phoneNumber.length(); i++) {
                        if (Character.isDigit(phoneNumber.charAt(i))) {
                            isValid = true;
                        } else {
                            isValid = false;
                            break;
                        }
                    }
                }
            }
        } else if (phoneNumber.length() == LENGTH_TWELVE_DIGITS) {
            if (phoneNumber.charAt(0) == '9' && phoneNumber.charAt(1) == '7' && phoneNumber.charAt(2) == '2' && phoneNumber.charAt(3) == '5') {
                for (int i = 4; i < phoneNumber.length(); i++) {
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
        textField.setFont(new Font("arial", Font.BOLD, SIZE_TEXT_FIELD));
        textField.setForeground(Color.BLUE);
        textField.setBackground(Color.lightGray);
        textField.setVisible(true);

        return textField;
    }

    public JLabel createJLabel(String text, int x, int y, int width, int height) {
        JLabel jLabel = new JLabel(text);
        jLabel.setBounds(x, y, width, height);
        jLabel.setFont(new Font("arial", Font.BOLD, SIZE_TEXT));
        jLabel.setVisible(true);

        return jLabel;
    }

    public void createUI(JPanel panel) {

        //Phone number text
        enterPhoneNumberTextField = createTextField(X_BUTTON, Window.WINDOW_HEIGHT / 5, 200, 50);
        panel.add(enterPhoneNumberTextField);

        //TextField to enter phone number
        enterPhoneNumberText = createJLabel("Enter phone number: ", enterPhoneNumberTextField.getX(), enterPhoneNumberTextField.getY() - 50, 200, 50);
        panel.add(enterPhoneNumberText);

        //TextField to enter message
        messageToSendTextField = createTextField(X_BUTTON, Window.WINDOW_HEIGHT - 400, 200, 50);
        panel.add(messageToSendTextField);

        //Message text
        enterMessageText = createJLabel("Enter a message: ", messageToSendTextField.getX(), messageToSendTextField.getY() - 50, 200, 50);
        panel.add(enterMessageText);

        //Status message text
        statusMessage = createJLabel("Status: ", Window.WINDOW_WIDTH / 10, Window.WINDOW_HEIGHT / 3, 200, 50);
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
                driver.findElement(By.cssSelector("div[class=\"_1INL_ _1iyey A_WMk _1UG2S\"]"));
            } catch (Exception e) {
                tryConnect(driver);
            }
            tryToConnect = false;
            isConnect = true;
        }
        return isConnect;
    }


}

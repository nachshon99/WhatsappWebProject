import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import javax.swing.*;
import java.awt.*;
import java.util.List;


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

    private boolean tryToConnect = true;
    private boolean update = true, v1 = true, v2 = true;

    public MainScene() {
        this.setSize(Window.WINDOW_WIDTH, Window.WINDOW_HEIGHT);
        this.setLayout(null);
        this.setBackground(null);
        this.setDoubleBuffered(true);

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
                            connectToPhoneNumber(enterPhoneNumberTextField.getText());
                            JOptionPane.showConfirmDialog(frame, "Connection Completed Successfully!", "Status", JOptionPane.CLOSED_OPTION);
                        }
                        if (tryToSendMessage()) {
                            new Thread(() -> {
                                while (update) {
                                    if (checkV(this.driver, " נשלחה ", statusTextField) && v1) {
                                        statusTextField.setText(V);
                                        v1 = false;
                                    }
                                    if (checkV(this.driver, " נמסרה ", statusTextField) && v2) {
                                        statusTextField.setText(VV);
                                        v2 = false;
                                    }
                                    if (checkV(this.driver, " נקראה ", statusTextField) && update) {
                                        statusTextField.setDisabledTextColor(Color.BLUE);
                                        update = false;
                                    }
                                }
                            }).start();
                            JOptionPane.showConfirmDialog(frame, "The send succeeded!", "Send Message", JOptionPane.CLOSED_OPTION);
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

        background = new ImageIcon(this.getClass().getResource("/whatsapp-web.jpg"));
        backgroundLabel = new JLabel(background);
        backgroundLabel.setSize(Window.WINDOW_WIDTH, Window.WINDOW_HEIGHT);
        this.add(backgroundLabel);

        this.setVisible(true);
    }

    public static boolean checkV(ChromeDriver driver, String arialLabel, JTextField textField) {
        boolean isV = true;
        boolean check = false;
        try {
            while (isV) {
                List<WebElement> messages = driver.findElements(By.cssSelector("span[class=\"_1Gy50\"]"));
                for (WebElement element : messages) {
                    if (equals(textField,element.getText())) {
                        element.findElements(By.cssSelector("span[aria-label=\"" + arialLabel + "\"]"));
                        isV = false;
                        check = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            checkV(driver, arialLabel, textField);
        }
        return check;
    }

    public static boolean equals(JTextField textField, String text){
        boolean equal = false;
        for (int i = 0; i < textField.getText().length(); i++) {
            char currentChar = textField.getText().charAt(i);
            if(currentChar == text.charAt(i)){
                equal = true;
            }else {
                equal = false;
                break;
            }
        }
        return equal;
    }
    public boolean tryToSendMessage() {
        boolean succeed = false;
        try {
            WebElement textBox = driver.findElement(By.cssSelector("div[title=\"הקלדת ההודעה\"]"));
            textBox.sendKeys(messageToSendTextField.getText());
            textBox.sendKeys(Keys.ENTER);
            succeed = true;
        } catch (Exception e) {
        }
        return succeed;
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
                driver.findElement(By.cssSelector("div[class=\"_1ADa8 _3Nsgw app-wrapper-web font-fix os-win\"]"));
            } catch (Exception e) {
                tryConnect(driver);
            }
            tryToConnect = false;
            isConnect = true;
        }
        return isConnect;
    }


}

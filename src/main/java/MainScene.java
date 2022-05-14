import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import javax.swing.*;
import javax.swing.text.Element;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

public class MainScene extends JPanel {
    public static final String OPEN_WEB_BUTTON = "Whatsapp Web";
    public static final String URL_WEB = "https://web.whatsapp.com/";
    public static final int SIZE_BUTTON = 23;
    public static final int X_BUTTON = Window.WINDOW_WIDTH / 2 - 100;
    public static final int Y_BUTTON = Window.WINDOW_HEIGHT - 250;
    public static final int SIZE_TEXT_FIELD = 24;
    public static final int SIZE_TEXT = 18;
    public static final int LENGTH_TEN_DIGITS = 10;
    public static final int LENGTH_TWELVE_DIGITS = 12;
    private ChromeDriver driver;
    private JButton openWhatsappWebButton;
    private JTextField enterPhoneNumberTextField;
    private JTextField messageToSendTextField;
    private JFrame frame = new JFrame();
    private ImageIcon background;
    private JLabel backgroundLabel;
    private JLabel enterPhoneNumberText;
    private JLabel enterMessageText;

    private boolean tryToConnect = true;

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
                            frame.setSize(300, 100);
                            connectToPhoneNumber(enterPhoneNumberTextField.getText());
                            JOptionPane.showMessageDialog(frame, "Connection Completed Successfully!", "Status", JOptionPane.INFORMATION_MESSAGE);
                        }
                        WebElement textBox = driver.findElement(By.xpath("//*[@id=\"main\"]/footer/div[1]/div/span[2]/div/div[2]/div[1]/div/div[2]"));
                        textBox.sendKeys(messageToSendTextField.getText());
                        textBox.sendKeys(Keys.ENTER);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        driver.close();
                        JOptionPane.showConfirmDialog(frame, "The send succeeded!", "Send Message", JOptionPane.CLOSED_OPTION);

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

    public void connectToPhoneNumber(String phoneNumber) {
        String urlToChat = "https://web.whatsapp.com/send?phone=";
        String phoneNumberWithoutZero = phoneNumber.substring(1);
        this.driver.get(urlToChat + phoneNumberWithoutZero);
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
        //---------------------------------
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
        //---------------------------------

    }

    public boolean tryConnect(ChromeDriver driver) {
        boolean isConnect = false;
        while (tryToConnect) {
            try {
                driver.findElement(By.cssSelector("div[class=\"_1XkO3 two _22rDB\"]"));
            } catch (Exception e) {
                tryConnect(driver);
            }
            tryToConnect = false;
            isConnect = true;
        }
        return isConnect;
    }


}

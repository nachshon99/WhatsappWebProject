import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebElement;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class MainScene extends JPanel implements Runnable {



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
    private JLabel connectionLabel;
    private JPanel sendMessageSucceedPanel = new JPanel();
    private JLabel sendMessageLabel = new JLabel(Constants.SEND_MESSAGE_LABEL);
    private JPanel  messageDisplay=new JPanel();
    private WebElement lastMessage=new RemoteWebElement();
    private String status=" ";
    private boolean connectedToWeb;
    private boolean connectedToPhoneNumber;

    public MainScene() {

        this.setSize(Window.WINDOW_WIDTH, Window.WINDOW_HEIGHT);
        this.setLayout(null);
        this.setBackground(null);
        this.setDoubleBuffered(true);

        connectionSucceedPanel.setBounds(Constants.X_PANEL, Constants.Y_PANEL, Constants.WIDTH_PANEL, Constants.HEIGHT_PANEL);
        connectionSucceedPanel.setBackground(Color.BLUE);

        connectionLabel=createJLabel(
                Constants.CONNECTION_LABEL,
                connectionSucceedPanel.getX() + Constants.HEIGHT_PANEL,
                connectionSucceedPanel.getY(),
                connectionSucceedPanel.getWidth(),
                connectionSucceedPanel.getHeight(),
                false);

        connectionLabel.setForeground(Color.YELLOW);

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

        openWhatsappWebButton.addActionListener((event) ->
        {
            if (enterPhoneNumberTextField.getText().length() == Constants.INITIALIZE)
            {
                JOptionPane.showConfirmDialog(frame, Constants.ERROR_ENTER_PHONE, "Error", JOptionPane.CLOSED_OPTION);
            } else {
                if (!checkPhoneNumberFormat(enterPhoneNumberTextField.getText())) {
                    JOptionPane.showConfirmDialog(frame, Constants.ERROR_INVALID_NUMBER, "Error", JOptionPane.CLOSED_OPTION);
                } else {
                    if (messageToSendTextField.getText().length() == Constants.INITIALIZE) {
                        JOptionPane.showConfirmDialog(frame, Constants.ERROR_ENTER_MESSAGE, "Error", JOptionPane.CLOSED_OPTION);
                    } else {
                        initializeDriver();
                        openWhatsappWebButton.setEnabled(false);
                        tryToConnect();

                        if (this.connectedToWeb)
                        {

                            connectionSucceedPanel.setVisible(true);
                            connectionLabel.setVisible(true);
                            connectToPhoneNumber(enterPhoneNumberTextField.getText());
                        }

                       if (this.connectedToPhoneNumber)
                       {
                            startMainLoop();
                            tryToSendMessage();
                            sendMessageSucceedPanel.setVisible(true);
                            waitingForMessage();
                            checkV();
                        }


                        //in the end
                        //driver.close();
                    }
                }
            }
        });

        createUI();
        background =new ImageIcon(this.getClass().getResource(Constants.PATH_RESOURCE));
        backgroundLabel =new JLabel(background);
        backgroundLabel.setSize(Window.WINDOW_WIDTH,Window.WINDOW_HEIGHT);
        this.add(backgroundLabel);
        this.setVisible(true);
    }



    public void update()
    {
        if (this.connectedToPhoneNumber)
        {
            switch (this.status) {
                case Constants.STATUS_SENT -> {
                    this.statusTextField.setText(Constants.V);
                }
                case Constants.STATUS_DELIVERED -> {
                    this.statusTextField.setText(Constants.VV);
                    statusMessage.setText(Constants.VV);

                }
                case Constants.STATUS_READ -> {
                    this.statusTextField.setText(Constants.VV);
                    this.statusTextField.setDisabledTextColor(Color.blue);
                }
            }
            this.statusTextField.paintImmediately(this.statusMessage.getVisibleRect());
            this.repaint();
            getLastMessageFromMeHeight();
        }


    }






    public void initializeDriver()
    {
        System.setProperty(Constants.CHROME_DRIVER, Constants.PATH_TO_CHROME_DRIVER);
        this.driver = new ChromeDriver();
        this.driver.get(Constants.URL_WEB);
        this.driver.manage().window().fullscreen();
    }

    public void startMainLoop()
    {
        Thread mainThread=new Thread(this);
        mainThread.start();
    }

    public void run()
    {
        try {
            while (true) {
                Thread.sleep(5000);
                this.update();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public void waitingForMessage()
    {
        Thread tenSecondThread=new Thread(()->
        {
            boolean messagesAreIn=false;
            try {
                 while (!messagesAreIn&&this.lastMessage!=null) {
                     try {
                         Thread.sleep(1000);
                     } catch (InterruptedException ex) {
                         ex.printStackTrace();
                     }
                     if (this.lastMessage != null)
                     {
                         int lastMessageFromMeHeight = lastMessage.getLocation().getY();

                         List<WebElement> allClientsMessages = this.driver.findElements(By.cssSelector("div[class=\"_2wUmf _21bY5 message-in focusable-list-item\"]"));
                         allClientsMessages.addAll(this.driver.findElements(By.cssSelector("div[class=\"_2wUmf message-in focusable-list-item\"]")));
                         allClientsMessages.removeIf(element -> element.getLocation().getY() < lastMessageFromMeHeight);

                         if (!allClientsMessages.isEmpty()) {
                             displayLastMessages(allClientsMessages);
                             messagesAreIn = true;
                         }
                     }
                 }
            } catch (Exception ignored) {}
        });
        tenSecondThread.start();
    }

    public void displayLastMessages(List<WebElement> allLastMessagesFromClient)
    {
        this.connectionSucceedPanel.setVisible(false);
        this.connectionLabel.setVisible(false);
        this.sendMessageSucceedPanel.setVisible(false);

        while (!allLastMessagesFromClient.isEmpty())
        {
            JTextArea clientsMessages=new JTextArea(
                    allLastMessagesFromClient.get(0).getText(),
                    6,
                    20
            );
            clientsMessages.setFont(new Font("Serif", Font.LAYOUT_LEFT_TO_RIGHT, 16));
            clientsMessages.setLineWrap(true);
            clientsMessages.setWrapStyleWord(true);
            clientsMessages.setOpaque(false);
            clientsMessages.setEditable(false);
            this.messageDisplay.add(clientsMessages);
            allLastMessagesFromClient.remove(0);
        }
        this.messageDisplay.setVisible(true);
        this.driver.close();
    }

    public void getLastMessageFromMeHeight()
    {
        boolean gotLastMessage= false;
        WebElement lastMessageInFirstFormat;
        WebElement lastMessageInSecondFormat;
        while (!gotLastMessage)
        {
            try {
                List<WebElement> allMessageFromMeFirstFormat = this.driver.findElements(By.cssSelector("div[class=\"_2wUmf message-out focusable-list-item\"]"));//first format to find users messages
                List<WebElement>allMessageFromMeSecondFormat=this.driver.findElements(By.cssSelector("div[class=\"_2wUmf _21bY5 message-out focusable-list-item\"]")); //second format to find users messages
                if (allMessageFromMeFirstFormat.isEmpty())
                {
                    this.lastMessage=allMessageFromMeSecondFormat.get(allMessageFromMeSecondFormat.size()-1);
                    gotLastMessage=true;
                }
                if (allMessageFromMeSecondFormat.isEmpty())
                {
                    this.lastMessage=allMessageFromMeFirstFormat.get(allMessageFromMeFirstFormat.size()-1);
                    gotLastMessage=true;
                }

                if (!allMessageFromMeFirstFormat.isEmpty()&&!allMessageFromMeSecondFormat.isEmpty())
                {
                    lastMessageInFirstFormat=allMessageFromMeFirstFormat.get(allMessageFromMeFirstFormat.size()-1);
                    lastMessageInSecondFormat=allMessageFromMeSecondFormat.get(allMessageFromMeSecondFormat.size()-1);

                    if(lastMessageInFirstFormat.getLocation().getY()>lastMessageInSecondFormat.getLocation().getY())
                    {
                        this.lastMessage=lastMessageInFirstFormat;
                    }else
                    {
                        this.lastMessage=lastMessageInSecondFormat;
                    }
                    gotLastMessage=true;
                }

            }catch (Exception ignored) {}
        }
        System.out.println(this.lastMessage.getText());
    }

    public void checkV()
    {

        while (!this.status.equals(Constants.STATUS_READ))
        {
            try {
                checkTypeOfV(Constants.STATUS_SENT);
                checkTypeOfV(Constants.STATUS_DELIVERED);
                checkTypeOfV(Constants.STATUS_READ);
            } catch (Exception ignored) {}
        }
    }

    public void checkTypeOfV(String status)
    {
        try{
            if (status.equals(this.lastMessage.findElement(By.cssSelector("span[aria-label=\"" + status + "\"]")).getAttribute("aria-label")))
            {
                System.out.println(this.lastMessage.findElement(By.cssSelector("span[aria-label=\"" + status + "\"]")).getAttribute("aria-label"));
                System.out.println(this.lastMessage.getText());
                this.status=status;

            }
        }catch (Exception ignored) {}
    }


    public void tryToSendMessage()
    {

        while (true)
          {
          try {
             WebElement textBox=driver.findElement(By.cssSelector(Constants.CSS_SELECTOR_TRY_SEND_MESSAGE));
             textBox.sendKeys(messageToSendTextField.getText());
             textBox.sendKeys(Keys.ENTER);
               break;
            }catch (Exception ignored) {}
          }
    }

    public void connectToPhoneNumber(String phoneNumber)
    {
        String phoneNumberWithoutZero = phoneNumber.substring(1);
        this.driver.get(Constants.URL_TO_CHAT + phoneNumberWithoutZero);
        this.connectedToPhoneNumber=true;
    }

    public static boolean checkPhoneNumberFormat(String phoneNumber)
    {
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

    public JTextField createTextField(int x, int y, int width, int height)
    {
        JTextField textField = new JTextField();
        textField.setBounds(x, y, width, height);
        textField.setFont(new Font("arial", Font.BOLD, Constants.SIZE_TEXT_FIELD));
        textField.setForeground(Color.BLUE);
        textField.setBackground(Color.lightGray);
        textField.setVisible(true);
        this.add(textField);
        return textField;
    }

    public JLabel createJLabel(String text, int x, int y, int width, int height,boolean visible)
    {
        JLabel jLabel = new JLabel(text);
        jLabel.setBounds(x, y, width, height);
        jLabel.setFont(new Font("arial", Font.BOLD, Constants.SIZE_TEXT));
        jLabel.setVisible(visible);
        this.add(jLabel);
        return jLabel;
    }


    public void createUI()
    {
        //

        this.messageDisplay.setBounds(455,this.getY(),this.getWidth()/3,this.getHeight());
        this.messageDisplay.setBackground(Color.WHITE);
        this.messageDisplay.setVisible(false);
        this.add(messageDisplay);

        //Phone number text
        this.enterPhoneNumberTextField = createTextField(Constants.X_BUTTON,
                Window.WINDOW_HEIGHT / 5,
                Constants.WIDTH_TEXT_FIELD,
                Constants.HEIGHT_PANEL);

        //TextField to enter phone number
        this.enterPhoneNumberText = createJLabel("Enter phone number: ",
                this.enterPhoneNumberTextField.getX(),
                this.enterPhoneNumberTextField.getY() - Constants.HEIGHT_PANEL,
                Constants.WIDTH_TEXT_FIELD, Constants.HEIGHT_PANEL,
                true);


        //TextField to enter message
        this.messageToSendTextField = createTextField(Constants.X_BUTTON,
                Window.WINDOW_HEIGHT - 400,
                Constants.WIDTH_TEXT_FIELD,
                Constants.HEIGHT_PANEL);

        //Message text
        this.enterMessageText = createJLabel(
                "Enter a message: ",
                this.messageToSendTextField.getX(),
                this.messageToSendTextField.getY() - Constants.HEIGHT_PANEL,
                Constants.WIDTH_TEXT_FIELD,
                Constants.HEIGHT_PANEL,
                true);


        //Status message text
        this.statusMessage = createJLabel("Status: ",
                Window.WINDOW_WIDTH / 10,
                Window.WINDOW_HEIGHT / 3,
                Constants.WIDTH_TEXT_FIELD/2,
                Constants.HEIGHT_PANEL,true);
        this.statusMessage.setForeground(Color.BLACK);
        this.statusMessage.setBackground(Color.green);
        this.statusMessage.setOpaque(true);


        //Status Text Field
        this.statusTextField = createTextField(statusMessage.getX(), statusMessage.getY() + 40, statusMessage.getHeight(), statusMessage.getHeight());
        this.statusTextField.setBackground(Color.BLACK);
        this.statusTextField.setEnabled(false);
        this.statusTextField.setDisabledTextColor(Color.gray);
        this.statusTextField.setText("nothing");
    }

    public void tryToConnect()
    {
        while (true)
        {
            try {
                this.driver.findElement(By.cssSelector(Constants.CSS_SELECTOR_TRY_CONNECT));
                break;
            } catch (Exception ignored) {}
        }
        this.connectedToWeb = true;
    }

}

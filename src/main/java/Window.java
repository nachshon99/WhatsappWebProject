import javax.swing.*;

public class Window extends JFrame {
    public static void main(String[] args) {
        Window window = new Window();
    }

    public static final int WINDOW_WIDTH = 700;
    public static final int WINDOW_HEIGHT = 700;


    public Window(){
        this.setSize(WINDOW_WIDTH,WINDOW_HEIGHT);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        MainScene mainScene = new MainScene();
        this.add(mainScene);
        this.setTitle("Whatsapp Web");
        this.setVisible(true);
    }
}

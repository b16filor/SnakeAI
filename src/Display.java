import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Display extends JPanel implements KeyListener, Runnable{
    private static JFrame frame;
    private int mapSize = 30, highScore, totalScore, tries;
    private Snake snake;
    private AILearning ai;
    private final boolean autoReset = true;
    public static void main(String[] args){
        frame = new JFrame("SnakeAI");
        frame.setSize(720, 720);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new Display());
        frame.setVisible(true);
    }

    public Display(){
        highScore = totalScore = tries = 0;
        snake = new Snake(15, 15, 30);
        ai = new AILearning(snake, mapSize);
        addKeyListener(this);
        setFocusable(true);
        Thread resetThread = new Thread(this);
        resetThread.start();
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.setColor(Color.black);
        for(int i = 0; i < mapSize; i++)
            for(int j = 0; j < mapSize; j++)
                g.drawRect(i * 20 + 51, j * 20 + 60, 20, 20);
        g.setColor(Color.BLUE);
        g.fillRect(31, 40, 20, 640);
        g.fillRect(652, 40, 20, 640);
        g.fillRect(51, 40, 601, 20);
        g.fillRect(51, 661, 601, 20);
        g.setColor(Color.red);
        g.setFont(g.getFont().deriveFont(0, 20));
        g.drawString("Score: " + snake.getScore(), 500, 30);
        g.drawString("Nr: " + (tries + 1), 0, 30);
        g.drawString("Avg score: " + (totalScore / (tries == 0 ? 1 : tries)), 300, 30);
        g.drawString("Highscore: " + highScore, 100, 30);
        snake.drawSnake(g);
        snake.drawFruit(g);
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        repaint();
    }

    @Override
    public void run() {
        while (autoReset){
            if(!snake.isAlive() && ai.isDeathChangesMade() && tries < 35)
                reset();
        }
    }

    private void reset(){
        highScore = snake.getScore() > highScore ? snake.getScore() : highScore;
        totalScore += snake.getScore();
        tries++;
        System.out.println("Score: " + snake.getScore());
        ai.setSnake(snake = new Snake(15, 15, mapSize));
    }


    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_A)
            snake.setDirection(Snake.Direction.LEFT);
        else if(e.getKeyCode() == KeyEvent.VK_D)
            snake.setDirection(Snake.Direction.RIGHT);
        else if(e.getKeyCode() == KeyEvent.VK_W)
            snake.setDirection(Snake.Direction.UP);
        else if(e.getKeyCode() == KeyEvent.VK_S)
            snake.setDirection(Snake.Direction.DOWN);
        if(e.getKeyCode() == KeyEvent.VK_R)
            reset();
        if(e.getKeyCode() == KeyEvent.VK_C)
            ai.setSnake(null);
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}

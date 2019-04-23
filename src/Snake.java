import java.awt.*;
import java.util.ArrayList;

public class Snake implements Runnable{
    private final int scorePerFruit = 50;
    private int mapSize, score, movesSinceLastFruit;
    private ArrayList<Point> body;
    public enum Direction{UP, RIGHT, DOWN, LEFT}
    private Direction direction, lastDir;
    private boolean alive, AIUpdated;
    private Point fruit;

    public Snake(int x, int y, int size){
        body = new ArrayList<>();
        mapSize = size;
        body.add(new Point(x, y));
        direction = Direction.LEFT;
        alive = true;
        fruit = new Point((int)(Math.random() * mapSize), (int)(Math.random() * mapSize));
        score = 0;
        AIUpdated = false;
        movesSinceLastFruit = 10;
        Thread update = new Thread(this);
        update.start();
    }

    @Override
    public void run() {
        while(alive){
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            moveSnake();
            alive = !isDead();
            if(hitFruit())
                movesSinceLastFruit = 0;
            else
                movesSinceLastFruit++;
            AIUpdated = true;
        }
    }

    public void drawSnake(Graphics g){
        g.setColor(Color.GREEN);
        for(int i = 0; i < body.size(); i++)
            g.fillRect(body.get(i).x * 20 + 52, body.get(i).y * 20 + 61, 19, 19);
    }

    public void drawFruit(Graphics g){
        g.setColor(Color.red);
        g.fillRect(fruit.x * 20 + 52, fruit.y * 20 + 61, 19, 19);
    }

    private void moveSnake(){
        Point pos = body.get(0).getLocation();
        if(direction == Direction.LEFT)
            pos.x--;
        else if(direction == Direction.RIGHT)
            pos.x++;
        else if(direction == Direction.UP)
            pos.y--;
        else if(direction == Direction.DOWN)
            pos.y++;
        lastDir = direction;
        moveBody(1);
        body.get(0).setLocation(pos);
    }

    private void moveBody(int i){
        if(i == body.size())
            return;
        moveBody(i + 1);
        body.get(i).setLocation(body.get(i - 1));
    }

    private boolean isDead(){
        if(hitObject(body.get(0))) {
            return true;
        }
        return false;
    }

    public boolean hitObject(Point object){
        for(int i = 1; i < body.size(); i++)
            if(object.equals(body.get(i)))
                return true;
        if(object.x < 0 || object.y < 0 || object.x >= mapSize || object.y >= mapSize)
            return true;
        return false;
    }

    private boolean hitFruit(){
        if(body.get(0).equals(fruit)){
            fruit = new Point((int)(Math.random() * 30), (int)(Math.random() * 30));
            body.add(new Point(body.get(body.size() - 1).getLocation()));
            score += scorePerFruit;
            return true;
        }
        return false;
    }

    public Direction getDirection(int dir){
        if(dir == Direction.LEFT.ordinal())
            return Direction.LEFT;
        if(dir == Direction.RIGHT.ordinal())
            return Direction.RIGHT;
        if(dir == Direction.UP.ordinal())
            return Direction.UP;
        if(dir == Direction.DOWN.ordinal())
            return Direction.DOWN;
        return null;
    }

    public boolean setDirection(Direction dir){
        if((dir.ordinal() + 2) % 4 != lastDir.ordinal()) {
            direction = dir;
            return true;
        }
        return false;
    }

    public void setAIUpdated(boolean updated) {
        AIUpdated = updated;
    }

    public int getScore(){
        return score;
    }

    public synchronized boolean isAlive(){
        return alive;
    }

    public Point getFruit(){
        return fruit;
    }

    public ArrayList<Point> getBody() {
        return body;
    }

    public boolean isAIUpdated() {
        if(AIUpdated){
            AIUpdated = false;
            return true;
        }
        return false;
    }

    public Direction getLastDir() {
        return lastDir;
    }

    public int getMovesSinceLastFruit() {
        return movesSinceLastFruit;
    }
}

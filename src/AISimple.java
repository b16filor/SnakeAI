public class AISimple implements Runnable{
    private Snake snake;
    private int mapSize;
    private boolean deathChangesMade = true;
    public AISimple(Snake snake, int size){
        this.snake = snake;
        mapSize = size;
        Thread update = new Thread(this);
        update.start();
    }

    @Override
    public void run() {
        while (true){
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(snake.isAlive() && snake.isAIUpdated()){
                snake.setDirection(snake.getDirection((int)(Math.random() * 4)));
            }
        }
    }

    public void setSnake(Snake snake){
        this.snake = snake;
    }

    public boolean isDeathChangesMade() {
        return deathChangesMade;
    }
}

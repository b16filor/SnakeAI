import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class AILearning implements Runnable{
    private Snake snake;
    private int mapSize, straightCounter;
    private boolean deathChangesMade;
    private ArrayList<Boolean[]> deathTurns; //The last turns before death right = true and left = false
    private Boolean[] lastTurns;
    private int[] nextDeathTurn;
    private final int nrOfDeathTurns = 5;
    private final String fileName = "deathTurns2.txt";
    public AILearning(Snake snake, int size){
        this.snake = snake;
        mapSize = size;
        deathChangesMade = false;
        deathTurns = new ArrayList<>();
        lastTurns = new Boolean[nrOfDeathTurns];
        nextDeathTurn = new int[4];
        straightCounter = 0;
        readDeathTurns();
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
                deathChangesMade = false;
                Point head = snake.getBody().get(0);
                // Add the possbile directions to a list
                ArrayList<Snake.Direction> possibleDirections = new ArrayList<>();
                if(snake.setDirection(Snake.Direction.LEFT) && !snake.hitObject(new Point(head.x - 1, head.y)))
                    possibleDirections.add(Snake.Direction.LEFT);
                if(snake.setDirection(Snake.Direction.RIGHT) && !snake.hitObject(new Point(head.x + 1, head.y)))
                    possibleDirections.add(Snake.Direction.RIGHT);
                if(snake.setDirection(Snake.Direction.UP) && !snake.hitObject(new Point(head.x, head.y - 1)))
                    possibleDirections.add(Snake.Direction.UP);
                if(snake.setDirection(Snake.Direction.DOWN) && !snake.hitObject(new Point(head.x, head.y + 1)))
                    possibleDirections.add(Snake.Direction.DOWN);


                pickDirection(possibleDirections);

                possibleDirections.clear();
            }else if (!snake.isAlive() && !deathChangesMade){
                // Save last turns before death
                Boolean[] lastTurnsCopy = new Boolean[lastTurns.length];
                for(int i = 0; i < lastTurns.length; i++)
                    lastTurnsCopy[i] = lastTurns[i];
                deathTurns.add(lastTurnsCopy);
                lastTurns = new Boolean[nrOfDeathTurns];
                writeDeathTurns();
                deathChangesMade = true;
            }
        }
    }

    private void pickDirection(ArrayList<Snake.Direction> directions){
        compareTurns();
        Point head = snake.getBody().get(0);
        int[] points = new int[4];
        for(int i = 0;  i < 4; i++){
            //Add points
            if((i == Snake.Direction.LEFT.ordinal() && snake.getFruit().x < head.x) || (i == Snake.Direction.RIGHT.ordinal() && snake.getFruit().x > head.x) ||
                    (i == Snake.Direction.UP.ordinal() && snake.getFruit().y < head.y) ||(i == Snake.Direction.DOWN.ordinal() && snake.getFruit().y > head.y)) {
                points[i] += snake.getMovesSinceLastFruit();
            }
            if((i == Snake.Direction.LEFT.ordinal() && snake.getFruit().x > head.x) || (i == Snake.Direction.RIGHT.ordinal() && snake.getFruit().x < head.x) ||
                    (i == Snake.Direction.UP.ordinal() && snake.getFruit().y > head.y) ||(i == Snake.Direction.DOWN.ordinal() && snake.getFruit().y < head.y)) {
                points[i] -= 1; // Prevents snake from moving straight away from the fruit
            }
            if(i == snake.getLastDir().ordinal() && straightCounter > mapSize - 5)
                points[i] -= straightCounter;

            points[i] -= nextDeathTurn[i];

            for(int j = 0; j < directions.size(); j++)
                if(directions.get(j).ordinal() == i) {
                    points[i] += 1000;
                }
        }
        int mostPointsDir = 0;
        for(int i = 0; i < points.length; i++) {
            mostPointsDir = points[i] > points[mostPointsDir] ? i : mostPointsDir;
        }
        if(snake.getLastDir().ordinal() == mostPointsDir)
            straightCounter++;
        else
            straightCounter = 0;
        snake.setDirection(snake.getDirection(mostPointsDir));
        addToDeathTurns(snake.getDirection(mostPointsDir));
    }

    private void compareTurns(){
        if(deathTurns.size() == 0)
            return;
        String compareString = "";
        for(int i = 0; i < lastTurns.length; i++){
            if(lastTurns[i] != null)
                compareString = compareString + (lastTurns[i] ? "1" : "0");
        }
        int[] counters = new int[4];
        nextDeathTurn = new int[4];
        // Compares last turns with saved turns
        for(int i = 0; i < deathTurns.size(); i++){
            String deathString = "";
            int currentMatches;
            int next = 0;
            for(int j = 0; j < deathTurns.get(i).length; j++)
                    deathString = deathString + (deathTurns.get(i)[j] ? "1" : "0");
            int maxMatches = 0;
            for(int j = 0; j < deathTurns.get(i).length; j++){
                currentMatches = 0;
                for(int k = j + 1; k < deathTurns.get(i).length; k++){
                    String subString = deathString.substring(j, k);
                    if(compareString.contains(subString)) {
                        currentMatches++;
                    }else {
                        break;
                    }
                }
                if(currentMatches > maxMatches && currentMatches != lastTurns.length) {
                    next = j - 1;
                    maxMatches = currentMatches;
                }
            }

            if(next >= 0 ) {
                // +1 right, -1 left
                int index = (snake.getLastDir().ordinal() + (deathTurns.get(i)[next] ? 1 : -1)) % 4;
                if (index == -1)
                    index = 3;
                counters[index]++;
                nextDeathTurn[index] += Math.pow(maxMatches, 2);
            }
        }
        // Calculate average
        for(int i = 0; i < nextDeathTurn.length; i++) {
            nextDeathTurn[i] /= (counters[i] == 0 ? 1 : counters[i]);
        }
        System.out.println();
    }

    private void addToDeathTurns(Snake.Direction dir){
        // Add last turn to list
        if(dir.ordinal() % 2 == snake.getLastDir().ordinal() % 2)//doesn't turn
            return;
        Boolean turn;
        turn = (snake.getLastDir().ordinal() + 1) % 4 == dir.ordinal();
        pushArray(lastTurns, 0);
        lastTurns[0] = turn;
    }

    private void pushArray(Boolean[] array, int i){
        // Recursive function that pushes the array one step
        if(i < array.length - 2)
            pushArray(array, i + 1);
        array[i + 1] = array[i];
    }

    private void writeDeathTurns(){
        // Saves turns to file
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(fileName, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            for(int i = 0; i < deathTurns.size(); i++) {
                for (int j = 0; j < deathTurns.get(i).length; j++)
                    writer.append(deathTurns.get(i)[j] ? "1" : "0");
                writer.append("\n");
            }
        }

        writer.close();
    }

    private void readDeathTurns(){
        // Reads turns from file
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            try {
                String input;
                while ((input = reader.readLine()) != null && input != ""){
                    Boolean[] turns = new Boolean[lastTurns.length];
                    for(int i = 0; i < input.length(); i++)
                        turns[i] = input.charAt(i) == '1';
                    deathTurns.add(turns);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(deathTurns.size());
    }

    public void setSnake(Snake snake){
        this.snake = snake;
    }

    public boolean isDeathChangesMade() {
        return deathChangesMade;
    }
}


package casino;

import java.util.Random;

public class SlotMachine{
    private Random random;
    public SlotMachine() {
        random = new Random(1); // Using a fixed seed to make a better test case
    }
    public void insertCoin(){
    }
    public void push(){
    }
    public Integer getResult(){
        return new Integer((int)(100.0 * random.nextDouble()));
    }
}


import java.util.List;

/**
 * Created by tommy on 2016/2/15.
 */
public class Signal {
    public static final Signal dao = new Signal();


    String name;
    int direction;// 0 -> DOWN    1 -> UP
    int position=1;//0 -> stop    1 -> go
    String currentBlock;//in which block
    String controllBlock;
    String next;//next signal(s)

    public Signal() {
    }

    public Signal(String name, int direction, String currentBlock , String controllBlock,String next) {
        this.name = name;
        this.direction = direction;
        this.controllBlock = controllBlock;
        this.currentBlock = currentBlock;
        this.next = next;
    }

    /**
     * get route by its name
     *
     * */
    public Signal getByName(List<Signal> signals,String name){
        Signal s = null;
        for (int i = 0; i < signals.size(); i++) {
            Signal signal = signals.get(i);
            if (signal.getName().equals(name)){
                s=signal;
                break;
            }
        }
        return s;
    }

    public String getControllBlock() {
        return controllBlock;
    }

    public void setControllBlock(String controllBlock) {
        this.controllBlock = controllBlock;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getCurrentBlock() {
        return currentBlock;
    }

    public void setCurrentBlock(String currentBlock) {
        this.currentBlock = currentBlock;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

}

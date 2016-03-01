
/**
 * Created by tommy on 2016/2/15.
 */
public class Block{
    String name;
//    int type;// 0 -> block     1 -> point
    int type;//1->no left neigh  2->right neigh is point     3->MINUS block between points  4->PLUS block between points  5->left neigh is point  6->no right neigh   11-> 1-2 one left neigh point   21-> 2-1 one right neigh point
    int position;// 0 -> PLUS    1 -> MINUS   3 -> npt point
    String net;//branch id  n1
    String previous;
    String next;
    String occupy="";//occupy by which journeyId   j1,j2

    public Block(String name, int type, String previous, String next, String net) {
        this.name = name;
        this.type = type;
        this.previous = previous;
        this.next = next;
        this.net = net;
//        this.occupy="";
    }

    public Block() {
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getNet() {
        return net;
    }

    public void setNet(String net) {
        this.net = net;
    }

    public String getOccupy() {
        return occupy;
    }

    public void setOccupy(String occupy) {
        this.occupy = occupy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}

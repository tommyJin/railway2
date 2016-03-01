/**
 * CSC8110 Cloud Computing Coursework
 * Created by Yiwei Jing on 01/03/2016 20:00
 * This is my assessment
 * Project name is railway2
 */
public class Path {
    String current;
    String next;

    public Path(String current, String next) {
        this.current = current;
        this.next = next;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }
}

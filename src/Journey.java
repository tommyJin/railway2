import java.util.ArrayList;
import java.util.List;

/**
 * Created by tommy on 2016/2/28.
 */
public class Journey {
    String id;
    int state;//0->waiting   1->running   2->end
    String source;// source signal
    String dest;//dest signal
    String currentRoute;//current route
    String currentBlock;//current block
    List<Route> routes = new ArrayList<>();

    public Journey(String id, String source, String dest) {
        this.id = id;
        this.state = 0;
        this.source = source;
        this.dest = dest;
    }

    public String getCurrentRoute() {
        return currentRoute;
    }

    public void setCurrentRoute(String currentRoute) {
        this.currentRoute = currentRoute;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public String getCurrentBlock() {
        return currentBlock;
    }

    public void setCurrentBlock(String currentBlock) {
        this.currentBlock = currentBlock;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

}


import java.util.List;

/**
 * Created by tommy on 2016/2/15.
 */
public class Route {
    public static final Route dao = new Route();

    String id;
    String source;//s1
    String dest;//s7
    String points;
    String signals;
    String path;
    String conflicts;
    int direction;//0->DOWN   1->UP

    public Route(){

    }

    public Route(String id,String source,String dest,int direction){
        this.id = id;
        this.source = source;
        this.dest = dest;
//        this.points = points;
//        this.signals = signals;
//        this.path = path;
        this.direction = direction;
    }

    /**
     * get route by its id
     *
     * */
    public Route getById(List<Route> routes,String id){
        Route r = new Route();
        for (int i = 0; i < routes.size(); i++) {
            Route route = routes.get(i);
            if (route.getId().equals(id)){
                r=route;
                break;
            }
        }
        return r;
    }

    /**
     * get route By Source And Dest
     * */
    public Route getBySourceAndDest(List<Route> routes,String source,String dest){
        Route r = new Route();
        for (int i = 0; i < routes.size(); i++) {
            Route route = routes.get(i);
            if (route.getSource().equals(source) && route.getDest().equals(dest)){
                r = route;
                break;
            }
        }
        return r;
    }


    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getSignals() {
        return signals;
    }

    public void setSignals(String signals) {
        this.signals = signals;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getConflicts() {
        return conflicts;
    }

    public void setConflicts(String conflicts) {
        this.conflicts = conflicts;
    }
}

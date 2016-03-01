import java.util.ArrayList;
import java.util.List;

/**
 * Created by tommy on 2016/2/18.
 */
public class Railway {
    List<Signal> signals;
    List<Signal> upSignals = new ArrayList<>();
    List<Signal> downSignals = new ArrayList<>();
    List<Block> blocks;
    List<Route> routes;
    List<Route> upRoutes = new ArrayList<>();
    List<Route> downRoutes = new ArrayList<>();
    List<Journey> journeys = new ArrayList<>();


    List<Path> paths = new ArrayList<>();

    public Railway() {

        JsonFile jf = new JsonFile();
        this.signals = jf.getSignal();
        this.blocks = jf.getBlock();
        this.routes = jf.getRoute();
        this.paths = getPaths();


        for (int i = 0; i < this.paths.size(); i++) {
//            System.out.println("Path current:"+this.paths.get(i).getCurrent()+"  next:"+this.paths.get(i).getNext());
        }

        for (int i = 0; i < this.routes.size(); i++) {
            Route route = Route.dao.getById(this.routes, this.routes.get(i).getId());
            if (route.getDirection() == 1) {//signal direction=0->down  1->up
                this.upRoutes.add(route);
            } else {
                this.downRoutes.add(route);
            }
        }

        for (int i = 0; i < this.signals.size(); i++) {
            Signal signal = Signal.dao.getByName(this.signals, this.signals.get(i).getName());
            if (signal.getDirection() == 1) {//signal direction=0->down  1->up
                this.upSignals.add(signal);
            } else {
                this.downSignals.add(signal);
            }
        }

    }

    public List<Path> getPaths(){
        List<Path> paths = new ArrayList<>();

        List<Block> blocks = this.blocks;

        for (int i = 0; i < blocks.size(); i++) {
            Block block = blocks.get(i);


            String next = block.getNext();
            String[] nextBlocks;
            if (!next.equals("") && next.contains(";")){
                nextBlocks = next.split(";");
            }else{
                nextBlocks = new String[1];
                nextBlocks[0] = next;
            }

            for (int j = 0; j < nextBlocks.length; j++) {
                if (!nextBlocks[j].equals("")) {
                    Path path = new Path(block.getName(), nextBlocks[j]);
                    paths.add(path);
                }
            }
        }



        return paths;
    }


    public void addJourney(String journeyId, String source, String dest, String passby) {
//        System.out.println("#####################  Adding journeys begins! ####################");
        Journey journey = new Journey(journeyId, source, dest);
        String[] passbys = passby.split(";");
        for (int i = 1; i < passbys.length; i++) {
//            System.out.println("Source:" + passbys[i - 1] + "  dest:" + passbys[i]);

            Route route = Route.dao.getBySourceAndDest(this.routes, passbys[i - 1], passbys[i]);
            journey.getRoutes().add(route);
        }

        String routeId = journey.getRoutes().get(0).getId();//get the first route id
        journey.setCurrentRoute(routeId);//set the current route id
        Route route = Route.dao.getById(this.routes, routeId);//get the route by its id
        Signal signal = Signal.dao.getByName(this.signals, route.getSource());//get the signal by route source
        journey.setCurrentBlock(signal.getCurrentBlock());//set the journey current block by signal's current
//        System.out.println("Journey current route is " + journey.getCurrentRoute() + " and block is " + journey.getCurrentBlock());

        this.journeys.add(journey);
//        System.out.println("#####################  Adding journeys ends! ####################\n");
    }

    public void checkWaitingList() {
        System.out.println("#####################  Checking waiting journeys begins! ####################");
        for (int i = 0; i < this.journeys.size(); i++) {
            Journey waiting = this.journeys.get(i);
            if (waiting.getState() == 0) {//if this journey is waiting
                boolean flag = lock(waiting, waiting.getCurrentRoute());
                for (int j = 0; j < this.signals.size(); j++) {
                    System.out.println("Signal " + this.signals.get(j).getName() + "  :  " + this.signals.get(j).getPosition());
                }
                for (int j = 0; j < this.blocks.size(); j++) {
                    if (this.blocks.get(j).getType() == 1) {//point
                        System.out.println("Point " + this.blocks.get(j).getName() + "  :  " + this.blocks.get(j).getOccupy() + "  position: " + (this.blocks.get(j).getPosition() == 0 ? "PLUS" : "MINUS"));
                    } else {
                        System.out.println("Block " + this.blocks.get(j).getName() + "  :  " + this.blocks.get(j).getOccupy());
                    }
                }
                if (flag) {
                    this.journeys.get(i).setState(1);

                    Route route = Route.dao.getById(this.routes,waiting.getCurrentRoute());

                    System.out.println("Journey " + this.journeys.get(i).getId() + " satisfies all conditions and set to " + this.journeys.get(i).getState() + " and lock all signals in this route");

                    String currentRoute = waiting.getCurrentRoute();


                }
            }
        }

        System.out.println("#####################  Checking waiting journeys ends! ####################\n");
    }

    public void runFreely() {
        System.out.println("#####################  Checking running journeys begins! ####################");
        List<Journey> journeys = this.journeys;


        for (int i = 0; i < journeys.size(); i++) {
            Journey j = journeys.get(i);

            String id = j.getId();
            String currentRoute = j.getCurrentRoute();//the route which the train is on
            String currentBlock = j.getCurrentBlock();//the block which the train is on
            int state = j.getState();//current state of the journey

            if (state == 1) {//only the state is running could run
                System.out.println("Journey " + id + " starts running!");
                boolean addRouteFlag = false;
                List<Route> routes = new ArrayList<>();// the rest routes to run for this journey
                for (int k = 0; k < j.getRoutes().size(); k++) {
                    if (j.getRoutes().get(k).getId().equals(currentRoute)) {
                        addRouteFlag = true;
                    }

                    if (addRouteFlag) {
                        routes.add(j.getRoutes().get(k));
                    }
                }

                Route route = routes.get(0);
                String paths = route.getPath();
                String[] path = paths.split(";");

                boolean isInArray = false;
                for (int k = 0; k < path.length; k++) {
//                    System.out.println("k[" + k + "]=" + path[k] + " and current block is " + currentBlock);
                    if (path[k].equals(currentBlock)){
                        isInArray = true;
                    }
                    if (k==path.length-1 && !isInArray){
                        this.journeys.get(i).setCurrentBlock(path[0]);//set the current block of this journey
                        System.out.println("Journey " + this.journeys.get(i).getId() + " moves from block " + currentBlock + " to " + this.journeys.get(i).getCurrentBlock() + " and release the block " + currentBlock);
                        releaseBlock(currentBlock);//release the passed block
                        break;
                    }else {
                        if (path[k].equals(currentBlock)) {// if the train is on this block
                            if (k < path.length - 1) {//there are more than one block to go in this route
                                this.journeys.get(i).setCurrentBlock(path[k+1]);//set the current block of this journey
                                System.out.println("Journey " + this.journeys.get(i).getId() + " moves from block " + currentBlock + " to " + this.journeys.get(i).getCurrentBlock() + " and release the block " + currentBlock);
                                releaseBlock(currentBlock);//release the passed block
                            } else {
                                this.journeys.get(i).setCurrentBlock(path[k]);//set the current block of this journey
                                if (routes.size() > 1) {//there are more than one route left to run in this journey
                                    this.journeys.get(i).setCurrentRoute(routes.get(1).getId());//set the current route id by the next route of the route list
                                    this.journeys.get(i).setState(0);//set the state waiting
                                    System.out.println("Journey " + this.journeys.get(i).getId() + " has " + routes.size() + " routes and moves from route " + currentRoute + " to " + this.journeys.get(i).getCurrentRoute() + " and change state from " + state + " to " + this.journeys.get(i).getState());
                                } else {
                                    this.journeys.get(i).setState(2);//set this journey end
                                    System.out.println("Journey " + this.journeys.get(i).getId() + " has no routes to run and change state from " + state + " to " + this.journeys.get(i).getState() + " and release all signals in this route");
                                    releaseBlock(currentBlock);//release the passed block
                                }
                                releaseSignal(route);
                            }
                            break;
                        }
                    }
                }
            }
        }

        System.out.println("#####################  Checking running journeys ends! ####################\n");
    }

    /**
     * Lock a list of blocks and change signal by a journey and the current route
     * route : a route belongs to a journey
     * name : the current block it is on
     */
    public boolean lock(Journey journey, String routeId) {
        System.out.println("-------------------------lock  checking------------------------------");
        System.out.println("Journey id :" + journey.getId() + " and current route is :" + routeId + " and block is :"+journey.getCurrentBlock());

        boolean flag = true;
        List<Block> blocks = this.blocks;
        List<Signal> signals = this.signals;

        Route route = Route.dao.getById(this.routes, routeId);//get the current route
        String path = route.getPath();//get the passing path of the route
        String[] paths = path.split(";");

        for (int i = 0; i < blocks.size(); i++) {
            for (int j = 0; j < paths.length; j++) {
                if (blocks.get(i).getName().equals(paths[j])) {
                    System.out.println(blocks.get(i).getName() + " is occupied by journey < " + blocks.get(i).getOccupy() + " > and now journey is " + journey.getId());
                    if (!blocks.get(i).getOccupy().equals("")) {//some on occupy
                        if (!blocks.get(i).getOccupy().equals(journey.getId())) {//the one who wants to occupy is not the one who has occupied
//                            System.out.println("Block " + blocks.get(i).getName() + " is occupied by " + blocks.get(i).getOccupy() + "!!! Set flag to false!");
                            flag = false;
                            break;
                        }
                    }
                }
            }
            if (!flag) {
                break;
            }
        }

        for (int i = 0; i < blocks.size(); i++) {
            for (int j = 0; j < paths.length; j++) {
                if (blocks.get(i).getName().equals(paths[j])) {
                    if (flag) {// no journey occupy
                        blocks.get(i).setOccupy(journey.getId());

                        if (blocks.get(i).getType() == 1) {// this is a point
                            String[] point;
                            if (route.getPoints().contains(";")) {
                                point = route.getPoints().split(";");
                            } else {
                                point = new String[1];
                                point[0] = route.getPoints();
                            }

                            int position = 0;
                            for (int k = 0; k < point.length; k++) {
                                String[] p = point[k].split(":");
                                if (blocks.get(i).getName().equals(p[0])) {
                                    position = p[1].equals("p") ? 0 : 1;
                                }
                            }

                            blocks.get(i).setPosition(position);
                        }
                    }
                }
            }
        }

        if (flag) {
            this.blocks = blocks;
            System.out.println(journey.getId() + " satisfies every condition! Lock now and set the signals stop!");
        }

        String[] signal = route.getSignals().split(";");
        for (int i = 0; i < signals.size(); i++) {
            for (int j = 0; j < signal.length; j++) {
                if (flag && signals.get(i).getName().equals(signal[j])) {//signal name is the same and set to stop
                    this.signals.get(i).setPosition(0);//set to stop
                }
            }
        }


        System.out.println("-------------------------lock  end------------------------------");
        return flag;
    }

    public void releaseBlock(String name) {
        for (int i = 0; i < this.blocks.size(); i++) {
            if (this.blocks.get(i).getName().equals(name)) {
                this.blocks.get(i).setOccupy("");
                System.out.println("Release block " + name + " and now it is occupied by " + this.blocks.get(i).getOccupy());
            }
        }
    }

    public void releaseSignal(Route route) {
        String signals = route.getSignals();
        String[] signal = signals.split(";");

        for (int i = 0; i < this.signals.size(); i++) {
            for (int j = 0; j < signal.length; j++) {
                if (this.signals.get(i).getName().equals(signal[j])) {
                    this.signals.get(i).setPosition(1);//set the signal to go
//                    System.out.println("Set " + this.signals.get(i).getName() + " to go");
                }
            }
        }

    }


    public void setPaths(List<Path> paths) {
        this.paths = paths;
    }

    public List<Journey> getJourneys() {
        return journeys;
    }

    public void setJourneys(List<Journey> journeys) {
        this.journeys = journeys;
    }

    public List<Signal> getUpSignals() {
        return upSignals;
    }

    public void setUpSignals(List<Signal> upSignals) {
        this.upSignals = upSignals;
    }

    public List<Signal> getDownSignals() {
        return downSignals;
    }

    public void setDownSignals(List<Signal> downSignals) {
        this.downSignals = downSignals;
    }

    public List<Route> getUpRoutes() {
        return upRoutes;
    }

    public void setUpRoutes(List<Route> upRoutes) {
        this.upRoutes = upRoutes;
    }

    public List<Route> getDownRoutes() {
        return downRoutes;
    }

    public void setDownRoutes(List<Route> downRoutes) {
        this.downRoutes = downRoutes;
    }


    public List<Signal> getSignals() {
        return signals;
    }

    public void setSignals(List<Signal> signals) {
        this.signals = signals;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }
}

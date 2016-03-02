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

    public Railway() {

        JsonFile jf = new JsonFile();
        this.signals = jf.getSignal();//get all signals from file

        //set up and down direction signals
        for (int i = 0; i < this.signals.size(); i++) {
            Signal signal = Signal.dao.getByName(this.signals, this.signals.get(i).getName());
            if (signal.getDirection() == 1) {//signal direction=0->down  1->up
                this.upSignals.add(signal);
            } else {
                this.downSignals.add(signal);
            }
        }

        this.blocks = jf.getBlock();//get all blocks from file

        this.routes = getRoute();//get all routes

        //set up and down direction routes
        for (int i = 0; i < this.routes.size(); i++) {
            Route route = Route.dao.getById(this.routes, this.routes.get(i).getId());
            if (route.getDirection() == 1) {//signal direction=0->down  1->up
                this.upRoutes.add(route);
            } else {
                this.downRoutes.add(route);
            }
        }

        //set points signals paths for each route in up direction
        for (int i = 0; i < upRoutes.size(); i++) {
            Route route = upRoutes.get(i);

            String path = getPath(route);
            this.upRoutes.get(i).setPath(path);

            String signal = getSignal(route);
            this.upRoutes.get(i).setSignals(signal);

            String points = getPoint(route);
            this.upRoutes.get(i).setPoints(points);

            System.out.println("Route " + route.getId()+" s:"+route.getSource()+" d:"+route.getDest());
            System.out.println("path = " + path);
            System.out.println("signal = " + signal);
            System.out.println("point = " + points + "\n");
            String conflicts = "";

        }


        //set points signals paths for each route in down direction
        for (int i = 0; i < downRoutes.size(); i++) {
            Route route = downRoutes.get(i);

            String path = getPath(route);
            this.downRoutes.get(i).setPath(path);

            String signal = getSignal(route);
            this.downRoutes.get(i).setSignals(signal);

            String points = getPoint(route);
            this.downRoutes.get(i).setPoints(points);

            System.out.println("Route " + route.getId()+" s:"+route.getSource()+" d:"+route.getDest());
            System.out.println("path = " + path);
            System.out.println("signal = " + signal);
            System.out.println("point = " + points + "\n");
            String conflicts = "";

        }

        this.routes.clear();//reset all routes
        this.routes.addAll(upRoutes);//add all up direction routes to routes
        this.routes.addAll(downRoutes);//add all down direction routes to routes

    }

    /**
    * add a journey by inputing  source and dest signals and signals which would passby
    * */
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

    /**
    * check the journeys which are waiting and judge if it could be added to the network
    * */
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

    /**
    * once the journey is allowed to run in the network , just run freely
    * */
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
        System.out.println("Path is "+path);
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

    /**
    * release the block by passing the block name when the train has left the block
    * */
    public void releaseBlock(String name) {
        for (int i = 0; i < this.blocks.size(); i++) {
            if (this.blocks.get(i).getName().equals(name)) {
                this.blocks.get(i).setOccupy("");
                System.out.println("Release block " + name + " and now it is occupied by " + this.blocks.get(i).getOccupy());
            }
        }
    }


    /**
     * release all signals by passing the route when the train has left the block
     * */
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

    /**
    * get a block object by its name
    * */
    public Block getBlockByName( String name) {
        Block block = this.blocks.get(0);
        for (int i = 0; i < this.blocks.size(); i++) {
            block = this.blocks.get(i);
            if (block.getName().equals(name)) {
                break;
            }
        }
        return block;
    }

    /**
     * get a signal object by its name
     * */
    public  Signal getSignalByName(String name) {
        Signal signal = this.signals.get(0);
        for (int i = 0; i < signals.size(); i++) {
            signal = this.signals.get(i);
            if (signal.getName().equals(name)) {
                break;
            }
        }
        return signal;
    }

    /**
    * get all routes by the signals
    * */
    public  List<Route> getRoute(){
        List<Route> routes = new ArrayList<>();
        List<Route> upRoutes = new ArrayList<>();
        List<Route> downRoutes = new ArrayList<>();
        List<Signal> upSignals = this.upSignals;
        List<Signal> downSignals = this.downSignals;

        int counter = 1;//used to name the prefix of the  route name
        for (int i = 0; i < upSignals.size(); i++) {
            Signal signal = upSignals.get(i);
            String[] next = signal.getNext().split(";");
            if (!next[0].equals("")) {
                for (int j = 0; j < next.length; j++) {
                    Route route = new Route("r" + counter, signal.getName(), next[j], signal.getDirection());
                    upRoutes.add(route);
                    counter++;
                }
            }
        }

        for (int i = 0; i < downSignals.size(); i++) {
            Signal signal = downSignals.get(i);
            String[] next = signal.getNext().split(";");
            if (!next[0].equals("")) {
                for (int j = 0; j < next.length; j++) {
                    Route route = new Route("r" + counter, signal.getName(), next[j], signal.getDirection());
                    downRoutes.add(route);
                    counter++;
                }
            }
        }

        routes.addAll(upRoutes);
        routes.addAll(downRoutes);
        return routes;
    }

    /**
     * get all points by the route
     * */
    public  String getPoint(Route route) {
        String point = "";

        String path = route.getPath();
        String[] paths = path.split(";");
        Block p = new Block();
        boolean pointFlag = true;//true->move into a block between two points    false->move out of a block between two points
        for (int i = 0; i < paths.length; i++) {
            p = getBlockByName(paths[i]);
            if (p.getType() == 12) {
                pointFlag = true;
                break;
            } else if (p.getType() == 21) {
                pointFlag = false;
                break;
            }
        }

        if (pointFlag) {
            Block dest = getBlockByName( getSignalByName(route.getDest()).getCurrentBlock());
            String leftPoint = "";
            String rightPoint = "";

            if (route.getDirection()==1){
                leftPoint = dest.getPrevious();
                rightPoint = dest.getNext();
            }else {
                leftPoint = dest.getNext();
                rightPoint = dest.getPrevious();
            }

            if (dest.getType() == 4) {//on PLUS
                point = leftPoint + ":p;" + rightPoint + ":m";
            } else {// on MINUS
                point = leftPoint + ":m;" + rightPoint + ":p";
            }
        } else {
            Block source = getBlockByName(getSignalByName(route.getSource()).getCurrentBlock());
            if (source.getType() == 4) {//on PLUS
                point = p.getName() + ":p";
            } else {// on MINUS
                point = p.getName() + ":m";
            }
        }
        return point;
    }

    /**
     * get all paths by the route
     * */
    public  String getPath( Route route) {
        Signal source = getSignalByName(route.getSource());
        Signal dest = getSignalByName(route.getDest());

        Block next = getBlockByName(source.getCurrentBlock());//source block
        String path = "";
        if (route.getDirection() == 1) {
            while (!next.getNext().contains(dest.getCurrentBlock())) {
                path += next.getNext() + ";";
                next = getBlockByName(next.getNext().split(";")[0]);
            }
        } else {
            while (!next.getPrevious().contains(dest.getCurrentBlock())) {
                path += next.getPrevious() + ";";
                next = getBlockByName(next.getPrevious().split(";")[0]);
            }
        }
        path += dest.getCurrentBlock();
        return path;
    }

    /**
     * get all signals by the route
     * */
    public  String getSignal( Route route) {
        String signal = "";
        String path = route.getPath();
        String[] paths = path.split(";");
        List<Signal> signals = new ArrayList<>();
        boolean pointFlag = true;//true->pass a 1-2 point   false->pass a 2-1 point
        String oppsiteBlock = "";// record the other side of the block which is between two point  e.g.   if a route leave from b3 and path are p2;b5 record b4   if a route leave from b4 and path are p2;b5 record b3
        for (int i = 0; i < paths.length; i++) {
            Block block = getBlockByName(paths[i]);
            if (block.getType() > 10) {// the block is a point
                if (block.getType() == 12) {  //1-2 point
                    pointFlag = true;
                } else { // 2-1 point
                    pointFlag = false;
                }
            }

            if (block.getType() == 3 || block.getType() == 4) {// one of blocks is on the MINUS or PLUS
                String next = "";
                if (route.getDirection()==1){
                    next = block.getNext();
                }else {
                    next = block.getPrevious();
                }
                String[] opps = getBlockByName(next).getPrevious().split(";");
                for (int j = 0; j < opps.length; j++) {
                    if (!opps[j].equals(block.getName())) {
                        oppsiteBlock = opps[j];
                    }
                }
            }
            if (block.getType() == 21) {
                String sourceBlock = getSignalByName(route.getSource()).getCurrentBlock();

                String[] opps = getBlockByName(block.getName()).getPrevious().split(";");
                for (int j = 0; j < opps.length; j++) {
                    if (!sourceBlock.equals(opps[j])) {
                        oppsiteBlock = opps[j];
                    }
                }
            }
            for (int j = 0; j < this.signals.size(); j++) {
                if (this.signals.get(j).getCurrentBlock().equals(paths[i])) {
                    signals.add(this.signals.get(j));//add all signals which are on the passed blocks
                }
            }
        }

        if (pointFlag) {
            for (int i = 0; i < signals.size(); i++) {
                if (signals.get(i).getDirection() == 0) {
                    signal += signals.get(i).getName() + ";";
                }
            }
            for (int i = 0; i < this.signals.size(); i++) {
                if (this.signals.get(i).getCurrentBlock().equals(oppsiteBlock) && this.signals.get(i).getDirection() != route.getDirection()) {
                    signal += this.signals.get(i).getName();
                }
            }
        } else {
            for (int i = 0; i < this.signals.size(); i++) {
                if (this.signals.get(i).getCurrentBlock().equals(oppsiteBlock) && this.signals.get(i).getDirection() == route.getDirection()) {
                    signal += this.signals.get(i).getName() + ";";
                    break;
                }
            }
        }

        String destBlock = getSignalByName(route.getDest()).getCurrentBlock();//get the dest block name

        for (int i = 0; i < this.signals.size(); i++) {
            if (this.signals.get(i).getControllBlock().equals(destBlock)) {
                signal += this.signals.get(i).getName();
                break;
            }
        }
        return signal;
    }
    

    /**
    * getter and setter
    * */
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
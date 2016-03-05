package yiwei;

import org.apache.commons.lang3.StringUtils;

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
        this.blocks = jf.getBlock();
        this.signals = jf.getSignal();

        //set up and down direction signals
        for (int i = 0; i < this.signals.size(); i++) {
            Signal signal = Signal.dao.getByName(this.signals, this.signals.get(i).getName());
            if (signal.getDirection() == 1) {//signal direction=0->down  1->up
                this.upSignals.add(signal);
            } else {
                this.downSignals.add(signal);
            }
        }

    }

    public Railway(String filepath) {
        JsonFile jf = new JsonFile();
        Railway railway = jf.returnRailway(filepath);//get the railway object from file
        this.signals = railway.getSignals();//get all signals from file
//        this.signals = jf.getSignal();

        //set up and down direction signals
        for (int i = 0; i < this.signals.size(); i++) {
            Signal signal = Signal.dao.getByName(this.signals, this.signals.get(i).getName());
            if (signal.getDirection() == 1) {//signal direction=0->down  1->up
                this.upSignals.add(signal);
            } else {
                this.downSignals.add(signal);
            }
        }

        this.blocks = railway.getBlocks();//get all blocks from file
//        this.blocks = jf.getBlock();

        this.routes = getRoute();//get all routes

        //set points signals paths for each route in up direction
        for (int i = 0; i < this.routes.size(); i++) {
            Route route = this.routes.get(i);

            String path = getPath(route);
            if ((path.substring(path.length() - 1, path.length())).equals(";")) {
                path = path.substring(0, path.length() - 1);
            }
            this.routes.get(i).setPath(path);


            String signal = getSignal(route);
            if ((signal.substring(signal.length() - 1, signal.length())).equals(";")) {
                signal = signal.substring(0, signal.length() - 1);
            }
            this.routes.get(i).setSignals(signal);

            String points = getPoint(route);
            if ((points.substring(points.length() - 1, points.length())).equals(";")) {
                points = points.substring(0, points.length() - 1);
            }
            this.routes.get(i).setPoints(points);
        }

        //set up and down direction routes
        for (int i = 0; i < this.routes.size(); i++) {
            Route route = Route.dao.getById(this.routes, this.routes.get(i).getId());
            if (route.getDirection() == 1) {//signal direction=0->down  1->up
                this.upRoutes.add(route);
            } else {
                this.downRoutes.add(route);
            }
        }

        for (int i = 0; i < this.routes.size(); i++) {
            Route route = this.routes.get(i);
            String conflicts = getConflict(this.routes.get(i));
            this.routes.get(i).setConflicts(conflicts);

            System.out.println("Route " + route.getId() + " s:" + route.getSource() + " d:" + route.getDest());
            System.out.println("path = " + route.getPath());
            System.out.println("signal = " + route.getSignals());
            System.out.println("conflict = " + route.getConflicts());
            System.out.println("point = " + route.getPoints() + "\n");
        }

        //set up and down direction routes
        for (int i = 0; i < this.routes.size(); i++) {
            Route route = Route.dao.getById(this.routes, this.routes.get(i).getId());
            if (route.getDirection() == 1) {//signal direction=0->down  1->up
                this.upRoutes.add(route);
            } else {
                this.downRoutes.add(route);
            }
        }
    }

    /**
    * get all possible journey passby signals by passing railway , source signal , dest signal
    * */
    public List<String> getJourneyPassby(String source,String dest){
        String passby = "";

        passby+=source+",";
        Signal next = getSignalByName(source);
        while (!next.getNext().equals(dest)){
            passby += next.getNext()+",";
            next = getSignalByName(next.getNext().split(";")[0]);
        }
        passby+=dest;

        String[] passbys = passby.split(",");
        List<Integer> index = new ArrayList<>();


        List<String> list = new ArrayList<>();
        for (int i = 0; i < passbys.length; i++) {
            if (passbys[i].contains(";")){
                index.add(i);
            }
        }

        for (int i = 0; i < 2 * index.size(); i++) {
            list.add(passby);
        }

        List<String> possible = new ArrayList<>();
        for (int i = 0; i < 2*index.size(); i++) {
            String tmp = StringUtils.leftPad(Integer.toBinaryString(i), index.size(), "0");
            possible.add(tmp);
        }


        for (int i = 0; i < list.size(); i++) {
            String wholePath = list.get(i);
            String choice = possible.get(i);

            String[] paths = wholePath.split(",");
            String[] choices = choice.split("");

            for (int j = 0; j < index.size(); j++) {
                paths[index.get(j)] = paths[index.get(j)].split(";")[Integer.parseInt(choices[j])];
            }

            String rs = "";
            for (int j = 0; j < paths.length; j++) {
                rs += paths[j];
                if (j!=paths.length-1){
                    rs += ";";
                }
            }

            list.set(i, rs);
        }

        return list;
    }

    /**
     * add a journey by inputing  source and dest signals and signals which would passby
     */
    public void addJourney(String journeyId, String source, String dest, String passby) {
//        System.out.println("#####################  Adding journeys begins! ####################");
//        System.out.println(journeyId +" "+source+" "+dest+" "+passby+"   "+this.routes.size());
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
//        System.out.println("Journey "+journeyId+" current route is " + journey.getCurrentRoute() + " and block is " + journey.getCurrentBlock());
        journey.setCurrentBlock(signal.getCurrentBlock());//set the journey current block by signal's current

        this.journeys.add(journey);
//        System.out.println("#####################  Adding journeys ends! ####################\n");
    }

    /**
     * check the journeys which are waiting and judge if it could be added to the network
     */
    public void checkWaitingList() {
        System.out.println("#####################  Checking waiting journeys begins! ####################");
        for (int i = 0; i < this.journeys.size(); i++) {
            Journey waiting = this.journeys.get(i);
            if (waiting.getState() == 0) {//if this journey is waiting
                boolean flag = lock(waiting, waiting.getCurrentRoute());
                if (flag) {
                    this.journeys.get(i).setState(1);

                    System.out.println("Journey " + this.journeys.get(i).getId() + " satisfies all conditions and set to " + this.journeys.get(i).getState() + " and lock all signals in this route");

                }else {
                    this.journeys.get(i).setState(0);//set state of the journeys
                }
            }
        }

        System.out.println("#####################  Checking waiting journeys ends! ####################\n");
    }

    /**
     * once the journey is allowed to run in the network , just run freely
     */
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
                System.out.println("Journey " + id + " starts running!Current route="+currentRoute+" ,block="+currentBlock);
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
                    if (path[k].equals(currentBlock)) {
                        isInArray = true;
                    }
                    if (k == path.length - 1 && !isInArray) {
                        this.journeys.get(i).setCurrentBlock(path[0]);//set the current block of this journey
                        System.out.println("Journey " + this.journeys.get(i).getId() + " moves from source signal block: " + currentBlock + " to first route path[0]: " + this.journeys.get(i).getCurrentBlock() + " and release the block " + currentBlock);
                        releaseBlock(currentBlock);//release the passed block
                        break;
                    } else {
                        if (path[k].equals(currentBlock)) {// if the train is on this block
                            if (k < path.length - 1) {//there are more than one block to go in this route
                                this.journeys.get(i).setCurrentBlock(path[k + 1]);//set the current block of this journey
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
        System.out.println("Journey id :" + journey.getId() + " and current route is :" + routeId + " and block is :" + journey.getCurrentBlock());

        boolean flag = true;
        List<Block> blocks = this.blocks;
        List<Signal> signals = this.signals;

        Route route = Route.dao.getById(this.routes, routeId);//get the current route
        String path = route.getPath();//get the passing path of the route
        System.out.println(this.routes.size()+" Route is "+route.getId()+" Path is " + path);
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

                        if (blocks.get(i).getType() > 10) {// this is a point
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
     */
    public void releaseBlock(String name) {
        for (int i = 0; i < this.blocks.size(); i++) {
            if (this.blocks.get(i).getName().equals(name)) {
                this.blocks.get(i).setOccupy("");
                System.out.println("Release block " + name + " and now it is occupied by " + this.blocks.get(i).getOccupy());
            }
        }
    }

    /**
     * release the block by passing the block name when the train has left the block
     */
    public void releaseBlockByRoute(Route route) {
        String path = route.getPath();
        String[] paths = path.split(";");
        for (int i = 0; i < paths.length; i++) {
            releaseBlock(paths[i]);
        }
        System.out.println("Release all blocks " + path + " of route " +route.getId());
    }

    /**
     * release all signals by passing the route when the train has left the block
     */
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
     */
    public Block getBlockByName(String name) {
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
     */
    public Signal getSignalByName(String name) {
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
     */
    public List<Route> getRoute() {
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
     */
    public String getPoint(Route route){
        String point = "";

        Block source = getBlockByName(getSignalByName(route.getSource()).getCurrentBlock());
        Block dest = getBlockByName(getSignalByName(route.getDest()).getCurrentBlock());

        if (source.getType()==3 || source.getType()==4){
            Block previous = getBlockByName(source.getPrevious());
            Block next = getBlockByName(source.getNext());
            if (route.getDirection()==1){
                point += next.getName()+":";
            }else {
                point += previous.getName()+":";
            }
            point += source.getType()==3?"m;":"p;";
        }
        if (dest.getType()==3 || dest.getType()==4){
            Block previous = getBlockByName(dest.getPrevious());
            Block next = getBlockByName(dest.getNext());

            if (route.getDirection()==1){
                if (dest.getType()==3){
                    point += previous.getName()+":m;";
                    point += next.getName()+":p;";
                }else {
                    point += previous.getName()+":p;";
                    point += next.getName()+":m;";
                }
            }else {
                if (dest.getType()==3){
                    point += previous.getName()+":p;";
                    point += next.getName()+":m;";
                }else {
                    point += previous.getName()+":m;";
                    point += next.getName()+":p;";
                }
            }
        }

        return point;
    }

    /**
     * get all paths by the route
     */
    public String getPath(Route route) {
        Signal source = getSignalByName(route.getSource());
        Signal dest = getSignalByName(route.getDest());

        Block next = getBlockByName(source.getCurrentBlock());//source block
        String path = "";
//        System.out.println(route.getId() + " dire=" + route.getDirection() + " source_block=" + next.getName() + " pre=" + next.getPrevious() + " next=" + next.getNext() + " dest_block=" + dest.getCurrentBlock());
        if (route.getDirection() == 1) {
            while (!next.getNext().contains(dest.getCurrentBlock())) {
                path += next.getNext().split(";")[0] + ";";
                next = getBlockByName(next.getNext().split(";")[0]);
            }
        } else {
            while (!next.getPrevious().contains(dest.getCurrentBlock())) {
                path += next.getPrevious().split(";")[0] + ";";
                next = getBlockByName(next.getPrevious().split(";")[0]);
            }
        }
        path += dest.getCurrentBlock();
        return path;
    }

    /**
     * get all signals by the route
     */
    public String getSignal(Route route){
        String signal = "";
        Block source = getBlockByName(getSignalByName(route.getSource()).getCurrentBlock());
        Block dest = getBlockByName(getSignalByName(route.getDest()).getCurrentBlock());

        if (source.getType()==3 || source.getType()==4){//source is between two points
            String previous = source.getPrevious();
            String next = source.getNext();

            for (int i = 0; i < this.signals.size(); i++) {
                Signal s = this.signals.get(i);
                Block b = getBlockByName(s.getCurrentBlock());
                if (b.getPrevious().equals(previous) && b.getNext().equals(next) && b.getType()!=source.getType() && s.getDirection()==route.getDirection()){
                    signal += s.getName()+";";
                    break;
                }
            }
            if (dest.getType()!=3 && dest.getType()!=4){
                for (int i = 0; i < this.signals.size(); i++) {
                    if (this.signals.get(i).getControllBlock().equals(dest.getName()) && this.signals.get(i).getDirection()!=route.getDirection()){
                        signal += this.signals.get(i).getName()+";";
                    }
                }
            }
        }

        if (dest.getType()==3 || dest.getType()==4){//source is between two points
            if (source.getType()!=3 && source.getType()!=4){
                for (int i = 0; i < this.signals.size(); i++) {
                    if (this.signals.get(i).getControllBlock().equals(source.getName()) && this.signals.get(i).getDirection()!=route.getDirection()){
                        signal += this.signals.get(i).getName()+";";
                    }
                }
            }

            String previous = dest.getPrevious();
            String next = dest.getNext();

            for (int i = 0; i < this.signals.size(); i++) {
                Signal s = this.signals.get(i);
                Block b = getBlockByName(s.getCurrentBlock());
                if (b.getPrevious().equals(previous) && b.getNext().equals(next)  && s.getDirection()!=route.getDirection()){
                    signal += s.getName()+";";
                }
            }
        }

        return signal;
    }

    /**
     * get all conflicts by the route
     */
    public String getConflict(Route route) {
        String conflict = "";
        List<String> list = new ArrayList<>();
        List<Route> routes = this.routes;
        String path = route.getPath();
        String[] paths = path.split(";");

        for (int j = 0; j < routes.size(); j++) {
            Route route1 = routes.get(j);
            boolean isConflict = false;
            if (!route.getId().equals(route1.getId())) {
                String path1 = route1.getPath();
                String[] path1s = path1.split(";");
                for (int i = 0; i < paths.length; i++) {
                    for (int k = 0; k < path1s.length; k++) {
                        if (paths[i].equals(path1s[k])) {
                            if (!list.contains(route1.getId())) {
                                list.add(route1.getId());
                                isConflict = true;
                            }
                        }
                        if (isConflict) {
                            break;
                        }
                    }
                    if (isConflict) {
                        break;
                    }
                }
            }
        }

        for (int i = 0; i < list.size(); i++) {
            conflict += list.get(i) + ";";
        }

        return conflict.substring(0, conflict.length() - 1);
    }

    /**
     * getter and setter
     */
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
package yiwei;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by tommy on 03/03/2016 19:55
 */
public class InterlockImpl implements Interlock {
    /**
    * if it is true , this journey would be added to the network automatically
     * if it is false, you should send another journey
     *
     * Map contains three attributes
     * 1.  journey :   String   s1;s4;s8;s11
     * 2.  path   :    List<String>   get(0)=b2;p3;b4
     * 3.  journeyId : String   j1
    * */
    @Override
    public boolean check(Railway railway,Map<String, Object> map) {
        boolean flag = true;
        String signals = map.get("journey")==null?"":map.get("journey").toString();

        if (!journeyExits(railway,signals)) {

            String[] signal = signals.split(";");
            List<String> paths = map.get("path") == null ? new ArrayList<>() : (List<String>) map.get("path");
            for (int i = 0; i < signal.length; i++) {
                if (i < signal.length - 1) {
                    String source = signal[i];
                    String dest = signal[i + 1];
                    if (!Route.dao.getBySourceAndDest(railway.getRoutes(), source, dest).getPath().equals(paths.get(i))) {
                        flag = false;
                    }
                }
            }
            Random r = new Random();
            String id = map.get("journeyId") != null ? map.get("journeyId").toString() : "j" + r.nextInt(10);

            Journey journey = addJourney(railway, id, signal[0], signal[signal.length - 1], signals);

            flag = lock(railway, journey);

            if (flag) {
                journey.setState(1);
                railway.getJourneys().add(journey);
            } else {
                journey.setState(0);
                railway.getJourneys().add(journey);
            }

        }else {
            flag = false;
        }
        return  flag;
    }

    /**
     * Lock a list of blocks and change signal by a journey and the current route
     * route : a route belongs to a journey
     * name : the current block it is on
     */
    public boolean lock(Railway railway,Journey journey) {
        System.out.println("-------------------------lock  checking------------------------------");
        System.out.println("Journey id :" + journey.getId() + " and current route is :" + journey.getCurrentRoute() + " and block is :" + journey.getCurrentBlock());

        boolean flag = true;
        List<Block> blocks = railway.getBlocks();
        List<Signal> signals = railway.getSignals();

        Route route = Route.dao.getById(railway.getRoutes(), journey.getCurrentRoute());//get the current route
        String path = route.getPath();//get the passing path of the route
//        System.out.println(railway.getRoute().size()+" Route is "+route.getId()+" Path is " + path);
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
            System.out.println(journey.getId() + " satisfies every condition! Could be added!");
        }

        String[] signal = route.getSignals().split(";");
        for (int i = 0; i < signals.size(); i++) {
            for (int j = 0; j < signal.length; j++) {
                if (flag && signals.get(i).getName().equals(signal[j])) {//signal name is the same and set to stop
                    railway.getSignals().get(i).setPosition(0);//set to stop
                }
            }
        }

        System.out.println("-------------------------lock  end------------------------------");
        return flag;
    }

    @Override
    public List<Railway> running(Railway railway) {
        List<Railway> railways = new ArrayList<>();//store every move of the railway

        boolean flag = true;

        while (flag) {

            railway.checkWaitingList();
            for (int j = 0; j < railway.getSignals().size(); j++) {
                System.out.println("Signal " + railway.getSignals().get(j).getName() + "  :  " + railway.getSignals().get(j).getPosition());
            }
            for (int j = 0; j < railway.getBlocks().size(); j++) {
                if (railway.getBlocks().get(j).getType() > 10) {//point
                    System.out.println("Point " + railway.getBlocks().get(j).getName() + "  :  " + railway.getBlocks().get(j).getOccupy() + "  position: " + (railway.getBlocks().get(j).getPosition() == 0 ? "PLUS" : "MINUS"));
                } else {
                    System.out.println("Block " + railway.getBlocks().get(j).getName() + "  :  " + railway.getBlocks().get(j).getOccupy());
                }
            }

            railway.runFreely();
            for (int j = 0; j < railway.getSignals().size(); j++) {
                System.out.println("Signal " + railway.getSignals().get(j).getName() + "  :  " + railway.getSignals().get(j).getPosition());
            }
            for (int j = 0; j < railway.getBlocks().size(); j++) {
                if (railway.getBlocks().get(j).getType() > 10) {//point
                    System.out.println("Point " + railway.getBlocks().get(j).getName() + "  :  " + railway.getBlocks().get(j).getOccupy() + "  position: " + (railway.getBlocks().get(j).getPosition() == 0 ? "PLUS" : "MINUS"));
                } else {
                    System.out.println("Block " + railway.getBlocks().get(j).getName() + "  :  " + railway.getBlocks().get(j).getOccupy());
                }
            }


            int counter = 0;
            for (int i = 0; i < railway.getJourneys().size(); i++) {
                if (railway.getJourneys().get(i).getState() != 2) {
                    counter++;
                }
            }

            for (int i = 0; i < railway.getJourneys().size(); i++) {
                Journey j = railway.getJourneys().get(i);
                System.out.println("Journey " + j.getId() + " block:" + j.getCurrentBlock() + " route:" + j.getCurrentRoute() + " state:" + j.getState());
            }

            railways.add(railway);

            if (counter == 0) {
                flag = false;
            }

        }

        System.out.println("running result: railway size=" + railways.size());

        return railways;
    }


    /**
     * add a journey by inputing  source and dest signals and signals which would passby
     */
    @Override
    public Journey addJourney(Railway railway, String journeyId, String source, String dest, String passby) {
        Journey journey = new Journey(journeyId, source, dest);
        String[] passbys = passby.split(";");
        for (int i = 1; i < passbys.length; i++) {

            Route route = Route.dao.getBySourceAndDest(railway.getRoutes(), passbys[i - 1], passbys[i]);
            journey.getRoutes().add(route);
        }

        String routeId = journey.getRoutes().get(0).getId();//get the first route id
        journey.setCurrentRoute(routeId);//set the current route id
        Route route = Route.dao.getById(railway.getRoutes(), routeId);//get the route by its id
        Signal signal = Signal.dao.getByName(railway.getSignals(), route.getSource());//get the signal by route source

        journey.setCurrentBlock(signal.getCurrentBlock());//set the journey current block by signal's current

        System.out.println("addJourney result journey=" + journey.getId()+"  source="+journey.getSource()+"  dest="+journey.getDest());
        return journey;
    }

    /**
    * check if the journey exists in the network right now
    * */
    private boolean journeyExits(Railway railway,String passby){
        boolean flag = false;
        List<Journey> journeys = railway.getJourneys();

        for (int i = 0; i < journeys.size(); i++) {
            Journey j = journeys.get(i);
            List<Route> routes = j.getRoutes();
            String signals = routes.get(0).getSource();
            for (int k = 0; k < routes.size(); k++) {
                signals += ";"+routes.get(k).getDest();
            }
            if (signals.equals(passby)){
                flag = true;
                break;
            }
        }

        return flag;
    }


    @Override
    public List<Route> getRoutes(Railway railway) {
        System.out.println("getRoutes result routes size=" + railway.getRoutes().size());
        return railway.getRoutes();
    }

    @Override
    public List<Block> getBlocks(Railway railway) {
        System.out.println("getBlocks result routes size=" + railway.getBlocks().size());
        return railway.getBlocks();
    }

    @Override
    public List<Signal> getSignals(Railway railway) {
        System.out.println("getSignals result routes size=" + railway.getSignals().size());
        return railway.getSignals();
    }
}

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tommy on 03/03/2016 19:55
 */
public class InterlockImpl implements Interlock {
    @Override
    public List<String> getJourneyPassby(Railway railway, String source, String dest) {
        String passby = "";

        passby+=source+",";
        Signal next = railway.getSignalByName(source);
        while (!next.getNext().equals(dest)){
            passby += next.getNext()+",";
            next = railway.getSignalByName(next.getNext().split(";")[0]);
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

            list.set(i,rs);
        }

        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }
        return list;
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

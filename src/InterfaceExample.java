import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tommy on 03/03/2016 20:39
 */
public class InterfaceExample {
    public static void main(String[] args){
        String filepath = "./resource/map2.json";
        String source = "s12";
        String dest = "s2";

        Interlock interlock = new InterlockImpl();

        Railway railway = new Railway(filepath);
        //get all routes
//        List<Route> routes = interlock.getRoutes(railway);

        //get all blocks
//        List<Block> blocks = interlock.getBlocks(railway);

        //get all signals
//        List<Signal> signals = interlock.getSignals(railway);

        List<Journey> journeys = new ArrayList<>();



        //get possible journeys
        List<String> passbys = interlock.getJourneyPassby(railway, source, dest);



      /*  //add a journey to the list
        //map1
        journeys.add(interlock.addJourney(railway,"j1", "s1", "s7", "s1;s4;s7"));
        journeys.add(interlock.addJourney(railway,"j2", "s1", "s7", "s1;s6;s7"));
        journeys.add(interlock.addJourney(railway,"j3", "s8", "s2", "s8;s3;s2"));
        journeys.add(interlock.addJourney(railway,"j4", "s8", "s2", "s8;s5;s2"));*/

/*        //map2
        journeys.add(interlock.addJourney(railway,"j1",  "s1", "s11", "s1;s4;s8;s11"));
        journeys.add(interlock.addJourney(railway,"j2",  "s1", "s11", "s1;s6;s10;s11"));
        journeys.add(interlock.addJourney(railway,"j3",  "s12", "s2", "s12;s9;s3;s2"));
        journeys.add(interlock.addJourney(railway,"j4",  "s12", "s2", "s12;s9;s5;s2"));*/

        //pass a journey list to the program to let it run
//        railway.setJourneys(journeys);
//        List<Railway> railways = interlock.running(railway);
    }

}

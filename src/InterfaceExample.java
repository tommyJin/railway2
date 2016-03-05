import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Created by tommy on 03/03/2016 20:39
 */
public class InterfaceExample {
    public static void main(String[] args){
        String filepath = "./resource/map2.json";
        String source = "s12";
        String dest = "s2";

        Interlock interlock = new InterlockImpl();

        //create a railway object
        Railway railway = new Railway(filepath);



        //check interface demo
        Map<String,Object> map = new HashMap<>();
        String journey = "s1;s4;s8;s11";
        map.put("journey",journey);
        map.put("journeyId","j1");
        List<String> paths = new ArrayList<>();
        String[] signal = journey.split(";");
        for (int i = 0; i < signal.length ; i++) {
            if (i < signal.length - 1) {
                String path = Route.dao.getBySourceAndDest(railway.getRoutes(), signal[i], signal[i + 1]).getPath();
                paths.add(path);
            }
        }
        map.put("path",paths);

        boolean flag = interlock.check(railway, map);


        System.out.println(flag);
        System.out.println(interlock.check(railway, map)+"  "+railway.getJourneys().size());//attempt to add the same journey






//        List<Journey> journeys = new ArrayList<>();

      /*  //add a journey to the list
        //map1
        journeys.add(interlock.addJourney(railway,"j1", "s1", "s7", "s1;s4;s7"));
        journeys.add(interlock.addJourney(railway,"j2", "s1", "s7", "s1;s6;s7"));
        journeys.add(interlock.addJourney(railway,"j3", "s8", "s2", "s8;s3;s2"));
        journeys.add(interlock.addJourney(railway,"j4", "s8", "s2", "s8;s5;s2"));*/
/*
        //map2
        journeys.add(interlock.addJourney(railway,"j1",  "s1", "s11", "s1;s4;s8;s11"));
        journeys.add(interlock.addJourney(railway,"j2",  "s1", "s11", "s1;s6;s10;s11"));
        journeys.add(interlock.addJourney(railway,"j3",  "s12", "s2", "s12;s9;s3;s2"));
        journeys.add(interlock.addJourney(railway,"j4",  "s12", "s2", "s12;s9;s5;s2"));
        journeys.add(interlock.addJourney(railway,"j5",  "s12", "s2", "s12;s7;s3;s2"));
        journeys.add(interlock.addJourney(railway,"j6",  "s12", "s2", "s12;s7;s5;s2"));

        //pass a journey list to the program to let it run
        railway.setJourneys(journeys);*/

//        String passby = generateJourneyPassby(railway,passbys);
//        System.out.println("Chosen by me : "+passby);

        List<Railway> railways = interlock.running(railway);
    }

}

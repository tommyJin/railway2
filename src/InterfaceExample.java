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

        Interlock interlock = new InterlockImpl();

        Railway railway = new Railway(filepath);
        List<Journey> journeys = new ArrayList<>();

        getJourney(railway,"s1","s11");

//        List<Route> routes = interlock.getRoutes(railway);

//        List<Block> blocks = interlock.getBlocks(railway);

//        List<Signal> signals = interlock.getSignals(railway);

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

*/
        //pass a journey list to the program to let it run
//        railway.setJourneys(journeys);
//        List<Railway> railways = interlock.running(railway);
    }

    public static void getJourney(Railway railway,String source,String dest){
        List<Route> routes = railway.getRoutes();
        List<Signal> signals = railway.getSignals();

        String passby = "";
        Signal sourceSignal = railway.getSignalByName(source);
        Signal destSignal = railway.getSignalByName(dest);

        passby+=source+",";
        Signal next = railway.getSignalByName(source);
        while (!next.getNext().equals(dest)){
            System.out.println("next.getnext="+next.getNext());
            passby += next.getNext()+",";
            next = railway.getSignalByName(next.getNext().split(";")[0]);
            System.out.println("passby="+passby);
        }
        passby+=dest;
        System.out.println("Final passby="+passby);

        String[] passbys = passby.split(",");
        List<Integer> index = new ArrayList<>();
        List<String> list = new ArrayList<>();
        for (int i = 0; i < passbys.length; i++) {
            if (passbys[i].contains(";")){
                index.add(i);
            }
            System.out.println(passbys[i]);
        }
        System.out.println("index="+index.size());

        for (int i = 0; i < 2 * index.size(); i++) {
            list.add(source);
        }

        for (int i = 1; i < passbys.length; i++) {
            if (passbys[i].contains(";")){
                String[] passbyss = passbys[i].split(";");
                for (int k = 0; k < passbyss.length; k++) {
                    for (int j = 0; j < list.size(); j++) {
                        list.set(j,list.get(j)+","+passbys[i]);
                    }
                }

            }else {
                for (int j = 0; j < list.size(); j++) {
                    list.set(j,list.get(j)+","+passbys[i]);
                }
            }
        }

        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }
    }
}

import com.google.gson.Gson;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tommy on 2016/2/20.
 */
public class JsonFile {

    public List<Signal> getSignal(){
        List<Signal> list = new ArrayList<>();
        Signal s1 = new Signal("s1",1, "b1", "b2","s4;s6");
        Signal s2 = new Signal("s2",0, "b2", "b1","");
        Signal s3 = new Signal("s3",0, "b3", "p1","s2");
        Signal s4 = new Signal("s4",1, "b3", "p2","s7");
        Signal s5 = new Signal("s5",0, "b4", "p1","s2");
        Signal s6 = new Signal("s6",1, "b4", "p2","s7");
        Signal s7 = new Signal("s7",1, "b5", "b6","");
        Signal s8 = new Signal("s8",0, "b6", "b5","s3;s5");
        list.add(s1);
        list.add(s2);
        list.add(s3);
        list.add(s4);
        list.add(s5);
        list.add(s6);
        list.add(s7);
        list.add(s8);
        return list;
    }

    public List<Block> getBlock(){
        List<Block> list = new ArrayList<>();
        Block b1 = new Block("b1",1,"","b2","");
        Block b2 = new Block("b2",2,"b1","p1","");
        Block b3 = new Block("b3",3,"p1","p2","n1");
        Block b4 = new Block("b4",4,"p1","p2","n1");
        Block b5 = new Block("b5",5,"p2","b6","");
        Block b6 = new Block("b6",6,"b5","","");
        Block p1 = new Block("p1",12,"b2","b3;b4","");
        Block p2 = new Block("p2",21,"b3;b4","b5","");

        list.add(b1);
        list.add(b2);
        list.add(b3);
        list.add(b4);
        list.add(b5);
        list.add(b6);
        list.add(p1);
        list.add(p2);
        return list;
    }

    public List<Route> getRoute(){
        List<Route> list = new ArrayList<>();

        Signal s1 = new Signal("s1",1, "b1", "b2","s4;s6");
        Signal s2 = new Signal("s2",0, "b2", "b1","");
        Signal s3 = new Signal("s3",0, "b3", "p1","s2");
        Signal s4 = new Signal("s4",1, "b3", "p2","s7");
        Signal s5 = new Signal("s5",0, "b4", "p1","s2");
        Signal s6 = new Signal("s6",1, "b4", "p2","s7");
        Signal s7 = new Signal("s7",1, "b5", "b6","");
        Signal s8 = new Signal("s8",0, "b6", "b5","s3;s5");

        Route r1 = new Route("r1","s1","s6","p1:p;p2:m","s2;s3;s5","b2;p1;b4",1);
        List<Signal> signals = new ArrayList<>();
        signals.add(s2);
        signals.add(s3);
        signals.add(s5);
//        r1.setSignals(signals);
        list.add(r1);

        signals.clear();
        Route r2 = new Route("r2","s1","s4","p1:m;p2:p","s2;s3;s5","b2;p1;b3",1);
        signals.add(s2);
        signals.add(s3);
        signals.add(s5);
//        r2.setSignals(signals);
        list.add(r2);

        signals.clear();
        Route r3 = new Route("r3","s4","s7","p2:p","s8;s6","p2;b5",1);
        signals.add(s8);
        signals.add(s6);
//        r3.setSignals(signals);
        list.add(r3);

        signals.clear();
        Route r4 = new Route("r4","s6","s7","p2:p","s8;s4","p2;b5",1);
        signals.add(s8);
        signals.add(s4);
//        r4.setSignals(signals);
        list.add(r4);



        signals.clear();
        Route r5 = new Route("r5","s8","s5","p1:m;p2:p","s4;s6;s7","b5;p2;b4",0);
        signals.add(s7);
        signals.add(s6);
        signals.add(s4);
//        r5.setSignals(signals);
        list.add(r5);

        signals.clear();
        Route r6 = new Route("r6","s8","s3","p1:p;p2:m","s4;s6;s7","b5;p2;b3",0);
        signals.add(s7);
        signals.add(s6);
        signals.add(s4);
//        r6.setSignals(signals);
        list.add(r6);

        signals.clear();
        Route r7 = new Route("r7","s3","s2","p1:m","s1;s5","p1;b2",0);
        signals.add(s1);
        signals.add(s6);
//        r7.setSignals(signals);
        list.add(r7);

        signals.clear();
        Route r8 = new Route("r8","s5","s2","p1:p","s1;s3","p1;b2",0);
        signals.add(s1);
        signals.add(s6);
//        r8.setSignals(signals);
        list.add(r8);

        return list;
    }

    public Railway getRailway(){
        Railway railway = new Railway();
        return railway;
    }


    /**
     * read railway object from json file
     * */
    public Railway returnRailway(){
        Gson gson = new Gson();
        String jsonStr = readFile("./src/test.json");
        Railway railway = gson.fromJson(jsonStr, Railway.class);
//        System.out.println(railway.getSignals().size()+" "+railway.getBlocks().get(0).getName());
        return railway;
    }


    public static void writeFile(String filePath, String sets)
            throws  IOException {
        FileWriter fw = new FileWriter(filePath);
        PrintWriter out = new PrintWriter(fw);
        out.write(sets);
        out.println();
        fw.close();
        out.close();
    }

    public static String readFile(String path) {
        File file = new File(path);
        BufferedReader reader = null;
        String laststr = "";
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                laststr = laststr + tempString;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return laststr;
    }
}

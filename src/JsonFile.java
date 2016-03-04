import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tommy on 2016/2/20.
 */
public class JsonFile {

    public List<Signal> getSignal(){
        List<Signal> list = new ArrayList<>();
/*
        //map1
        Signal s1 = new Signal("s1",1, "b1", "b2","s4;s6");
        Signal s2 = new Signal("s2",0, "b2", "b1","");
        Signal s3 = new Signal("s3",0, "b3", "p1","s2");
        Signal s4 = new Signal("s4",1, "b3", "p2","s7");
        Signal s5 = new Signal("s5",0, "b4", "p1","s2");
        Signal s6 = new Signal("s6",1, "b4", "p2","s7");
        Signal s7 = new Signal("s7",1, "b5", "b6","");
        Signal s8 = new Signal("s8",0, "b6", "b5","s3;s5");
*/


        //map2
        Signal s1 = new Signal("s1",1, "b1", "b2","s4;s6");
        Signal s2 = new Signal("s2",0, "b2", "b1","");
        Signal s3 = new Signal("s3",0, "b12", "p3","s2");
        Signal s4 = new Signal("s4",1, "b12", "p5","s8;s10");
        Signal s5 = new Signal("s5",0, "b4", "p3","s2");
        Signal s6 = new Signal("s6",1, "b4", "p5","s8;s10");
        Signal s7 = new Signal("s7",0, "b13", "p7","s3;s5");
        Signal s8 = new Signal("s8",1, "b13", "p9","s11");
        Signal s9 = new Signal("s9",0, "b8", "p7","s3;s5");
        Signal s10 = new Signal("s10",1, "b8", "p9","s11");
        Signal s11 = new Signal("s11",1, "b10", "b11","");
        Signal s12 = new Signal("s12",0, "b11", "b10","s7;s9");


        list.add(s1);
        list.add(s2);
        list.add(s3);
        list.add(s4);
        list.add(s5);
        list.add(s6);
        list.add(s7);
        list.add(s8);

        list.add(s9);
        list.add(s10);
        list.add(s11);
        list.add(s12);

        return list;
    }

    public List<Block> getBlock(){
        List<Block> list = new ArrayList<>();
/*
        //map1
        Block b1 = new Block("b1",1,"","b2");
        Block b2 = new Block("b2",2,"b1","p1");
        Block b3 = new Block("b3",3,"p1","p2");
        Block b4 = new Block("b4",4,"p1","p2");
        Block b5 = new Block("b5",5,"p2","b6");
        Block b6 = new Block("b6",6,"b5","");
        Block p1 = new Block("p1",12,"b2","b3;b4");
        Block p2 = new Block("p2",21,"b3;b4","b5");
*/

      //map2
        Block b1 = new Block("b1",1,"","b2");
        Block b2 = new Block("b2",2,"b1","p3");
        Block b4 = new Block("b4",4,"p3","p5");
        Block b6 = new Block("b6",7,"p5","p7");
        Block b8 = new Block("b8",4,"p7","p9");
        Block b10 = new Block("b10",5,"p9","b11");
        Block b11 = new Block("b11",6,"b10","");
        Block b12 = new Block("b12",3,"p3","p5");
        Block b13 = new Block("b13",3,"p7","p9");

        Block p3 = new Block("p3",12,"b2","b12;b4");
        Block p5 = new Block("p5",21,"b12;b4","b6");
        Block p7 = new Block("p7",12,"b6","b13;b8");
        Block p9 = new Block("p9",21,"b13;b8","b10");


        list.add(b1);
        list.add(b2);
        list.add(b4);
        list.add(b6);
        list.add(b8);
        list.add(b10);
        list.add(b11);
        list.add(b12);
        list.add(b13);


        list.add(p3);
        list.add(p5);
        list.add(p7);
        list.add(p9);

        return list;
    }

    public Railway getRailway(){
        Railway railway = new Railway();
        return railway;
    }


    /**
     * read railway object from json file
     * */
    public Railway returnRailway(String filepath){
        Gson gson = new Gson();
        String jsonStr = readFile(filepath);
        Railway railway = gson.fromJson(jsonStr, new TypeToken<Railway>(){}.getType());
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

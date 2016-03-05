package yiwei;

import com.google.gson.Gson;

import java.io.IOException;


/**
 * Created by tommy on 2016/2/18.
 */
public class Init {
    public static void main(String[] args){
        JsonFile jf = new JsonFile();
        Gson gson = new Gson();

        try {
            jf.writeFile("./src/test.json", gson.toJson(jf.getRailway()));
        } catch (IOException e) {
            e.printStackTrace();
        }

//        String jsonStr = jf.readFile("./src/test.json");
//        Railway railway = gson.fromJson(jsonStr, new TypeToken<Railway>(){}.getType());
//        System.out.println(railway.getBlocks().size()+" "+railway.getSignals().size());
    }
}

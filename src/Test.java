import java.util.ArrayList;
import java.util.List;

/**
 * Created by tommy on 2016/2/28.
 */
public class Test {
    public static void main(String[] args){
        List<Railway> railways = new ArrayList<>();//store every move of the railway

        String filepath = "./resource/map2.json";
        Railway railway = new Railway(filepath);
       /* //map1
        railway.addJourney("j1", "s1", "s7", "s1;s4;s7");
        railway.addJourney("j2", "s1", "s7", "s1;s6;s7");
        railway.addJourney("j3", "s8", "s2", "s8;s3;s2");
        railway.addJourney("j4", "s8", "s2", "s8;s5;s2");*/


       //map2
        railway.addJourney("j1", "s1", "s11", "s1;s4;s8;s11");
        railway.addJourney("j2", "s1", "s11", "s1;s6;s10;s11");
        railway.addJourney("j3", "s12", "s2", "s12;s9;s3;s2");
        railway.addJourney("j4", "s12", "s2", "s12;s9;s5;s2");


        boolean flag = true;

        while (flag){

//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

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



//            for (int i = 0; i < 2; i++) {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//            }

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
                if (railway.getJourneys().get(i).getState()!=2){
                    counter++;
                }
            }

            for (int i = 0; i < railway.getJourneys().size(); i++) {
                Journey j = railway.getJourneys().get(i);
                System.out.println("Journey "+j.getId()+" block:"+j.getCurrentBlock()+" route:"+j.getCurrentRoute()+" state:"+j.getState());
            }

            railways.add(railway);

            if (counter==0){
                flag=false;
            }

        }

        System.out.println("railways size="+railways.size());

    }
}

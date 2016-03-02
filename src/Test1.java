import java.util.ArrayList;
import java.util.List;

/**
 * CSC8110 Cloud Computing Coursework
 * Created by Yiwei Jing on 01/03/2016 17:19
 * This is my assessment
 * Project name is railway2
 */
public class Test1 {
    public static void main(String[] args) {
        Railway railway = new Railway();

        List<Route> routes = getRoute(railway);

        List<Signal> upSignals = new ArrayList<>();
        List<Signal> downSignals = new ArrayList<>();
        List<Route> upRoutes = new ArrayList<>();
        List<Route> downRoutes = new ArrayList<>();



        for (int i = 0; i < railway.getSignals().size(); i++) {
            Signal signal = railway.getSignals().get(i);
//            System.out.println("Signal "+signal.getName()+" direction: "+signal.getDirection()+"  next: "+signal.getNext()+"  current: "+signal.getCurrentBlock()+"  position: "+signal.getPosition());
            if (signal.getDirection() == 0) {//down
                downSignals.add(signal);
            } else {
                upSignals.add(signal);
            }
        }

        for (int i = 0; i < routes.size(); i++) {
            Route route = routes.get(i);
//            System.out.println("Route  "+route.getId()+" source: "+route.getSource()+" dest: "+route.getDest()+" direction: "+route.getDirection());
            if (route.getDirection() == 0) {//down
                downRoutes.add(route);
            } else {
                upRoutes.add(route);
            }
        }

        railway.setUpRoutes(upRoutes);
        railway.setDownRoutes(downRoutes);
        railway.setRoutes(routes);

        for (int i = 0; i < upRoutes.size(); i++) {
            Route route = upRoutes.get(i);

            String path = getPath(railway, route);
            railway.getUpRoutes().get(i).setPath(path);

            String signal = getSignal(railway, route);
            railway.getUpRoutes().get(i).setSignals(signal);

            String points = getPoint(railway, route);
            railway.getUpRoutes().get(i).setPoints(points);

            System.out.println("Route " + route.getId()+" s:"+route.getSource()+" d:"+route.getDest());
            System.out.println("path = " + path);
            System.out.println("signal = " + signal);
            System.out.println("point = " + points + "\n");
            String conflicts = "";

        }


        for (int i = 0; i < downRoutes.size(); i++) {
            Route route = downRoutes.get(i);

            String path = getPath(railway, route);
            railway.getDownRoutes().get(i).setPath(path);

            String signal = getSignal(railway, route);
            railway.getDownRoutes().get(i).setSignals(signal);

            String points = getPoint(railway, route);
            railway.getDownRoutes().get(i).setPoints(points);

            System.out.println("Route " + route.getId()+" s:"+route.getSource()+" d:"+route.getDest());
            System.out.println("path = " + path);
            System.out.println("signal = " + signal);
            System.out.println("point = " + points + "\n");
            String conflicts = "";

        }

    }

    public static Block getBlockByName(List<Block> blocks, String name) {
        Block block = blocks.get(0);
        for (int i = 0; i < blocks.size(); i++) {
            block = blocks.get(i);
            if (block.getName().equals(name)) {
                break;
            }
        }
        return block;
    }

    public static Signal getSignalByName(List<Signal> signals, String name) {
        Signal signal = signals.get(0);
        for (int i = 0; i < signals.size(); i++) {
            signal = signals.get(i);
            if (signal.getName().equals(name)) {
                break;
            }
        }
        return signal;
    }

    public static List<Route> getRoute(Railway railway){
        List<Route> routes = new ArrayList<>();
        List<Route> upRoutes = new ArrayList<>();
        List<Route> downRoutes = new ArrayList<>();
        List<Signal> upSignals = railway.getUpSignals();
        List<Signal> downSignals = railway.getDownSignals();

        int counter = 1;
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

    public static String getPoint(Railway railway, Route route) {
        String point = "";

        String path = route.getPath();
        String[] paths = path.split(";");
        Block p = new Block();
        boolean pointFlag = true;//true->move into a block between two points    false->move out of a block between two points
        for (int i = 0; i < paths.length; i++) {
            p = getBlockByName(railway.getBlocks(), paths[i]);
            if (p.getType() == 12) {
                pointFlag = true;
                break;
            } else if (p.getType() == 21) {
                pointFlag = false;
                break;
            }
        }

        if (pointFlag) {
            Block dest = getBlockByName(railway.getBlocks(), getSignalByName(railway.getSignals(), route.getDest()).getCurrentBlock());
            String leftPoint = "";
            String rightPoint = "";

            if (route.getDirection()==1){
                leftPoint = dest.getPrevious();
                rightPoint = dest.getNext();
            }else {
                leftPoint = dest.getNext();
                rightPoint = dest.getPrevious();
            }

            if (dest.getType() == 4) {//on PLUS
                point = leftPoint + ":p;" + rightPoint + ":m";
            } else {// on MINUS
                point = leftPoint + ":m;" + rightPoint + ":p";
            }
        } else {
            Block source = getBlockByName(railway.getBlocks(), getSignalByName(railway.getSignals(), route.getSource()).getCurrentBlock());
            if (source.getType() == 4) {//on PLUS
                point = p.getName() + ":p";
            } else {// on MINUS
                point = p.getName() + ":m";
            }
        }


        return point;
    }

    public static String getPath(Railway railway, Route route) {
        Signal source = getSignalByName(railway.getSignals(), route.getSource());
        Signal dest = getSignalByName(railway.getSignals(), route.getDest());

        Block next = getBlockByName(railway.getBlocks(), source.getCurrentBlock());//source block
        String path = "";
//        System.out.println("1  s:" + source.getControllBlock() + "   d:" + dest.getCurrentBlock() + "  " + next.getNext().split(";")[0]);
        if (route.getDirection() == 1) {
            while (!next.getNext().contains(dest.getCurrentBlock())) {
                path += next.getNext() + ";";
//                System.out.println(path);
                next = getBlockByName(railway.getBlocks(), next.getNext().split(";")[0]);
//                System.out.println("2  s:" + source.getControllBlock() + "   d:" + dest.getCurrentBlock() + "  " + next.getNext().split(";")[0]);
            }
        } else {
            while (!next.getPrevious().contains(dest.getCurrentBlock())) {
                path += next.getPrevious() + ";";
//                System.out.println(path);
                next = getBlockByName(railway.getBlocks(), next.getPrevious().split(";")[0]);
//                System.out.println("2  s:" + source.getControllBlock() + "   d:" + dest.getCurrentBlock() + "  " + next.getPrevious().split(";")[0]);
            }
        }
        path += dest.getCurrentBlock();
        return path;
    }

    public static String getSignal(Railway railway, Route route) {
        String signal = "";
        String path = route.getPath();
        String[] paths = path.split(";");
        List<Signal> signals = new ArrayList<>();
        boolean pointFlag = true;//true->pass a 1-2 point   false->pass a 2-1 point
        String oppsiteBlock = "";// record the other side of the block which is between two point  e.g.   if a route leave from b3 and path are p2;b5 record b4   if a route leave from b4 and path are p2;b5 record b3
        for (int i = 0; i < paths.length; i++) {
            Block block = getBlockByName(railway.getBlocks(), paths[i]);
            if (block.getType() > 10) {// the block is a point
                if (block.getType() == 12) {  //1-2 point
                    pointFlag = true;
                } else { // 2-1 point
                    pointFlag = false;
                }
            }

            if (block.getType() == 3 || block.getType() == 4) {// one of blocks is on the MINUS or PLUS
                String next = "";
                if (route.getDirection()==1){
                    next = block.getNext();
                }else {
                    next = block.getPrevious();
                }
                String[] opps = getBlockByName(railway.getBlocks(), next).getPrevious().split(";");
                for (int j = 0; j < opps.length; j++) {
                    if (!opps[j].equals(block.getName())) {
                        oppsiteBlock = opps[j];
                    }
                }
            }
            if (block.getType() == 21) {
                String sourceBlock = getSignalByName(railway.getSignals(), route.getSource()).getCurrentBlock();

                String[] opps = getBlockByName(railway.getBlocks(), block.getName()).getPrevious().split(";");
                for (int j = 0; j < opps.length; j++) {
                    if (!sourceBlock.equals(opps[j])) {
                        oppsiteBlock = opps[j];
                    }
                }
            }
//            System.out.println("Current mid block is "+block.getName()+" and type is "+block.getType()+" and oppsite block is "+oppsiteBlock);

            for (int j = 0; j < railway.getSignals().size(); j++) {
                if (railway.getSignals().get(j).getCurrentBlock().equals(paths[i])) {
                    signals.add(railway.getSignals().get(j));//add all signals which are on the passed blocks
                }
            }
        }

        if (pointFlag) {
            for (int i = 0; i < signals.size(); i++) {
                if (signals.get(i).getDirection() == 0) {
                    signal += signals.get(i).getName() + ";";
                }
            }

            for (int i = 0; i < railway.getSignals().size(); i++) {
                if (railway.getSignals().get(i).getCurrentBlock().equals(oppsiteBlock) && railway.getSignals().get(i).getDirection() != route.getDirection()) {
                    signal += railway.getSignals().get(i).getName();
                }
            }

        } else {
            for (int i = 0; i < railway.getSignals().size(); i++) {
                if (railway.getSignals().get(i).getCurrentBlock().equals(oppsiteBlock) && railway.getSignals().get(i).getDirection() == route.getDirection()) {
                    signal += railway.getSignals().get(i).getName() + ";";
                    break;
                }
            }
        }

        String destBlock = getSignalByName(railway.getSignals(), route.getDest()).getCurrentBlock();//get the dest block name

        for (int i = 0; i < railway.getSignals().size(); i++) {
            if (railway.getSignals().get(i).getControllBlock().equals(destBlock)) {
                signal += railway.getSignals().get(i).getName();
                break;
            }
        }
        return signal;
    }
}

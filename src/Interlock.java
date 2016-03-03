import java.util.List;

/**
 * Created by tommy on 03/03/2016 19:56
 */
public interface Interlock {
    /**
    *  run this network by passing added journeys
    * */
    public List<Railway> running(List<Journey> journeys);

    /**
     *  add a journey by passing the railway object, a id(better to be generated automatically), source signal, dest signal, passby signals
     * */
    public Journey addJourney(Railway railway,String journeyId, String source, String dest, String passby);

    /**
     *  get all routes info by passing filepath of the network file
     * */
    public List<Route> getRoutes(String filepath);

    /**
     *  get all blocks info by passing filepath of the network file
     * */
    public List<Block> getBlocks(String filepath);

    /**
     *  get all signals info by passing filepath of the network file
     * */
    public List<Signal> getSignals(String filepath);
}

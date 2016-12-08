package learning;

/**
 * Created by Fabrice Vanner on 08.12.2016.
 */
public class StopWatch {

    /* Private Instance Variables */
    /**
     * Stores the start time when an object of the StopWatch class is initialized.
     */
    private long startTime;
    private String whatToStop;

    /**
     * Custom constructor which initializes the {@link #startTime} parameter.
     */
    public StopWatch() {
        startTime = System.currentTimeMillis();
    }

    public StopWatch(String str) {
        startTime = System.currentTimeMillis();
        whatToStop =str;
    }
    /**
     * Gets the elapsed time (in seconds) since the time the object of StopWatch was initialized.
     *
     * @return Elapsed time in seconds.
     */
    public double getElapsedTimeSec() {
        long endTime = System.currentTimeMillis();
        return (double) (endTime - startTime) / (1000);
    }
    public long getElapsedTimeMillis() {
        long endTime = System.currentTimeMillis();
        return (endTime - startTime);
    }
    public String getElapsedTimeStrwhatToStop() {
        long endTime = System.currentTimeMillis();
        return "STOP-WATCH:"+ (endTime - startTime)+"ms or "+((endTime - startTime) / (1000)) +"sec FROM "+ whatToStop + " TILL (NOW)";
    }

    public String getElapsedTimeStr() {
        long endTime = System.currentTimeMillis();
        return  (endTime - startTime)+"ms or "+((endTime - startTime) / (1000));
    }
}

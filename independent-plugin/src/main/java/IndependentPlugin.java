import org.joda.time.DateTime;

public class IndependentPlugin implements PluginSpi {
    @Override
    public boolean isDifferenceLongerThanDay(Timestamp x, Timestamp y) {
        // Regardless of the implementation of Timestamp, using joda-time just for logging.
        System.out.println(DateTime.now().toString());

        final long absoluteDiff;
        if (x.getEpoch() < y.getEpoch()) { absoluteDiff = y.getEpoch() - x.getEpoch(); }
        else                             { absoluteDiff = x.getEpoch() - y.getEpoch(); }

        return absoluteDiff >= 60L * 60L * 24L;
    }
}

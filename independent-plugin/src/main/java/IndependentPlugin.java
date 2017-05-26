import org.joda.time.DateTime;

public class IndependentPlugin implements PluginSpi {
    @Override
    public String formatTimestamp(Timestamp timestamp) {
        return new DateTime(timestamp.getEpoch()).toString();
    }
}

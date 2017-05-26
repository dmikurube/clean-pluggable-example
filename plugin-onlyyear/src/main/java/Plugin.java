import org.joda.time.DateTime;

public class Plugin implements PluginSpi {
    @Override
    public String formatTimestamp(Timestamp timestamp) {
        return "" + new DateTime(timestamp.getEpoch() * 1000).getYear();
    }
}

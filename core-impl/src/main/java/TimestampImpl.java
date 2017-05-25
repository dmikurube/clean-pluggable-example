import org.joda.time.DateTime;

public class TimestampImpl implements Timestamp {
    public TimestampImpl(DateTime datetime) {
        this.datetime = datetime;
    }

    @Override
    public long getEpoch() {
        return this.datetime.getMillis() / 1000;
    }

    private final DateTime datetime;
}

package main.java.randall.gamecenter;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author mrzhqiang
 */
public class SimpleMonitor implements Monitor {
    private static final Logger LOGGER = LoggerFactory.getLogger("gamecenter");

    private final long startTime = System.currentTimeMillis();
    private final List<Period> periodList = Lists.newArrayList();

    @Override
    public void record(String name) {
        if (Strings.isNullOrEmpty(name)) {
            return;
        }
        periodList.add(new Period(name, System.currentTimeMillis()));
    }

    @Override
    public void report(String name) {
        long entTime = System.currentTimeMillis();
        long totalTime = entTime - startTime;
        LOGGER.info("The [{}] total time: {}(ms)", name, totalTime);

        for (int i = 0; i < periodList.size(); i++) {
            Period book = periodList.get(i);
            long intervalTime;
            if (i == 0) {
                intervalTime = book.timestamp - startTime;
            } else if (i == periodList.size() - 1) {
                intervalTime = entTime - book.timestamp;
            } else {
                intervalTime = periodList.get(i + 1).timestamp - book.timestamp;
            }
            LOGGER.info("The [{}] >>> [{}] time: {}(ms)", name, book.name, intervalTime);
        }
        periodList.clear();
    }

    private static final class Period {
        final String name;
        final long timestamp;

        private Period(String name, long timestamp) {
            this.name = name;
            this.timestamp = timestamp;
        }
    }
}

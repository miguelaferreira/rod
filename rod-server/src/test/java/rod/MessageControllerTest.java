package rod;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import rx.Observable;
import rx.observables.ConnectableObservable;
import ch.qos.logback.classic.Logger;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RodServer.class)
public class MessageControllerTest {

    private TestAppender testAppender;

    @Autowired
    private MessageController messageController;

    @Before
    public void setupLogsForTesting() {
        final Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        testAppender = (TestAppender) root.getAppender("TEST");
        if (testAppender != null) {
            testAppender.clear();
        }
    }

    @Test
    public void testName() throws Exception {
        final ConnectableObservable<Long> interval = Observable.interval(1, TimeUnit.SECONDS).publish();
        interval.connect();
        interval.subscribe(v -> System.err.println("First subscription: " + v));
        Thread.sleep(2000);
        interval.subscribe(v -> System.err.println("Second subscription: " + v));
        Thread.sleep(10000);
    }
}

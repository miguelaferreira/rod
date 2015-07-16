package rod;

import static com.jayway.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.equalTo;

import java.io.InputStream;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import rx.schedulers.Schedulers;

public class RabbitmqAdapterIT {

    private static final Logger logger = LoggerFactory.getLogger(RabbitmqAdapterIT.class);
    private static final ApplicationContext context = new AnnotationConfigApplicationContext(RabbitmqTestConfiguration.class);
    private static final AmqpTemplate template = context.getBean(AmqpTemplate.class);

    private final RabbitmqAdapter adapter = new RabbitmqAdapter();

    @BeforeClass
    public static void setupClass() throws Exception {
        final String propertyName = "rod.build.env";
        final String property = System.getProperty(propertyName);
        logger.debug("Value of {} is {}", propertyName, property);
        final String propertiesFile = property != null && property.equals("travis") ? "rabbitmq.travis.properties" : "rabbitmq.local.properties";
        final InputStream testProperties = Thread.currentThread().getContextClassLoader().getResourceAsStream(propertiesFile);
        System.getProperties().load(testProperties);
    }

    @Before
    public void setup() throws Exception {
        logger.debug("Setting up test");
        if (isRunning()) {
            logger.debug("RabbitMQ is running and will be shutdown before executing test");
            adapter.stopServer();
            adapter.getStopOutput().subscribeOn(Schedulers.io()).subscribe(logger::debug);
            waitForServer(false);
        }
    }

    @Test
    public void testStartStopRabbitmq() throws Exception {
        adapter.startServer();
        adapter.getStartOutput().subscribeOn(Schedulers.io()).subscribe(logger::debug);
        waitForServer(true);

        adapter.stopServer();
        adapter.getStopOutput().subscribeOn(Schedulers.io()).subscribe(logger::debug);
        waitForServer(false);
    }

    private static void waitForServer(final boolean toBeRunning) {
        await().atMost(5, SECONDS).until(() -> RabbitmqAdapterIT.isRunning(), equalTo(toBeRunning));
    }

    private static boolean isRunning() {
        try {
            template.convertAndSend("testqueue", "foo");
        } catch (final Exception e) {
            logger.trace("Caught exception checking if server is running", e);
            return false;
        }
        return true;
    }
}

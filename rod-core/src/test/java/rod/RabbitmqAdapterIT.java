package rod;

import static com.jayway.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class RabbitmqAdapterIT {

    private static final Logger logger = LoggerFactory.getLogger(TerminalCommand.class);
    private static final ApplicationContext context = new AnnotationConfigApplicationContext(RabbitmqTestConfiguration.class);
    private static final AmqpTemplate template = context.getBean(AmqpTemplate.class);

    private final RabbitmqAdapter adapter = new RabbitmqAdapter();

    @BeforeClass
    public static void setupClass() throws Exception {
        final String property = System.getProperty("rod.build.env");
        final String propertiesFile = property != null && property.equals("travis") ? "rabbitmq.travis.properties" : "rabbitmq.local.properties";
        final InputStream testProperties = Thread.currentThread().getContextClassLoader().getResourceAsStream(propertiesFile);
        System.getProperties().load(testProperties);
    }

    @Before
    public void setup() throws Exception {
        if (isRunning()) {
            adapter.stopServer();
            waitForServer(false);
        }
    }

    @After
    public void teardown() {
        adapter.stopServer();
    }

    @Test
    public void testStartStopRabbitmq() throws Exception {
        assertThat(isRunning(), equalTo(false));

        adapter.startServer();
        adapter.getOutput().subscribe(System.out::println);
        waitForServer(true);

        adapter.stopServer();
        waitForServer(false);
    }

    private static void waitForServer(final boolean toBeRunning) {
        await().atMost(15, SECONDS).until(() -> RabbitmqAdapterIT.isRunning(), equalTo(toBeRunning));
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

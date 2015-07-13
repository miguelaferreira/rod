package rod;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class RabbitmqStopServerCommandTest {

    @Injectable
    Runtime runtime;

    @Injectable
    Process process;

    InputStream processInputStream = new ByteArrayInputStream("Test".getBytes());

    @Test
    public void testExecuteCommand() throws Exception {

        new Expectations() {
            {
                runtime.exec((String[]) any);
                result = process;

                process.getInputStream();
                result = processInputStream;
            }
        };

        final RabbitmqStopServerCommand command = new RabbitmqStopServerCommand.Builder()
                .command(System.getProperty("rod.command.rabbitmqserver"))
                .withArgument(System.getProperty("rod.command.rabbitmqserver.args"))
                .runtime(runtime)
                .build();
        command.execute();
        final List<String> first = command.getOutput().toList().toBlocking().first();

        assertThat(first, hasItem("Test"));
    }

}

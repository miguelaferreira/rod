package rod;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;

import java.util.List;

import org.junit.Test;

public class TerminalCommandTest {

    @Test
    @SuppressWarnings("rawtypes")
    public void testExecute() throws Exception {
        final TestCommand testCommand = new TestCommand(
                new TerminalCommand.Builder()
                        .command("/bin/ls")
                        .withArgument("/")
                        .runtime(Runtime.getRuntime()));
        testCommand.execute();

        final List<String> output = testCommand.getOutput().toList().toBlocking().first();

        assertThat(output, hasItem("bin"));
    }

}

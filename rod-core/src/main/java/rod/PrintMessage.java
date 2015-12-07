package rod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrintMessage implements Action {

    private static final Logger logger = LoggerFactory.getLogger(PrintMessage.class);
    private final String message;

    public PrintMessage(final String message) {
        this.message = message;
    }

    @Override
    public void execute() {
        logger.info("[Message] {}", message);
    }
}

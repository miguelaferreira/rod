package rod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageService extends BaseRodService {

    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    @Override
    public void onCompleted() {
        logger.error("No more messages to process.");
    }

    @Override
    public void onError(final Throwable e) {
        logger.error("Message pipeline experienced an error.");
    }

    @Override
    public void onNext(final Observation t) {
        logger.info("New message: {}", t.toString());

        onNext(new PrintMessage(t.toString()));
    }

}

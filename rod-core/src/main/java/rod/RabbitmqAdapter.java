package rod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import rx.Observable;

@Service
public class RabbitmqAdapter {

    private static final Logger logger = LoggerFactory.getLogger(RabbitmqAdapter.class);
    private final static Runtime runtime = Runtime.getRuntime();
    private final static boolean rabbitmqCommandSudo = RodProperties.isRabbitmqCommandSudo();

    private final RabbitmqStartServerCommand startServerCommand;
    private final RabbitmqStopServerCommand stopServerCommand;

    public RabbitmqAdapter() {
        logger.info("RabbitMQ command sudo: {}", rabbitmqCommandSudo);
        startServerCommand = new RabbitmqStartServerCommand.Builder()
                .command(RodProperties.getRabbitmqServerCommand())
                .withArguments(RodProperties.getRabbitmqServerCommandArguments())
                .sudo(rabbitmqCommandSudo)
                .runtime(runtime)
                .build();
        stopServerCommand = new RabbitmqStopServerCommand.Builder()
                .command(RodProperties.getRabbitmqCtlCommand())
                .sudo(rabbitmqCommandSudo)
                .runtime(runtime)
                .build();
    }

    public void startServer() {
        logger.info("Executing command to start RabbitMQ");
        startServerCommand.execute();
    }

    public Observable<String> getStartOutput() {
        return startServerCommand.getOutput();
    }

    public void stopServer() {
        logger.info("Executing command to stop RabbitMQ");
        stopServerCommand.execute();
    }

    public Observable<String> getStopOutput() {
        return stopServerCommand.getOutput();
    }
}

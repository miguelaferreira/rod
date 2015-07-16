package rod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RodProperties {

    private static final Logger logger = LoggerFactory.getLogger(RodProperties.class);

    public static final String ROD_COMMAND_RABBITMQSERVER = "rod.command.rabbitmqserver";
    public static final String ROD_COMMAND_RABBITMQSERVER_ARGUMENTS = "rod.command.rabbitmqserver.args";
    public static final String ROD_COMMAND_RABBITMQCTL = "rod.command.rabbitmqctl";
    public static final String ROD_COMMAND_RABBITMQ_SUDO = "rod.command.rabbitmq.sudo";

    public static String getRabbitmqServerCommand() {
        return System.getProperty(ROD_COMMAND_RABBITMQSERVER);
    }

    public static String[] getRabbitmqServerCommandArguments() {
        final String property = System.getProperty(ROD_COMMAND_RABBITMQSERVER_ARGUMENTS);
        if (property != null) {
            return property.split(" ");
        } else {
            return new String[] {};
        }
    }

    public static String getRabbitmqCtlCommand() {
        return System.getProperty(ROD_COMMAND_RABBITMQCTL);
    }

    public static boolean isRabbitmqCommandSudo() {
        return Boolean.getBoolean(ROD_COMMAND_RABBITMQ_SUDO);
    }

}

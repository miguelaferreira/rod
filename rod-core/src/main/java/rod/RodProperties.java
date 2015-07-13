package rod;

public class RodProperties {

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
        final String property = System.getProperty(ROD_COMMAND_RABBITMQ_SUDO);
        return property != null && Boolean.getBoolean(property);
    }

}

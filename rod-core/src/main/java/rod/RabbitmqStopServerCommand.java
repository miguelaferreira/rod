package rod;

public class RabbitmqStopServerCommand extends TerminalCommand {

    private RabbitmqStopServerCommand(final Builder builder) {
        super(builder);
    }

    public static final class Builder extends rod.TerminalCommand.Builder<Builder> {
        private static final String CTL_SUBCOMMAND_STOP = "stop";

        public RabbitmqStopServerCommand build() {
            withArgument(CTL_SUBCOMMAND_STOP);
            return new RabbitmqStopServerCommand(this);
        }
    }

}

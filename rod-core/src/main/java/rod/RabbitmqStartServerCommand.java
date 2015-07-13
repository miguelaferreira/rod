package rod;

public class RabbitmqStartServerCommand extends TerminalCommand {

    private RabbitmqStartServerCommand(final Builder builder) {
        super(builder);
    }

    public static final class Builder extends rod.TerminalCommand.Builder<Builder> {
        public RabbitmqStartServerCommand build() {
            return new RabbitmqStartServerCommand(this);
        }
    }

}

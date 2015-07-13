package rod;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observable;
import rx.observables.StringObservable;
import rx.subjects.PublishSubject;

public abstract class TerminalCommand implements Command {

    private static final Logger logger = LoggerFactory.getLogger(TerminalCommand.class);

    private final String command;
    private final List<String> commandArguments;
    private final Runtime runtime;
    private final PublishSubject<String> output = PublishSubject.create();

    protected TerminalCommand(final Builder<?> builder) {
        command = builder.command;
        commandArguments = builder.commandArguments;
        runtime = builder.runtime;
    }

    @Override
    public void execute() {
        Executors.newSingleThreadExecutor().execute(this::runFunction);
    }

    public Observable<String> getOutput() {
        return output.asObservable();
    }

    private void runFunction() {
        final ArrayList<String> commandBuilder = new ArrayList<>(commandArguments.size() + 1);
        commandBuilder.add(command);
        commandBuilder.addAll(commandArguments);
        try {
            final Process process = runtime.exec(commandBuilder.toArray(new String[commandBuilder.size()]));
            final InputStreamReader processOutputReader = new InputStreamReader(process.getInputStream());
            StringObservable.from(processOutputReader)
                    .flatMap(s -> Observable.from(s.split("\n")))
                    .subscribe(output);
        } catch (final IOException e) {
            logger.error("Failed to run command " + this.getClass().getName(), e);
        }
    }

    @SuppressWarnings({ "unchecked" })
    public static class Builder<T extends Builder<?>> {
        private String command;
        private final List<String> commandArguments = new ArrayList<>();
        private Runtime runtime;

        public T command(final String command) {
            this.command = command;
            return (T) this;
        }

        public T withArgument(final String argument) {
            commandArguments.add(argument);
            return (T) this;
        }

        public T withArguments(final String... arguments) {
            commandArguments.addAll(Arrays.asList(arguments));
            return (T) this;
        }

        public T runtime(final Runtime runtime) {
            this.runtime = runtime;
            return (T) this;
        }
    }

}

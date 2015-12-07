package rod;

public class DummyAnalyzer implements Transformation<Action, Observation> {

    private ObservationToCommandMapping observationToCommand;

    public void setObservationToCommand(final ObservationToCommandMapping observationToCommand) {
        this.observationToCommand = observationToCommand;
    }

    @Override
    public Action analyze(final Observation observation) throws UnrecognizableObservationException {
        if (observationToCommand.containsKey(observation.getClass())) {
            return observationToCommand.get(observation.getClass()).build();
        } else {
            throw new UnrecognizableObservationException(observation);
        }
    }

}

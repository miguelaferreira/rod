package rod;

public interface Transformation<B, A> {

    public B analyze(A observation) throws UnrecognizableObservationException;

}

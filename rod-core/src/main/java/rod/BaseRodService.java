package rod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public abstract class BaseRodService implements RodService {

    private static final Logger logger = LoggerFactory.getLogger(BaseRodService.class);

    private final PublishSubject<Action> commands = PublishSubject.create();

    public void onNext(final Action a) {
        commands.onNext(a);
    }

    @Override
    public void register(final Resource resource) {
        resource.observe().observeOn(Schedulers.computation()).subscribe(this);
    }

    @Override
    public void onCompleted() {
        logger.info("No more observations to analyze");
    }

    @Override
    public void onError(final Throwable e) {
        logger.error("Caught error while observing: {}", e);
        commands.onError(e);
    }

    @Override
    public Observable<Action> commands() {
        return commands.asObservable();
    }

}

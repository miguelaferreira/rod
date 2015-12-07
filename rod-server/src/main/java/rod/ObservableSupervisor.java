package rod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.schedulers.Schedulers;

@Component
public class ObservableSupervisor {

    private static final Logger logger = LoggerFactory.getLogger(ObservableSupervisor.class);

    private final Map<String, Subscription> subscriptions = new HashMap<>();

    public <T> void supervise(final Observable<T> observable, final String observableId) {
        if (subscriptions.containsKey(observableId)) {
            logger.warn("Already supervising {}", observableId);
        } else {
            logger.info("Supervising {}", observableId);
            setupSupervision(observable, observableId);
        }
    }

    public <T> void updateSupervision(final Observable<T> observable, final String observableId) {
        if (subscriptions.containsKey(observableId)) {
            subscriptions.get(observableId).unsubscribe();
            setupSupervision(observable, observableId);
        } else {
            logger.error("Not supervising {}", observableId);
        }
    }

    private <T> void setupSupervision(final Observable<T> observable, final String observableId) {
        final Subscription subscription = observable.buffer(1, TimeUnit.SECONDS, Schedulers.computation())
            .subscribeOn(Schedulers.io())
            .subscribe(new SupervisorObserver<T>(observableId));
        subscriptions.put(observableId, subscription);
    }

    private class SupervisorObserver<T> implements Observer<List<T>> {

        private final String observableId;

        public SupervisorObserver(final String observableId) {
            this.observableId = observableId;
        }

        @Override
        public void onCompleted() {
            logger.info("Observable {} completed. Supervision ended", observableId);
            subscriptions.get(observableId).unsubscribe();
            subscriptions.remove(observableId);
        }

        @Override
        public void onError(final Throwable e) {
            logger.error("Observable " + observableId + " generated an error: " + e.getMessage(), e);
        }

        @Override
        public void onNext(final List<T> t) {
            logger.info("Observable {} emmits {} per seccond ", observableId, t.size());
        }
    }

}

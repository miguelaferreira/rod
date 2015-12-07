package rod;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

@Controller
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private ObservableSupervisor supervisor;

    private final Map<String, Observable<String>> stringStreams = new HashMap<>();
    private final Map<Long, Observable<Long>> longStreams = new HashMap<>();
    private final Map<Observable<?>, Subscription> subscriptions = new HashMap<>();

    private final PublishSubject<String> errors = PublishSubject.create();

    private final Action1<? super String> printStrinElement = s -> logger.info("[" + s + "]");

    public MessageController() {
        errors.subscribeOn(Schedulers.io()).subscribe(logger::error);
    }

    private static Func1<? super Long, ? extends String> makeStringStream(final String symbol) {
        return x -> symbol;
    }

    private static String makeStreamName(final String symbol) {
        return "Stream " + symbol;
    }

    @RequestMapping(value = "/message/string/create/{symbol}")
    public @ResponseBody void stringCreate(@PathVariable final String symbol) {
        setupStringObservable(symbol, 1000);
    }

    private Observable<String> setupStringObservable(final String symbol, final long interval) {
        final Observable<String> observable = Observable.interval(interval, TimeUnit.MILLISECONDS).map(makeStringStream(symbol));
        final Subscription subscription = observable.subscribeOn(Schedulers.io()).subscribe(printStrinElement);
        subscriptions.put(observable, subscription);
        stringStreams.put(symbol, observable);
        return observable;
    }

    @RequestMapping(value = "/message/string/frequency/{symbol}/{interval}")
    public @ResponseBody void stringFrequency(@PathVariable final String symbol, @PathVariable final Long interval) {
        if (stringStreams.containsKey(symbol)) {
            final Observable<String> oldObservable = stringStreams.get(symbol);
            subscriptions.get(oldObservable).unsubscribe();
            final Observable<String> observable = setupStringObservable(symbol, interval);
            supervisor.updateSupervision(observable, makeStreamName(symbol));
        } else {
            errors.onNext("Requested stream '" + symbol + "' does not exist");
        }
    }

    @RequestMapping(value = "/message/string/supervise/{symbol}")
    public @ResponseBody void stringSupervise(@PathVariable final String symbol) {
        if (stringStreams.containsKey(symbol)) {
            supervisor.supervise(stringStreams.get(symbol), makeStreamName(symbol));
        } else {
            errors.onNext("Requested stream '" + symbol + "' does not exist");
        }
    }

    @RequestMapping(value = "/message/string/transform/{source}/{target}")
    public @ResponseBody void stringTransform(@PathVariable final String source, @PathVariable final String target) {
        if (stringStreams.containsKey(source)) {
            final Observable<String> sourceObservable = stringStreams.get(source);
            final Observable<String> targetObservable = sourceObservable.map(s -> (s + " -> " + target));
            final Subscription subscription = targetObservable.subscribeOn(Schedulers.io()).subscribe(s -> logger.info("[" + s + "]"));
            stringStreams.put(target, targetObservable);
            subscriptions.put(targetObservable, subscription);
        } else {
            errors.onNext("Requested source '" + source + "' does not exist");
        }
    }

    @RequestMapping(value = "/message/int/create/{value}")
    public @ResponseBody void intCreate(@PathVariable final Long value) {
        final ConnectableObservable<Long> repeat = Observable.interval(1, TimeUnit.SECONDS)
            .map(v -> v + value)
            .subscribeOn(Schedulers.io())
            .publish();
        repeat.connect();
        repeat.subscribe(s -> logger.info("[" + value + "]<" + s + ">"));
        longStreams.put(value, repeat);
    }

    @RequestMapping(value = "/message/int/add/{source}/{target}")
    public @ResponseBody void intAdd(@PathVariable final Long source, @PathVariable final Long target) {
        if (longStreams.containsKey(source)) {
            final Observable<Long> sourceObservable = longStreams.get(source);
            final Observable<Long> targetObservable = sourceObservable.map(s -> s + target)
                .subscribeOn(Schedulers.io());
            targetObservable.subscribe(s -> logger.info("[" + source + " + " + target + "]<" + s + ">"));
            longStreams.put(target, targetObservable);
        } else {
            errors.onNext("Requested source '" + source + "' does not exist");
        }
    }

    @RequestMapping(value = "/message/int/remove/{source}/{target}")
    public @ResponseBody void intRemove(@PathVariable final Long source, @PathVariable final Long target) {
        if (longStreams.containsKey(source)) {
            final Observable<Long> sourceObservable = longStreams.get(source);
            final Observable<Long> targetObservable = sourceObservable.map(s -> s - target);
            targetObservable.subscribeOn(Schedulers.io()).subscribe(s -> logger.info("[" + source + " - " + target + "]<" + s + ">"));
            longStreams.put(target, targetObservable);
        } else {
            errors.onNext("Requested source '" + source + "' does not exist");
        }
    }
}

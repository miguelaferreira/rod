package rod;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.camel.component.dataset.SimpleDataSet;
import org.junit.Test;

import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MessagePipelineTest {

    @Test
    public void testMapMultipleFunctions() {
        final Observable<String> observable = Observable.from(new String[] { "Message 1", "Message 2", "Message 3" });

        final Func1<String, String> decript = m -> "Decripted [" + m + "]";
        final Func1<String, String> transform = m -> "Transformed [" + m + "]";
        final Func1<String, String> persist = m -> "Persisted [" + m + "]";

        observable.subscribeOn(Schedulers.io())
            .map(decript)
            .map(transform)
            .map(persist)
            .subscribe(System.out::println);
    }

    @Test
    public void testMultipleSubscriptions() throws Exception {
        final Observable<String> observable = Observable.from(new String[] { "Message 1", "Message 2", "Message 3" });

        observable.subscribeOn(Schedulers.computation());

        final Observable<String> observable2 = observable.observeOn(Schedulers.io());

        observable.map(t -> "Original " + t).subscribe(System.out::println);
        observable2.subscribe(System.out::println);
    }

    @Test
    public void testName() throws Exception {
        // final DefaultCamelContext createContext = new DefaultCamelContext();
        // final SimpleRegistry registry = new SimpleRegistry();
        // registry.put("myDataSet", new MyDataSet());
        // createContext.setRegistry(registry);
        // final ReactiveCamel rx = new ReactiveCamel(createContext);
        // // rx.toObservable("dataset:myDataSet")
        // rx.toObservable("file:message-inbox/message-1.txt")
        // .subscribeOn(Schedulers.io())
        // .map(m -> "Hello " + m.getBody(String.class))
        // .subscribe(System.err::println);
        //
        // Thread.sleep(10000);
    }

    class MyDataSet extends SimpleDataSet {
        public MyDataSet() {
            super(10);
        }
    }

    @Test
    public void testPipeLine() throws Exception {
        final Observable<Long> source = Observable.interval(1, TimeUnit.SECONDS);
        final Observable<String> t1 = source.subscribeOn(Schedulers.computation()).map(n -> "Number " + n);
        final Observable<String> t2 = source.subscribeOn(Schedulers.computation()).map(n -> "[ " + n + " ]");

        t1.subscribeOn(Schedulers.io()).subscribe(System.err::println);
        t2.subscribeOn(Schedulers.io()).subscribe(System.err::println);

        System.in.read();
    }

    @Test
    public void testWindow() throws Exception {
        final Observable<Long> source = Observable.interval(500, TimeUnit.MILLISECONDS);
        source.subscribeOn(Schedulers.io()).subscribe(x -> System.err.println("Source: " + x));

        final Observable<Observable<Long>> window = source.window(2);
        window.subscribeOn(Schedulers.io()).subscribe(x -> x.subscribeOn(Schedulers.io()).subscribe(y -> System.err.println("WindowElement: " + y)));
        window.subscribeOn(Schedulers.io()).subscribe(x -> System.err.println("Window first: " + x.subscribeOn(Schedulers.io()).toBlocking().first()));

        Thread.sleep(4000);
    }

    @Test
    public void testBuffer() throws Exception {
        final Observable<Long> source = Observable.interval(500, TimeUnit.MILLISECONDS);
        source.subscribeOn(Schedulers.io()).subscribe(x -> System.err.println("Source: " + x));

        final Observable<List<Long>> buffer = source.buffer(2);
        buffer.subscribeOn(Schedulers.io()).subscribe(x -> System.err.println("Buffer first: " + x.get(0)));

        Thread.sleep(4000);
    }
}

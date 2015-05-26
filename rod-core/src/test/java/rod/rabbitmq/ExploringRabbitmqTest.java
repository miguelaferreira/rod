package rod.rabbitmq;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import rx.Observable;
import rx.Observer;

public class ExploringRabbitmqTest {

    @Test
    public void test() {
        ApplicationContext context = new AnnotationConfigApplicationContext(RabbitConfiguration.class);
        AmqpTemplate template = context.getBean(AmqpTemplate.class);

        template.convertAndSend("myqueue", "foo");

        final List<String> messages = new ArrayList<>();

        Observable.timer(0, 1, TimeUnit.SECONDS)
                .map(tick -> template.receive("myqueue"))
                .subscribe(new Observer<Message>() {

                    @Override
                    public void onCompleted() {
                        System.out.println("Complete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Message t) {
                        String text = new String(t.getBody());
                        messages.add(text);
                        System.out.println("Current is: " + text);
                        System.out.println("Moving on to next");
                    }
                });

        assertThat(messages, hasSize(1));
        assertThat(messages, hasItem(equalTo("foo")));
    }
}

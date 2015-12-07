package rod;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = "rod")
public class RodServer {

    public static void main(final String[] args) {
        SpringApplication.run(RodServer.class, args);
    }

    /*
     * TODO: This bean is here to satisfy an @Autowired annotation in the core. That annotation is there as a PoC for allowing users to determine the mapping between observations
     * and commands. Once a better way to define this mapping exists, e.g. include that in the domain model, the annotation will no longer be necessary as well as this bean.
     */
    @Bean
    public DummyAnalyzer getDummyAnalyzer() {
        final DummyAnalyzer dummyAnalyzer = new DummyAnalyzer();
        final ObservationToCommandMapping observationToCommand = new ObservationToCommandMapping();
        observationToCommand.put(DummyObservation.class, new NopCommandFactory());
        dummyAnalyzer.setObservationToCommand(observationToCommand);
        return dummyAnalyzer;
    }

}

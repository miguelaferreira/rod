package rod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DummyRodService extends BaseRodService {

    private static final Logger logger = LoggerFactory.getLogger(DummyRodService.class);

    @Autowired
    private DummyAnalyzer dummyAnalyzer;

    @Override
    public void onNext(final Observation observation) {
        try {
            final Action command = dummyAnalyzer.analyze(observation);
            logger.info("Dummy analysis produced command: {}", command);
            onNext(command);
        } catch (final UnrecognizableObservationException e) {
            logger.warn("Dummy analyzer cannot recognize commands of class {}: {}", observation.getClass(), e);
        }
    }

}

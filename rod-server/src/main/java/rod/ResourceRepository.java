package rod;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.stereotype.Repository;

@Repository
public class ResourceRepository {

    private final Map<String, Resource> resources = new HashMap<>();
    private final GaugeService gaugeService;

    @Autowired
    public ResourceRepository(final GaugeService gaugeService) {
        this.gaugeService = gaugeService;
        gaugeService.submit("gauge.repository.resource.count", resourceCount());
    }

    public void save(final Resource resource) {
        resources.put(resource.getName(), resource);
        gaugeService.submit("gauge.repository.resource.count", resourceCount());
    }

    public int resourceCount() {
        return resources.size();
    }
}

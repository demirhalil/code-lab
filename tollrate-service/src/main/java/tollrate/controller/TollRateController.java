package tollrate.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tollrate.domain.TollRate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RestController
public class TollRateController {
    private final List<TollRate> tollRates;
    private final Logger logger = LoggerFactory.getLogger(TollRateController.class);

    public TollRateController() {
        tollRates = new ArrayList<>();
        tollRates.add(new TollRate(1000, 0.55f, Instant.now().toString()));
        tollRates.add(new TollRate(1001, 1.05f, Instant.now().toString()));
        tollRates.add(new TollRate(1002, 0.60f, Instant.now().toString()));
        tollRates.add(new TollRate(1003, 1.00f, Instant.now().toString()));
    }

    @RequestMapping("/tollrate/{stationId}")
    public TollRate getTollRate(@PathVariable int stationId) {
        logger.info("Station requested: {}", stationId);
        return tollRates.stream().filter(tollRate -> stationId == tollRate.stationId()).findAny().get();
    }
}

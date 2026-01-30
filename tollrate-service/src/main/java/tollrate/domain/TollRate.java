package tollrate.domain;

public record TollRate(
        Integer stationId,
        Float currentRate,
        String timestamp
) { }

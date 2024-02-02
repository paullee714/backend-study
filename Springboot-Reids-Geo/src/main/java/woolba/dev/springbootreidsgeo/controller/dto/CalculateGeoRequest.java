package woolba.dev.springbootreidsgeo.controller.dto;

public record CalculateGeoRequest(
        String compareAreaName,
        double longitude,
        double latitude
) {
}

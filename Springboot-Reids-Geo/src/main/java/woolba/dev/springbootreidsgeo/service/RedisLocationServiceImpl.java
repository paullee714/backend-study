package woolba.dev.springbootreidsgeo.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.domain.geo.GeoLocation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RedisLocationServiceImpl implements RedisLocationService {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public RedisLocationServiceImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void addLocation(String name, double longitude, double latitude) {
        log.info("addLocation: name={}, longitude={}, latitude={}", name, longitude, latitude);
        var geoOps = redisTemplate.opsForGeo();
        geoOps.add("locations", new Point(latitude, longitude), name);
    }

    @Override
    public List<GeoLocation<String>> readAllLocation() {
        GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();

        // Using a large radius to cover the earth, centering the search at 0,0 and specifying a large enough radius in kilometers
        Circle earth = new Circle(new Point(0, 0), new Distance(20000, Metrics.KILOMETERS)); // Earth's radius is approximately 6371km, so 20000 should cover it all

        // Fetch all points. Note: This might return member names and distances as well, depending on your needs.
        List<GeoLocation<String>> points = geoOps.radius("locations", earth, RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeCoordinates())
                .getContent().stream()
                .map(position -> position.getContent())
                .collect(Collectors.toList());
        log.info("readAllLocation: points={}", points);
        return points;
    }

    @Override
    public List<Point> readLocation(String name) {
        log.info("readLocation: name={}", name);
        var geoOps = redisTemplate.opsForGeo();
        var point = geoOps.position("locations", name);
        return point;
    }

    @Override
    public Double calculateDistanceLonLatWithPointName(String compareAreaName, double longitude, double latitude) {
        log.info("Compare Location Between Name {} and Point {}", compareAreaName, new Point(latitude, longitude));
        var geoOps = redisTemplate.opsForGeo();
        // Calculate distance between two points
        var point = geoOps.position("locations", compareAreaName);
        var comparePoint = geoOps.add("locations", new Point(latitude, longitude), "comparePoint-tmp");
        var distance = geoOps.distance("locations", compareAreaName, "comparePoint-tmp", Metrics.KILOMETERS);
        log.info("Distance: {} ", distance);
        // Clean up
        geoOps.remove("locations", "comparePoint-tmp");
        return distance.getValue();
    }

    @Override
    public void removeLocation(String name) {
        var geoOps = redisTemplate.opsForGeo();
        geoOps.remove("locations", "comparePoint-tmp");

    }
}

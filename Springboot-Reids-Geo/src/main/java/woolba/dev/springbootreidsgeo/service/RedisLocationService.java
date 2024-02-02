package woolba.dev.springbootreidsgeo.service;

import org.springframework.data.geo.Point;
import org.springframework.data.redis.domain.geo.GeoLocation;
import org.springframework.stereotype.Service;

import java.util.List;

public interface RedisLocationService {

    // Add a location to the Redis database
    public void addLocation(String name, double longitude, double latitude);

    // Read all Location from the Redis database
    public List<GeoLocation<String>> readAllLocation();

    // Read a Location from the Redis database
    public List<Point> readLocation(String name);

    // Read a location from the Redis database with lon,lat
    public Double calculateDistanceLonLatWithPointName(String compareAreaName, double longitude, double latitude);

    // Remove a location from the Redis database
    public void removeLocation(String name);
}

package woolba.dev.springbootreidsgeo.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.domain.geo.GeoLocation;
import org.springframework.web.bind.annotation.*;
import woolba.dev.springbootreidsgeo.controller.dto.AddGeoRequest;
import woolba.dev.springbootreidsgeo.controller.dto.CalculateGeoRequest;
import woolba.dev.springbootreidsgeo.service.RedisLocationService;

import java.util.List;


@RestController
public class RedisGeoController {

    RedisLocationService redisLocationService;

    public RedisGeoController(RedisLocationService redisLocationService) {
        this.redisLocationService = redisLocationService;
    }

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @GetMapping("/hello")
    public String hello() {
        log.info("Hello, Redis Geo!");
        return "Hello, Redis Geo!";
    }

    @PostMapping("/add-geo")
    public String addGeo(@RequestBody AddGeoRequest request) {
        log.info("Add Geo");
        redisLocationService.addLocation(
                request.name(),
                request.longitude(),
                request.latitude()
        );
        return "Add Geo";
    }

    @GetMapping("/read-geo")
    public List<GeoLocation<String>> readGeo() {
        log.info("Read Geo");
        var result = redisLocationService.readAllLocation();
        return result;
    }

    @GetMapping("/read-geo/{name}")
    public List<Point> readGeo(@PathVariable String name) {
        log.info("Read Geo by name");
        var result = redisLocationService.readLocation(name);
        return result;
    }

    @PostMapping("/calculate-distance")
    public Double calculateDistance(@RequestBody CalculateGeoRequest request) {
        log.info("Calculate Distance");
        var result = redisLocationService.calculateDistanceLonLatWithPointName(request.compareAreaName(),request.longitude(), request.latitude());
        return result;
    }

    @DeleteMapping("/remove-geo/{name}")
    public String removeGeo(@PathVariable String name) {
        log.info("Remove Geo");
        redisLocationService.removeLocation(name);
        return "Remove Geo";
    }

}

package demo.rest;

import demo.model.GpsSimulatorRequest;
import demo.model.SimulatorInitLocations;
import demo.service.GpsSimulatorFactory;
import demo.service.PathService;
import demo.task.LocationSimulator;
import demo.task.LocationSimulatorInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/api")
public class LocationSimulatorRestApi {

    @Autowired
    private GpsSimulatorFactory gpsSimulatorFactory;

    @Autowired
    private AsyncTaskExecutor taskExecutor;

    @Autowired
    private PathService pathService;

    private Map<Long, LocationSimulatorInstance> taskFutures = new HashMap<>();

    // 1. LloadSimulatorInitLocaton
    // 2. Transform domain model simulator request to a class taht can be executed by taskExecutor
    //3. taskExecutor.submit(simulator)
    //4. simulation

    @RequestMapping("/simulation")
    public List<LocationSimulatorInstance> simulation(){
        final SimulatorInitLocations fixture = this.pathService.loadSimulatorInitLocations();

        final List<LocationSimulatorInstance> instances = new ArrayList<>();

        for(GpsSimulatorRequest gpsSimulatorRequest : fixture.getGpsSimulatorRequests()){
            final LocationSimulator locationSimulator = gpsSimulatorFactory.prepareGpsSimulator(gpsSimulatorRequest);

            final Future<?> future = taskExecutor.submit(locationSimulator);

            final LocationSimulatorInstance instance = new LocationSimulatorInstance(locationSimulator.getId(), locationSimulator, future);
            taskFutures.put(locationSimulator.getId(), instance);
            instances.add(instance);
        }

        return instances;
    }

    @RequestMapping("/cancel")
    public int cancel(){
        int numberOfCancelledTasks = 0;
        for (Map.Entry<Long, LocationSimulatorInstance> entry : taskFutures.entrySet()){
            LocationSimulatorInstance instance = entry.getValue();
            instance.getLocationSimulator().cancel();
            boolean wasCcancelled = instance.getLocationSimulatorTask().cancel(true);
            if (wasCcancelled){
                numberOfCancelledTasks++;
            }

        }
        taskFutures.clear();
        return numberOfCancelledTasks;
    }
}

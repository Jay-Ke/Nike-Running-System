package demo.rest;

import demo.domain.Location;
import demo.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RunningUploadRestController {

    private LocationService locationService;

    @Autowired
    public RunningUploadRestController(LocationService locationService){
        this.locationService = locationService;
    }

    @RequestMapping(value="/upload", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void upload(@RequestBody List<Location> locations){
        this.locationService.saveRunningLocations(locations);
    }

    @RequestMapping(value="/purge", method = RequestMethod.POST)
    public void purge(){
        this.locationService.deleteAll();
    }

    @RequestMapping(value="/running/{movementType}", method = RequestMethod.GET)
    public Page<Location> findByMovementType(@PathVariable String movementType, @RequestParam(name="page") int page, @RequestParam(name="size") int size){
        return this.locationService.findByRunnerMovementType(movementType,new PageRequest(page,size));
    }

    @RequestMapping(value="/running/runningId/{runningId}", method = RequestMethod.GET)
    public Page<Location> findByRunningId(@PathVariable String runningId, @RequestParam(name="page") int page, @RequestParam(name="size") int size){
        return this.locationService.findByRunningId(runningId,new PageRequest(page,size));
    }
}

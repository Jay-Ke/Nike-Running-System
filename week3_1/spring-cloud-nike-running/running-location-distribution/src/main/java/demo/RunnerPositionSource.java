package demo;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;



@EnableBinding(Source.class)
@RestController
@Slf4j
public class RunnerPositionSource {

    @Autowired
    private MessageChannel output;

    @RequestMapping(params = "/api/locations", methods = RequestMethod.POST)
    public void locations(@RequestBody String positionInfo){
        log.info("Receving currentPositionInfo from Simulator: " + positionInfo);
        this.output.send(MessageBuilder.withPayload(positionInfo).build());
    }

}

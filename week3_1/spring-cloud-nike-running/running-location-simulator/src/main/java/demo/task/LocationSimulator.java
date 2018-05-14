package demo.task;

import demo.model.*;
import demo.service.PositionService;
import demo.support.NavUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class LocationSimulator implements Runnable {
    @Getter
    @Setter
    private long id;

    @Setter
    private PositionService positionService;

    private AtomicBoolean cancel = new AtomicBoolean();

    private double speedInMps;

    private boolean shouldMove;
    private boolean exportPositionsToMessaging = true;
    private Integer reportInterval = 500;

    @Getter
    @Setter
    private PositionInfo currentPosition = null;

    @Setter
    private List<Leg> legs;
    private RunnerStatus runnerStatus = RunnerStatus.NONE;
    private String  runningId;

    @Setter
    private Point startPoint;
    private Date executionStartTime;

    private MedicalInfo medicalInfo;

    public LocationSimulator(GpsSimulatorRequest gpsSimulatorRequest){
        this.shouldMove = gpsSimulatorRequest.isMove();
        this.exportPositionsToMessaging = gpsSimulatorRequest.isExportPositionsToMessaging();
        this.setSpeed(gpsSimulatorRequest.getSpeed());
        this.reportInterval = gpsSimulatorRequest.getReportInterval();

        this.runningId = gpsSimulatorRequest.getRunningId();
        this.runnerStatus = gpsSimulatorRequest.getRunnerStatus();
        this.medicalInfo = gpsSimulatorRequest.getMedicalInfo();
    }


    @Override
    public void run() {
         try{
             executionStartTime = new Date();
             if (cancel.get()){
                 destory();
                 return;
             }

             while (!Thread.interrupted()){
                 long startTime = new Date().getTime();

                 if (currentPosition!=null){
                     if (shouldMove){
                         moveRunningLocation();
                         currentPosition.setSpeed(speedInMps);
                     }else{
                         currentPosition.setSpeed(0.0);
                     }
                     currentPosition.setRunnerStatus(this.runnerStatus);

                     final MedicalInfo medicalInfoToUse;

                     switch (this.runnerStatus){
                         case SUPPLY_NOW:
                         case SUPPLY_SOON:
                         case STOP_NOW:
                             medicalInfoToUse = this.medicalInfo;
                             break;
                         default:
                             medicalInfoToUse = null;
                             break;
                     }

                     final CurrentPosition currentPosition = new CurrentPosition(this.currentPosition.getRunningId(),
                             new Point(this.currentPosition.getPosition().getLatitude(), this.currentPosition.getPosition().getLongitude()),
                             this.currentPosition.getRunnerStatus(),
                             this.currentPosition.getSpeed(),
                             this.currentPosition.getLeg().getHeading(),
                             medicalInfoToUse);
                     //send current position to distribution service via rest api
                     //TODO implement positionInfoService
                     positionService.processPositionInfo(id, currentPosition, this.exportPositionsToMessaging);
                 }
                 sleep(startTime);
             }
         }
         catch(InterruptedException ie){
             destory();
             return;
         }
         destory();
    }

    void destory(){
        currentPosition = null;
    }

    private void sleep(long startTime) throws InterruptedException{
        long endTime = new Date().getTime();
        long elapsedTime = endTime - startTime;
        long sleepTime = reportInterval - elapsedTime > 0? reportInterval - elapsedTime : 0;
        Thread.sleep(sleepTime);
    }

    //Set new position based on running speed and current position
    private void moveRunningLocation(){
        double distance = speedInMps * reportInterval / 1000.0;
        double distanceFromStart = currentPosition.getDistanceFromStart() + distance;
        double excess = 0.0;

        for (int i=currentPosition.getLeg().getId();i<legs.size();i++){
            Leg currentLeg = legs.get(i);
            excess = distanceFromStart > currentLeg.getLength() ? distanceFromStart - currentLeg.getLength() : 0.0;

            if (Double.doubleToRawLongBits(excess)==0){
                //new Position falls within current leg
                currentPosition.setDistanceFromStart(distanceFromStart);

                currentPosition.setLeg(currentLeg);
                //Use the new p ocalculatin method in NavUtils
                Point newPosition = NavUtils.getPosition(currentLeg.getStartPosition(), distanceFromStart, currentLeg.getHeading());
                currentPosition.setPosition(newPosition);
                return;
            }
            distanceFromStart = excess;
        }

        setStartPosition();

    }

    public void setStartPosition(){
        currentPosition = new PositionInfo();
        currentPosition.setRunningId(this.runningId);
        Leg leg = legs.get(0);
        currentPosition.setLeg(leg);
        currentPosition.setPosition(leg.getStartPosition());
        currentPosition.setDistanceFromStart(0.0);
    }

    public void setSpeed(double speed){
        this.speedInMps = speed;
    }

    public double getSpeed(){
        return this.speedInMps;
    }

    public synchronized void cancel(){
        this.cancel.set(true);
    }
}

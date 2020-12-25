package vahy.original.environment.state;

import com.kitfox.svg.A;
import vahy.impl.model.ImmutableStateRewardReturnTuple;
import vahy.original.environment.HallwayAction;
import vahy.original.environment.agent.AgentHeading;
import vahy.utils.ArrayUtils;
import vahy.utils.ImmutableTuple;

import java.util.List;

public class RestrictedMovementData {
    private boolean enabled;
    //List<HallwayAction> historyOfAction;
    private int notMoved = 0;
    private int forward = 0;
    private int left = 0;
    private int right = 0;
    private AgentHeading direction;
    private int turned = 0;
    private HallwayAction turning = HallwayAction.NO_ACTION;
    private HallwayAction dTurning = HallwayAction.NO_ACTION;

    public static final boolean FORWARD = false;

    public HallwayAction getdTurning() {
        return dTurning;
    }

    public void setdTurning(HallwayAction dTurning) {
        this.dTurning = dTurning;
    }

    public int getTurned() {
        return turned;
    }

    public void setTurned(int turned) {
        this.turned = turned;
    }

    public void setTurning(HallwayAction turning) {
        this.turning = turning;
    }

    public HallwayAction getTurning() {
        return turning;
    }

    public int getForward() {
        return forward;
    }

    public int getLeft() {
        return left;
    }

    public int getRight() {
        return right;
    }

    public RestrictedMovementData(boolean enabled) {
        this(enabled,0,0,0,0, AgentHeading.NORTH);
    }

    public RestrictedMovementData(boolean enabled, int notMoved, int forward, int left, int right, AgentHeading agentHeading) {
        this.enabled = enabled;
        this.notMoved = notMoved;
        this.forward = forward;
        this.left = left;
        this.right = right;
        this.direction = agentHeading;
    }

    public AgentHeading getDirection() {
        return direction;
    }

    public AgentHeading getOppositeDirection() {
        switch(direction){
            case EAST: return AgentHeading.WEST;
            case WEST: return AgentHeading.EAST;
            case NORTH: return AgentHeading.SOUTH;
            case SOUTH: return AgentHeading.NORTH;
            default: return AgentHeading.NORTH;
        }
    }

    public void addAction(HallwayAction hallwayAction){
        notMoved++;
        switch (hallwayAction) {
            case FORWARD:
                forward++;
                break;
            case TURN_RIGHT:
                right++;
                break;
            case TURN_LEFT:
                left++;
                break;
        }
    }

    public void incTurned(){
        turned++;
    }

    public void moved(AgentHeading agentHeading){
        notMoved = 0;
        forward = 0;
        left = 0;
        right = 0;
        direction = agentHeading;
    }

    public void resetTurn(){
        left = 0;
        right = 0;
    }

    public boolean isEnabled(){
        return enabled;
    }


    public RestrictedMovementData deepCopy(){
        return new RestrictedMovementData(enabled, notMoved, forward, left, right, direction);
    }
}

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
    private int notMoved = 0;
    private int forward = 0;
    private int left = 0;
    private int right = 0;

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
        this(enabled,0,0,0,0);
    }

    public RestrictedMovementData(boolean enabled, int notMoved, int forward, int left, int right) {
        this.enabled = enabled;
        this.notMoved = notMoved;
        this.forward = forward;
        this.left = left;
        this.right = right;
    }

    public void addAction(HallwayAction hallwayAction){
        notMoved++;
        switch (hallwayAction) {
            case FORWARD:
                forward++;
                return;
            case TURN_RIGHT:
                right++;
                return;
            case TURN_LEFT:
                left++;
                return;
        }
    }

    public void moved(){
        notMoved = 0;
        forward = 0;
        left = 0;
        right = 0;
    }

    public boolean isEnabled(){
        return enabled;
    }

    public RestrictedMovementData deepCopy(){
        return new RestrictedMovementData(enabled, notMoved, forward, left, right);
    }
}

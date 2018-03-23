package vahy.environment.state;

import vahy.environment.ActionType;
import vahy.environment.agent.AgentHeading;
import vahy.utils.ArrayUtils;
import vahy.utils.ImmutableTuple;

import java.util.Arrays;
import java.util.Random;

public class ImmutableStateImpl implements IState {

    private final StaticGamePart staticGamePart;
    private final double[][] rewards;
    private final int rewardsLeft;

    private final boolean isAgentKilled;
    private final int agentXCoordination;
    private final int agentYCoordination;
    private final AgentHeading agentHeading;

    public ImmutableStateImpl(
            StaticGamePart staticGamePart,
            double[][] rewards,
            int agentXCoordination,
            int agentYCoordination,
            AgentHeading agentHeading) {
        this(staticGamePart,
                rewards,
                agentXCoordination,
                agentYCoordination,
                agentHeading,
                Arrays
                        .stream(rewards)
                        .map(doubles -> Arrays
                                .stream(doubles)
                                .filter(value -> value > 0.0)
                                .count())
                        .mapToInt(Long::intValue)
                        .sum());
    }

    private ImmutableStateImpl(
            StaticGamePart staticGamePart,
            double[][] rewards,
            int agentXCoordination,
            int agentYCoordination,
            AgentHeading agentHeading,
            int rewardsLeft) {
        this.staticGamePart = staticGamePart;
        this.rewards = rewards;
        this.agentXCoordination = agentXCoordination;
        this.agentYCoordination = agentYCoordination;
        this.agentHeading = agentHeading;
        this.rewardsLeft = rewardsLeft;
        this.isAgentKilled = isAgentKilled(staticGamePart.getTrapProbabilities()[agentXCoordination][agentYCoordination], staticGamePart.getRandom());
    }

    private boolean isAgentKilled(double trapProbability, Random random) {
        return random.nextDouble() <= trapProbability;
    }

    @Override
    public ActionType[] getListOfPossibleActions() {
        if(isFinalState()) {
            return new ActionType[0];
        } else {
            return ActionType.values();
        }
    }

    @Override
    public RewardStateReturn applyAction(ActionType actionType) {
        if(isFinalState()) {
            throw new IllegalStateException("Cannot apply actions on final state");
        }
        if(actionType == ActionType.FORWARD) {
            ImmutableTuple<Integer, Integer> agentCoordinates = makeForwardAction();
            double reward = rewards[agentCoordinates.getFirst()][agentCoordinates.getSecond()] - staticGamePart.getDefaultStepPenalty();
            double[][] newRewards = ArrayUtils.cloneArray(rewards);
            if(rewards[agentCoordinates.getFirst()][agentCoordinates.getSecond()] != 0.0) {
                newRewards[agentCoordinates.getFirst()][agentCoordinates.getSecond()] = 0.0;
            }
            IState state = new ImmutableStateImpl(
                    staticGamePart,
                    newRewards,
                    agentCoordinates.getFirst(),
                    agentCoordinates.getSecond(),
                    agentHeading,
                    rewardsLeft);
            return new RewardStateReturn(reward, state);
        } else {
            IState state = new ImmutableStateImpl(
                    staticGamePart,
                    ArrayUtils.cloneArray(rewards),
                    agentXCoordination,
                    agentYCoordination,
                    agentHeading.turn(actionType),
                    rewardsLeft);
            return new RewardStateReturn(-staticGamePart.getDefaultStepPenalty(), state);
        }
    }

    @Override
    public IState deepCopy() {
        return new ImmutableStateImpl(
                staticGamePart,
                ArrayUtils.cloneArray(rewards),
                agentXCoordination,
                agentYCoordination,
                agentHeading,
                rewardsLeft);
    }

    private int generateNoisyMove(int coordinate, Random random, double noisyProbability) {
        if(noisyProbability > 0.0) {
            boolean addNoise = random.nextDouble() <= noisyProbability;
            return addNoise
                    ? random.nextBoolean()
                        ? coordinate - 1
                        : coordinate + 1
                    : coordinate;
        } else {
            return coordinate;
        }
    }

    private ImmutableTuple<Integer, Integer> makeForwardAction() {
        int x = agentXCoordination;
        int y = agentYCoordination;
        switch (agentHeading) {
            case NORTH:
                x = agentXCoordination - 1;
                y = generateNoisyMove(y, staticGamePart.getRandom(), staticGamePart.getNoisyMoveProbability());
                break;
            case SOUTH:
                x = agentXCoordination + 1;
                y = generateNoisyMove(y, staticGamePart.getRandom(), staticGamePart.getNoisyMoveProbability());
                break;
            case EAST:
                x = generateNoisyMove(x, staticGamePart.getRandom(), staticGamePart.getNoisyMoveProbability());
                y = agentYCoordination - 1;
                break;
            case WEST:
                x = generateNoisyMove(x, staticGamePart.getRandom(), staticGamePart.getNoisyMoveProbability());
                y = agentYCoordination + 1;
                break;
            default:
                throw new IllegalArgumentException("Unknown enum value: [" + agentHeading + "]");
        }

        if(x < 0) {
            throw new IllegalStateException("Agent's coordinate X cannot be negative");
        }
        if(x >= staticGamePart.getWalls().length) {
            throw new IllegalStateException("Agent's coordinate X cannot be bigger or equal to maze size");
        }
        if(y < 0) {
            throw new IllegalStateException("Agent's coordinate Y cannot be negative");
        }
        if(y >= staticGamePart.getWalls()[agentXCoordination].length) {
            throw new IllegalStateException("Agent's coordinate Y cannot be bigger or equal to maze size");
        }

        if(!staticGamePart.getWalls()[x][y]) {
            return new ImmutableTuple<>(x, y);
        } else {
            return new ImmutableTuple<>(agentXCoordination, agentYCoordination);
        }
    }

    @Override
    public boolean isFinalState() {
        return isAgentKilled || rewardsLeft == 0;
    }

}

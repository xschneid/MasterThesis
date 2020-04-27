package vahy.original.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vahy.api.policy.PolicyMode;
import vahy.impl.episode.AbstractInitialStateSupplier;
import vahy.impl.learning.trainer.GameSamplerImpl;
import vahy.impl.model.observation.DoubleVector;
import vahy.original.environment.HallwayAction;
import vahy.original.environment.agent.AgentHeading;
import vahy.original.environment.config.GameConfig;
import vahy.original.environment.state.EnvironmentProbabilities;
import vahy.original.environment.state.HallwayStateImpl;
import vahy.original.environment.state.RestrictedMovementData;
import vahy.original.environment.state.StaticGamePart;
import vahy.original.game.cell.Cell;
import vahy.original.game.cell.CellType;
import vahy.utils.ArrayUtils;
import vahy.utils.ImmutableTuple;

import java.awt.geom.RoundRectangle2D;
import java.util.Arrays;
import java.util.List;
import java.util.SplittableRandom;
import java.util.stream.Collectors;

public class HallwayGameInitialInstanceSupplierAdv extends AbstractInitialStateSupplier<GameConfig, HallwayAction,  DoubleVector, EnvironmentProbabilities, HallwayStateImpl>{

    private static final Logger logger = LoggerFactory.getLogger(GameSamplerImpl.class.getName());
    public static final boolean RESTRICTED_MOVEMENT = false;
    public static final boolean RANDOM_STARTING_POSITION = true;
    public static final double STARTING_POSITION_CHANCE = 0.25;

    public HallwayGameInitialInstanceSupplierAdv(GameConfig gameConfig, SplittableRandom random) {
        super(gameConfig, random);
    }

    @Override
    protected HallwayStateImpl createState_inner(GameConfig problemConfig, PolicyMode policyMode, SplittableRandom random) {
        return createImmutableInitialState(problemConfig.getGameMatrix(), problemConfig, policyMode, random);
    }

    private ImmutableTuple<Integer, Integer> generateInitialAgentCoordinates(List<List<Cell>> gameSetup, PolicyMode policyMode, SplittableRandom random) {
        List<Cell> startingLocations;
        if (policyMode == PolicyMode.TRAINING && RANDOM_STARTING_POSITION && random.nextDouble() > STARTING_POSITION_CHANCE){
            startingLocations = gameSetup.stream()
                    .flatMap(List::stream)
                    .filter(cell -> cell.getCellType() == CellType.EMPTY)
                    .collect(Collectors.toList());
        }
        else {
            startingLocations = gameSetup.stream()
                    .flatMap(List::stream)
                    .filter(cell -> cell.getCellType() == CellType.STARTING_LOCATION)
                    .collect(Collectors.toList());
        }
        Cell startingLocation = startingLocations.get(random.nextInt(startingLocations.size()));
        return new ImmutableTuple<>(startingLocation.getCellPosition().getX(), startingLocation.getCellPosition().getY());
    }

    private HallwayStateImpl createImmutableInitialState(List<List<Cell>> gameSetup, GameConfig gameConfig, PolicyMode policyMode, SplittableRandom random) {
        boolean[][] walls = new boolean[gameSetup.size()][gameSetup.get(0).size()];
        double[][] rewards = new double[gameSetup.size()][gameSetup.get(0).size()];
        double[][] trapProbabilities = new double[gameSetup.size()][gameSetup.get(0).size()];
        gameSetup.stream()
                .flatMap(List::stream)
                .forEach(cell -> {
                    walls[cell.getCellPosition().getX()][cell.getCellPosition().getY()] = cell.getCellType() == CellType.WALL;
                    rewards[cell.getCellPosition().getX()][cell.getCellPosition().getY()] = cell.getCellType() == CellType.GOAL ? gameConfig.getGoalReward() : 0.0;
                    trapProbabilities[cell.getCellPosition().getX()][cell.getCellPosition().getY()] = cell.getCellType() == CellType.TRAP ? gameConfig.getTrapProbability() : 0.0;
                });
        int totalRewardsCount = (int) Arrays.stream(rewards).mapToLong(x -> Arrays.stream(x).filter(y -> y > 0.0).count()).sum();
        StaticGamePart staticGamePart = new StaticGamePart(gameConfig.getStateRepresentation(), trapProbabilities, ArrayUtils.cloneArray(rewards), walls, gameConfig.getStepPenalty(), gameConfig.getNoisyMoveProbability(), totalRewardsCount);
        ImmutableTuple<Integer, Integer> agentStartingPosition = generateInitialAgentCoordinates(gameSetup, policyMode, random);
        if(policyMode == PolicyMode.TRAINING && RESTRICTED_MOVEMENT){
            return new HallwayStateImpl(staticGamePart, rewards, agentStartingPosition.getFirst(), agentStartingPosition.getSecond(), AgentHeading.NORTH, new RestrictedMovementData(true));
        }else{
            return new HallwayStateImpl(staticGamePart, rewards, agentStartingPosition.getFirst(), agentStartingPosition.getSecond(), AgentHeading.NORTH, new RestrictedMovementData(false));
        }
    }




}

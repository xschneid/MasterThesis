package vahy.impl.policy.random;

import vahy.api.model.Action;
import vahy.api.model.State;
import vahy.api.model.observation.Observation;
import vahy.api.model.reward.Reward;

import java.util.Arrays;
import java.util.List;
import java.util.SplittableRandom;

public class UniformRandomWalkPolicy<TAction extends Action,
    TReward extends Reward,
    TPlayerObservation extends Observation,
    TOpponentObservation extends Observation,
    TState extends State<TAction, TReward, TPlayerObservation, TOpponentObservation, TState>>
    extends AbstractRandomWalkPolicy<TAction, TReward, TPlayerObservation, TOpponentObservation, TState> {

    public UniformRandomWalkPolicy(SplittableRandom random) {
        super(random);
    }

    @Override
    public double[] getActionProbabilityDistribution(TState gameState) {
        double[] probabilities = new double[gameState.getAllPossibleActions().length];
        Arrays.fill(probabilities, 1.0 / (double) probabilities.length);
        return probabilities;
    }

    @Override
    public TAction getDiscreteAction(TState gameState) {
        TAction[] actions = gameState.getAllPossibleActions();
        return actions[getRandom().nextInt(actions.length)];
    }

    @Override
    public void updateStateOnPlayedActions(List<TAction> opponentActionList) {
        // this is it
    }
}

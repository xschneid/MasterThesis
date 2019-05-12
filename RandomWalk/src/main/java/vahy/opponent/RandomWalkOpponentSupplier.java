package vahy.opponent;

import vahy.environment.RandomWalkAction;
import vahy.environment.RandomWalkProbabilities;
import vahy.environment.RandomWalkState;
import vahy.impl.model.observation.DoubleVector;
import vahy.impl.model.reward.DoubleReward;
import vahy.paperGenerics.PaperMetadata;
import vahy.paperGenerics.policy.PaperPolicy;
import vahy.paperGenerics.policy.PaperPolicySupplier;

import java.util.SplittableRandom;

public class RandomWalkOpponentSupplier extends PaperPolicySupplier<RandomWalkAction, DoubleReward, DoubleVector, RandomWalkProbabilities, PaperMetadata<RandomWalkAction, DoubleReward>, RandomWalkState> {

    public final SplittableRandom random;

    public RandomWalkOpponentSupplier(SplittableRandom random) {
        super(null, null, 0.0, random, null, null, null, null, null);
        this.random = random;
    }

    @Override
    public PaperPolicy<RandomWalkAction, DoubleReward, DoubleVector, RandomWalkProbabilities, RandomWalkState> initializePolicy(RandomWalkState initialState) {
        return new RandomWalkPolicy(random);
    }
}
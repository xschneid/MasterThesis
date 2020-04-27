package vahy.impl.episode;

import vahy.api.episode.InitialStateSupplier;
import vahy.api.experiment.ProblemConfig;
import vahy.api.model.Action;
import vahy.api.model.State;
import vahy.api.model.observation.Observation;
import vahy.api.policy.PolicyMode;

import java.util.SplittableRandom;

public abstract class AbstractInitialStateSupplier<
    TConfig extends ProblemConfig,
    TAction extends Action<TAction>,
    TPlayerObservation extends Observation,
    TOpponentObservation extends Observation,
    TState extends State<TAction, TPlayerObservation, TOpponentObservation, TState>>
    implements InitialStateSupplier<TConfig, TAction, TPlayerObservation, TOpponentObservation, TState> {

    private final TConfig problemConfig;
    private final SplittableRandom random;

    protected AbstractInitialStateSupplier(TConfig problemConfig, SplittableRandom random) {
        this.problemConfig = problemConfig;
        this.random = random;
    }

    @Override
    public TState createInitialState() {
        return createState_inner(problemConfig, PolicyMode.INFERENCE, random.split());
    }

    @Override
    public TState createInitialState(PolicyMode policyMode) {
        return createState_inner(problemConfig, policyMode, random.split());
    }

    protected abstract TState createState_inner(TConfig problemConfig, PolicyMode policyMode, SplittableRandom random);
}

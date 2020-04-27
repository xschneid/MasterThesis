package vahy.original.solutionExampleAdv;

import vahy.api.experiment.StochasticStrategy;
import vahy.api.experiment.SystemConfig;
import vahy.api.experiment.SystemConfigBuilder;
import vahy.api.learning.ApproximatorType;
import vahy.api.learning.dataAggregator.DataAggregationAlgorithm;
import vahy.config.AlgorithmConfigBuilder;
import vahy.config.EvaluatorType;
import vahy.config.PaperAlgorithmConfig;
import vahy.config.SelectorType;
import vahy.impl.search.tree.treeUpdateCondition.FixedUpdateCountTreeConditionFactory;
import vahy.original.environment.config.ConfigBuilder;
import vahy.original.environment.config.GameConfig;
import vahy.original.environment.state.StateRepresentation;
import vahy.original.game.HallwayInstance;
import vahy.original.solutionExamples.DefaultLocalBenchmark;
import vahy.paperGenerics.policy.flowOptimizer.FlowOptimizerType;
import vahy.paperGenerics.policy.riskSubtree.SubTreeRiskCalculatorType;
import vahy.paperGenerics.policy.riskSubtree.strategiesProvider.ExplorationExistingFlowStrategy;
import vahy.paperGenerics.policy.riskSubtree.strategiesProvider.ExplorationNonExistingFlowStrategy;
import vahy.paperGenerics.policy.riskSubtree.strategiesProvider.InferenceExistingFlowStrategy;
import vahy.paperGenerics.policy.riskSubtree.strategiesProvider.InferenceNonExistingFlowStrategy;

import java.util.function.Supplier;

public class Benchmark00SolutionAdv extends DefaultLocalBenchmarkAdv {

    public static void main(String[] args) {
        var benchmark = new Benchmark00SolutionAdv();
        benchmark.runBenchmark();
    }

    @Override
    protected SystemConfig createSystemConfig() {
        return new SystemConfigBuilder()
                .setRandomSeed(0)
                .setStochasticStrategy(StochasticStrategy.REPRODUCIBLE)
                .setDrawWindow(true)
                .setParallelThreadsCount(4)
                .setSingleThreadedEvaluation(false)
                .setEvalEpisodeCount(10000)
                .buildSystemConfig();
    }

    @Override
    protected GameConfig createGameConfig() {
        return new ConfigBuilder()
                .reward(100)
                .noisyMoveProbability(0.0)
                .stepPenalty(5)
                .trapProbability(0.2)
                .stateRepresentation(StateRepresentation.COMPACT)
                .gameStringRepresentation(HallwayInstance.BENCHMARK_00)
                .buildConfig();
    }

    @Override
    protected PaperAlgorithmConfig createAlgorithmConfig() {
        return new AlgorithmConfigBuilder()
                //MCTS
                .cpuctParameter(3)

                //.mcRolloutCount(1)
                //NN
                .trainingBatchSize(1)
                .trainingEpochCount(10)
                // REINFORCEMENT
                .discountFactor(1)
                .batchEpisodeCount(100)
                .treeUpdateConditionFactory(new FixedUpdateCountTreeConditionFactory(100))
                .stageCount(300)
                .evaluatorType(EvaluatorType.RALF)
//            .setBatchedEvaluationSize(1)
                .maximalStepCountBound(500)
                .trainerAlgorithm(DataAggregationAlgorithm.EVERY_VISIT_MC)
                .replayBufferSize(100_000)
                .trainingBatchSize(1)
                .learningRate(0.01)

                .approximatorType(ApproximatorType.HASHMAP_LR)
                .selectorType(SelectorType.UCB)

                .globalRiskAllowed(0.15)
                .riskSupplier(new Supplier<Double>() {
                    @Override
                    public Double get() {
                        return 0.15;
                    }

                    @Override
                    public String toString() {
                        return "() -> 1.00";
                    }
                })
                .explorationConstantSupplier(new Supplier<Double>() {
                    @Override
                    public Double get() {
                        return 0.5;
                    }

                    @Override
                    public String toString() {
                        return "() -> 1.00";
                    }
                })
                .temperatureSupplier(new Supplier<Double>() {
                    private int callCount = 0;
                    @Override
                    public Double get() {
                        callCount++;
                        return Math.exp(-callCount / 10000.0);
//                    return 2.00;
                    }

                    @Override
                    public String toString() {
                        return "() -> 1.05";
                    }
                })

                .setInferenceExistingFlowStrategy(InferenceExistingFlowStrategy.SAMPLE_OPTIMAL_FLOW)
                .setInferenceNonExistingFlowStrategy(InferenceNonExistingFlowStrategy.MAX_UCB_VALUE)
                .setExplorationExistingFlowStrategy(ExplorationExistingFlowStrategy.SAMPLE_OPTIMAL_FLOW_BOLTZMANN_NOISE)
                .setExplorationNonExistingFlowStrategy(ExplorationNonExistingFlowStrategy.SAMPLE_UCB_VALUE_WITH_TEMPERATURE)
                .setFlowOptimizerType(FlowOptimizerType.HARD_HARD)
                .setSubTreeRiskCalculatorTypeForKnownFlow(SubTreeRiskCalculatorType.FLOW_SUM)
                .setSubTreeRiskCalculatorTypeForUnknownFlow(SubTreeRiskCalculatorType.MINIMAL_RISK_REACHABILITY)
                .buildAlgorithmConfig();
    }
}

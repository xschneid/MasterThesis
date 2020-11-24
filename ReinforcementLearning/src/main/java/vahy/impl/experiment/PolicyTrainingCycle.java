package vahy.impl.experiment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vahy.api.experiment.AlgorithmConfig;
import vahy.api.experiment.SystemConfig;
import vahy.api.model.Action;
import vahy.api.model.State;
import vahy.api.model.observation.Observation;
import vahy.api.policy.PolicyRecord;
import vahy.impl.learning.trainer.Trainer;

import java.time.Duration;

public class PolicyTrainingCycle<
    TAction extends Enum<TAction> & Action<TAction>,
    TPlayerObservation extends Observation,
    TOpponentObservation extends Observation,
    TState extends State<TAction, TPlayerObservation, TOpponentObservation, TState>,
    TPolicyRecord extends PolicyRecord> {

    private static Logger logger = LoggerFactory.getLogger(PolicyTrainingCycle.class.getName());

    private final SystemConfig systemConfig;
    private final AlgorithmConfig algorithmConfig;
    private final EpisodeWriter<TAction, TPlayerObservation, TOpponentObservation, TState, TPolicyRecord> episodeWriter;
    private final Trainer<TAction, TPlayerObservation, TOpponentObservation, TState, TPolicyRecord> trainer;


    public PolicyTrainingCycle(SystemConfig systemConfig,
                               AlgorithmConfig algorithmConfig,
                               EpisodeWriter<TAction, TPlayerObservation, TOpponentObservation, TState, TPolicyRecord> episodeWriter,
                               Trainer<TAction, TPlayerObservation, TOpponentObservation, TState, TPolicyRecord> trainer) {
        this.systemConfig = systemConfig;
        this.algorithmConfig = algorithmConfig;
        this.episodeWriter = episodeWriter;
        this.trainer = trainer;
    }

    public Duration executeTrainingPolicy() {
        var startTrainingMillis = System.currentTimeMillis();
        innerTrainPolicy();
        var endTrainingMillis = System.currentTimeMillis();
        return Duration.ofMillis(endTrainingMillis - startTrainingMillis);
    }

    private void innerTrainPolicy() {
        double stepCountOverall = 0;
        double rewardCountOverall = 0;
        for (int i = 0; i < algorithmConfig.getStageCount(); i++) {
            logger.info("Training policy for [{}]th iteration", i);
            var episodes = trainer.trainPolicy(algorithmConfig.getBatchEpisodeCount(), algorithmConfig.getMaximalStepCountBound());
////////////////

            double step = 0;
            double reward = 0;
            double maxReward = -Double.MAX_VALUE;
            double minReward = Double.MAX_VALUE;
            int minEpisode = 0;
            int maxEpisode = 0;
            int episodeNumber = 1;
            for (var episode: episodes
            ) {
                step += episode.getPlayerStepCount();
                reward += episode.getTotalPayoff();
                if(episode.getTotalPayoff() > maxReward){
                    maxReward = episode.getTotalPayoff();
                    maxEpisode = episodeNumber;
                }
                if(episode.getTotalPayoff() < minReward){
                    minReward = episode.getTotalPayoff();
                    minEpisode = episodeNumber;
                }
                episodeNumber++;
            }
            logger.info("Training MaxReward [{}] in episode [{}]", maxReward,maxEpisode);
            logger.info("Training MinReward [{}] in episode [{}]", minReward,minEpisode);
            stepCountOverall += step / episodes.size();
            rewardCountOverall += reward / episodes.size();


///////////////////
            if(systemConfig.dumpTrainingData()) {
                episodeWriter.writeTrainingEpisode(i, episodes);
            }
        }
        logger.info("Training AvgStepCountOverall [{}]", stepCountOverall/algorithmConfig.getStageCount());
        logger.info("Training AvgRewardCountOverall [{}]", rewardCountOverall/algorithmConfig.getStageCount());
    }

}

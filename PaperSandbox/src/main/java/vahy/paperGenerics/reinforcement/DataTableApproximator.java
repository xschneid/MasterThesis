package vahy.paperGenerics.reinforcement;

import vahy.impl.model.observation.DoubleVector;
import vahy.utils.ImmutableTuple;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class DataTableApproximator<TObservation extends DoubleVector> extends TrainableApproximator<TObservation> {

    private final double[] defaultPrediction;

    public DataTableApproximator(int actionCount) {
        super(null);
        this.defaultPrediction = new double[2 + actionCount];
        this.defaultPrediction[0] = 0;
        this.defaultPrediction[1] = 0.0;
        for (int i = 0; i < actionCount; i++) {
            defaultPrediction[i + 2] = 1.0 / actionCount;
        }
    }

    private HashMap<TObservation, double[]> predictionMap = new HashMap<>();

    @Override
    public void train(List<ImmutableTuple<TObservation, double[]>> episodeData) {
        predictionMap = episodeData
            .stream()
            .collect(Collectors
                .toMap(
                    ImmutableTuple::getFirst,
                    ImmutableTuple::getSecond,
                    (oldValue, newValue) -> oldValue,
                    HashMap::new)
            );
    }

    @Override
    public double[] apply(TObservation doubleVectorialObservation) {
        return predictionMap.getOrDefault(doubleVectorialObservation, defaultPrediction);
    }
}

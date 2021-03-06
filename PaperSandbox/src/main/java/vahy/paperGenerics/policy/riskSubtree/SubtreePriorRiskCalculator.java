package vahy.paperGenerics.policy.riskSubtree;

import vahy.api.model.Action;
import vahy.api.model.observation.Observation;
import vahy.api.search.node.SearchNode;
import vahy.paperGenerics.metadata.PaperMetadata;
import vahy.paperGenerics.PaperState;
import vahy.utils.ImmutableTuple;

import java.util.LinkedList;

public class SubtreePriorRiskCalculator<
    TAction extends Action<TAction>,
    TPlayerObservation extends Observation,
    TOpponentObservation extends Observation,
    TSearchNodeMetadata extends PaperMetadata<TAction>,
    TState extends PaperState<TAction, TPlayerObservation, TOpponentObservation, TState>>
    implements SubtreeRiskCalculator<TAction, TPlayerObservation, TOpponentObservation, TSearchNodeMetadata, TState> {

    @Override
    public double calculateRisk(SearchNode<TAction, TPlayerObservation, TOpponentObservation, TSearchNodeMetadata, TState> subtreeRoot) {
        double totalRisk = 0;
        var queue = new LinkedList<ImmutableTuple<SearchNode<TAction, TPlayerObservation, TOpponentObservation, TSearchNodeMetadata, TState>, Double>>();
        queue.add(new ImmutableTuple<>(subtreeRoot, 1.0));
        while(!queue.isEmpty()) {
            var node = queue.poll();
            if(node.getFirst().isLeaf()) {
                if(node.getFirst().isFinalNode()) {
                    totalRisk += node.getFirst().getWrappedState().isRiskHit() ? node.getSecond() : 0.0;
                } else {
                    totalRisk += node.getSecond() * node.getFirst().getSearchNodeMetadata().getPredictedRisk();
                }
            } else {
                for (var entry : node.getFirst().getChildNodeMap().entrySet()) {
                    queue.addLast(new ImmutableTuple<>(entry.getValue(), entry.getValue().getSearchNodeMetadata().getPriorProbability() * node.getSecond()));
                }
            }
        }
        return totalRisk;
    }
}

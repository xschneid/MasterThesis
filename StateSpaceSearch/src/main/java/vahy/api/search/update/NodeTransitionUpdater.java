package vahy.api.search.update;

import vahy.api.model.Action;
import vahy.api.model.State;
import vahy.api.model.observation.Observation;
import vahy.api.search.node.SearchNode;
import vahy.api.search.node.SearchNodeMetadata;

public interface NodeTransitionUpdater<
    TAction extends Action<TAction>,
    TPlayerObservation extends Observation,
    TOpponentObservation extends Observation,
    TSearchNodeMetadata extends SearchNodeMetadata,
    TState extends State<TAction, TPlayerObservation, TOpponentObservation, TState>> {

    void applyUpdate(SearchNode<TAction, TPlayerObservation, TOpponentObservation, TSearchNodeMetadata, TState> evaluatedNode,
                     SearchNode<TAction, TPlayerObservation, TOpponentObservation, TSearchNodeMetadata, TState> parent,
                     SearchNode<TAction, TPlayerObservation, TOpponentObservation, TSearchNodeMetadata, TState> child);
}

package vahy.testDomain.search;

import vahy.api.model.StateRewardReturn;
import vahy.api.search.node.SearchNode;
import vahy.impl.model.observation.DoubleVector;
import vahy.impl.search.MCTS.MonteCarloTreeSearchMetadata;
import vahy.impl.search.node.SearchNodeImpl;
import vahy.impl.testdomain.model.TestAction;
import vahy.impl.testdomain.model.TestState;

import java.util.LinkedHashMap;
import java.util.Map;

public class TestHelper {

    public static SearchNode<TestAction, DoubleVector, TestState, MonteCarloTreeSearchMetadata, TestState> createOneLevelTree(boolean playerTurn) {
        TestState initialState = playerTurn ? TestState.getDefaultInitialStatePlayerTurn() : TestState.getDefaultInitialStateOpponentTurn();
        MonteCarloTreeSearchMetadata rootMetadata = new MonteCarloTreeSearchMetadata(0.0, 0.0, 0.0);
        rootMetadata.increaseVisitCounter();
        SearchNode<TestAction, DoubleVector, TestState, MonteCarloTreeSearchMetadata, TestState> root = new SearchNodeImpl<>(
            initialState,
            rootMetadata,
            new LinkedHashMap<>());
        Map<TestAction, SearchNode<TestAction, DoubleVector, TestState, MonteCarloTreeSearchMetadata, TestState>> childNodeMap = root.getChildNodeMap();
        for (TestAction playerAction : playerTurn ? TestAction.playerActions : TestAction.opponentActions) {
            StateRewardReturn<TestAction, DoubleVector, TestState, TestState> rewardReturn = initialState.applyAction(playerAction);
            childNodeMap.put(playerAction, new SearchNodeImpl<>(
                rewardReturn.getState(),
                new MonteCarloTreeSearchMetadata(rewardReturn.getReward(), rewardReturn.getReward(), rewardReturn.getReward()),
                new LinkedHashMap<>(),
                root,
                playerAction)
            );
        }
        return root;
    }
}

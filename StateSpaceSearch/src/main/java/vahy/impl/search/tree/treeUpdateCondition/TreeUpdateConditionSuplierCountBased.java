package vahy.impl.search.tree.treeUpdateCondition;

import vahy.api.search.tree.treeUpdateCondition.TreeUpdateCondition;

public class TreeUpdateConditionSuplierCountBased implements TreeUpdateCondition {

    private final int callsPerStep;
    private int callCount = 0;

    public TreeUpdateConditionSuplierCountBased(int callsPerStep) {
        this.callsPerStep = callsPerStep;
    }

    @Override
    public void treeUpdateRequired() {
        callCount = 0;
    }

    @Override
    public boolean isConditionSatisfied() {
        callCount++;
        return callCount <= callsPerStep;
    }

    @Override
    public void treeUpdateFinished() {
        // empty
    }

    @Override
    public void reset() {
        treeUpdateRequired();
    }
}

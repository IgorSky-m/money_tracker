package com.bot.calculation.executor.api;

import java.util.List;

public abstract class AExecutorFactory<EXECUTOR extends IIdentifiable<ID>, ID> implements IExecutorFactory<EXECUTOR, ID> {

    private final List<EXECUTOR> executors;

    public AExecutorFactory(List<EXECUTOR> executors) {
        this.executors = executors;
    }

    @Override
    public EXECUTOR findExecutor(ID id) {
        return executors
                .stream()
                .filter(e -> e.getIdentifier().equals(id))
                .findFirst()
                .orElse(null);
    }


    @Override
    public List<EXECUTOR> getAll() {
        return executors;
    }
}

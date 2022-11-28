package com.bot.calculation.executor.api;

import java.util.List;

public interface IExecutorFactory<EXECUTOR extends IIdentifiable<ID>, ID> {
    EXECUTOR findExecutor(ID id);
    List<EXECUTOR> getAll();
}

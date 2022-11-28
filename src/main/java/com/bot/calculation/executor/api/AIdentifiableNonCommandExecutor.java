package com.bot.calculation.executor.api;

import com.bot.calculation.sender.api.IMessageSender;
import org.slf4j.Logger;

public abstract class AIdentifiableNonCommandExecutor implements IIdentifiableNonCommandExecutor {

    protected final ENonCommandExecutorType EXECUTOR_ID;
    protected final IMessageSender messageSender;

    public AIdentifiableNonCommandExecutor(ENonCommandExecutorType type, IMessageSender messageSender) {
        this.EXECUTOR_ID = type;
        this.messageSender = messageSender;
    }

    @Override
    public ENonCommandExecutorType getIdentifier(){
        return EXECUTOR_ID;
    }

    protected abstract Logger getLogger();
}

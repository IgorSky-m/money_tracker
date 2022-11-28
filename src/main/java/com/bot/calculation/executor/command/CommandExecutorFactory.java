package com.bot.calculation.executor.command;

import com.bot.calculation.executor.api.AExecutorFactory;
import com.bot.calculation.executor.command.api.ECommandExecutorType;
import com.bot.calculation.executor.command.api.ICommandExecutorFactory;
import com.bot.calculation.executor.command.api.IIdentifiableCommandExecutor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommandExecutorFactory extends AExecutorFactory<IIdentifiableCommandExecutor, ECommandExecutorType> implements ICommandExecutorFactory {

    public CommandExecutorFactory(List<IIdentifiableCommandExecutor> executors) {
        super(executors);
    }
}

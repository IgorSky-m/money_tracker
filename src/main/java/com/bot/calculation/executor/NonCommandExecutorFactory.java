package com.bot.calculation.executor;

import com.bot.calculation.executor.api.AExecutorFactory;
import com.bot.calculation.executor.api.IIdentifiableNonCommandExecutor;
import com.bot.calculation.executor.api.INonCommandExecutorFactory;
import com.bot.calculation.executor.api.ENonCommandExecutorType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NonCommandExecutorFactory extends AExecutorFactory<IIdentifiableNonCommandExecutor, ENonCommandExecutorType> implements INonCommandExecutorFactory {
    public NonCommandExecutorFactory(List<IIdentifiableNonCommandExecutor> executors) {
        super(executors);
    }
}

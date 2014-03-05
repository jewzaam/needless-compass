/*
 * Copyright (C) 2014 Naveen Malik
 *
 * Needless Compass is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Needless Compass is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Needless Compass.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.namal.needless.compass.hystrix;

import com.netflix.config.DynamicBooleanProperty;
import com.netflix.config.DynamicIntProperty;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author nmalik
 */
public final class HystrixConfiguration {
    private static final Map<Class, HystrixConfiguration> configs = new HashMap<>();

    public static HystrixCommand.Setter Setter(Class x, String groupName) {
        HystrixConfiguration config = configs.get(x);
        if (config == null) {
            config = new HystrixConfiguration(x);
            configs.put(x, config);
        }
        return config.setter(x, groupName);
    }

    private final DynamicStringProperty executionIsolationStrategy;
    private final DynamicIntProperty executionIsolationSemaphoreMaxConcurrentRequests;
    private final DynamicBooleanProperty executionIsolationThreadInterruptOnTimeout;
    private final DynamicIntProperty executionIsolationThreadTimeoutInMilliseconds;
    private final DynamicIntProperty fallbackIsolationSemaphoreMaxConcurrentRequests;

    private <T extends HystrixCommand> HystrixConfiguration(Class<T> x) {
        executionIsolationStrategy
                = DynamicPropertyFactory.getInstance().getStringProperty(
                        x.getName() + ".executionIsolationStrategy",
                        HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE.name()
                );
        executionIsolationSemaphoreMaxConcurrentRequests
                = DynamicPropertyFactory.getInstance().getIntProperty(
                        x.getName() + ".executionIsolationSemaphoreMaxConcurrentRequests",
                        10
                );
        executionIsolationThreadInterruptOnTimeout
                = DynamicPropertyFactory.getInstance().getBooleanProperty(
                        x.getName() + ".executionIsolationThreadInterruptOnTimeout",
                        true
                );
        executionIsolationThreadTimeoutInMilliseconds
                = DynamicPropertyFactory.getInstance().getIntProperty(
                        x.getName() + ".executionIsolationThreadTimeoutInMilliseconds",
                        2000
                );
        fallbackIsolationSemaphoreMaxConcurrentRequests
                = DynamicPropertyFactory.getInstance().getIntProperty(
                        x.getName() + ".fallbackIsolationSemaphoreMaxConcurrentRequests",
                        10
                );
    }

    private HystrixCommand.Setter setter(Class clazz, String groupName) {
        String name = clazz.getSimpleName() + ":" + groupName;
        return HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(name))
                .andCommandKey(HystrixCommandKey.Factory.asKey(name))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(name))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.valueOf(executionIsolationStrategy.get()))
                        .withExecutionIsolationSemaphoreMaxConcurrentRequests(executionIsolationSemaphoreMaxConcurrentRequests.get())
                        .withExecutionIsolationThreadInterruptOnTimeout(executionIsolationThreadInterruptOnTimeout.get())
                        .withExecutionIsolationThreadTimeoutInMilliseconds(executionIsolationThreadTimeoutInMilliseconds.get())
                        .withFallbackIsolationSemaphoreMaxConcurrentRequests(fallbackIsolationSemaphoreMaxConcurrentRequests.get())
                );
    }
}

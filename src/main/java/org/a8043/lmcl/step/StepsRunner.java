package org.a8043.lmcl.step;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Getter
@RequiredArgsConstructor
public class StepsRunner {
    private final List<List<Step>> groups;
    private final int maxParallelism;
    private ExecutorService executorService;

    public void run(StepStatus stepStatus, StepContext stepContext, Listener listener) {
        executorService = Executors.newFixedThreadPool(maxParallelism);

        CompletableFuture<Void> chain = CompletableFuture.completedFuture(null);

        for (List<Step> group : groups) {
            if (group == null || group.isEmpty()) {
                continue;
            }
            chain = chain.thenCompose(ignored -> {
                CompletableFuture<?>[] fs = group.stream()
                    .map(step -> CompletableFuture.runAsync(() -> {
                        try {
                            step.execute(stepStatus, stepContext);
                        } catch (Exception e) {
                            log.error("运行步骤失败: {}", e.getMessage(), e);
                            listener.onStepFailed(step, e);
                            throw new CompletionException(e);
                        }
                        listener.onStepCompleted(step);
                    }, executorService))
                    .toArray(CompletableFuture[]::new);
                return CompletableFuture.allOf(fs);
            });
        }

        chain.whenComplete((v, e) -> {
            executorService.shutdown();
            listener.onCompleted(e == null);
        });
    }

    public interface Listener {
        void onStepCompleted(Step step);

        void onStepFailed(Step step, Throwable throwable);

        void onCompleted(boolean success);
    }
}

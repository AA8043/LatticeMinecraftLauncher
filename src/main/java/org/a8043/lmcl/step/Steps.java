package org.a8043.lmcl.step;

import lombok.Getter;

import java.util.*;

public class Steps {
    @Getter
    private static final Map<Class<? extends Step>, List<Class<? extends Step>>> dependencyMap = new HashMap<>();

    public static List<List<Step>> buildStepGroups(List<Step> steps) {
        if (steps == null || steps.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Class<? extends Step>, Step> stepByClass = new LinkedHashMap<>();
        for (Step step : steps) {
            Step prev = stepByClass.putIfAbsent(step.getClass(), step);
            if (prev != null) {
                throw new IllegalStateException("步骤列表中存在多个相同类型的步骤: " + step.getClass().getName());
            }
        }

        for (Step step : steps) {
            Class<? extends Step> cls = step.getClass();
            List<Class<? extends Step>> deps = dependencyMap.get(cls);
            if (deps == null || deps.isEmpty()) {
                continue;
            }
            for (Class<? extends Step> dep : deps) {
                if (!stepByClass.containsKey(dep)) {
                    throw new IllegalStateException("步骤 " + cls.getName() + " 依赖的 " + dep.getName() + " 不在待执行步骤列表中");
                }
            }
        }

        Map<Class<? extends Step>, Integer> inDegree = new LinkedHashMap<>();
        Map<Class<? extends Step>, List<Class<? extends Step>>> reverse = new HashMap<>();
        for (Class<? extends Step> cls : stepByClass.keySet()) {
            inDegree.put(cls, 0);
            reverse.put(cls, new ArrayList<>());
        }
        for (Class<? extends Step> cls : stepByClass.keySet()) {
            List<Class<? extends Step>> deps = dependencyMap.get(cls);
            if (deps == null) {
                continue;
            }
            for (Class<? extends Step> dep : deps) {
                reverse.get(dep).add(cls);
                inDegree.merge(cls, 1, Integer::sum);
            }
        }

        List<Class<? extends Step>> current = new ArrayList<>();
        for (Map.Entry<Class<? extends Step>, Integer> e : inDegree.entrySet()) {
            if (e.getValue() == 0) {
                current.add(e.getKey());
            }
        }

        List<List<Step>> groups = new ArrayList<>();
        int processed = 0;
        while (!current.isEmpty()) {
            List<Step> group = new ArrayList<>(current.size());
            for (Class<? extends Step> cls : current) {
                group.add(stepByClass.get(cls));
            }
            groups.add(group);
            processed += current.size();

            List<Class<? extends Step>> next = new ArrayList<>();
            for (Class<? extends Step> cls : current) {
                for (Class<? extends Step> dependent : reverse.get(cls)) {
                    if (inDegree.merge(dependent, -1, Integer::sum) == 0) {
                        next.add(dependent);
                    }
                }
            }
            current = next;
        }

        if (processed != steps.size()) {
            List<String> unresolved = new ArrayList<>();
            for (Map.Entry<Class<? extends Step>, Integer> e : inDegree.entrySet()) {
                if (e.getValue() > 0) {
                    unresolved.add(e.getKey().getName());
                }
            }
            throw new IllegalStateException("循环依赖: " + unresolved);
        }

        return groups;
    }
}

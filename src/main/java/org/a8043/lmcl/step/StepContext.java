package org.a8043.lmcl.step;

import lombok.Value;

import java.util.Map;

@Value
public class StepContext {
    Map<String, Object> context;

    public void put(String key, Object value) {
        context.put(key, value);
    }

    public <T> T get(String key) {
        return (T) context.get(key);
    }
}

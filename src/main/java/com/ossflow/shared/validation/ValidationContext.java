package com.ossflow.shared.validation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ValidationContext {
    private final Map<String, Object> data = new ConcurrentHashMap<>();
    public void put(String k, Object v) { data.put(k, v); }
    @SuppressWarnings("unchecked")
    public <T> T get(String k) { return (T) data.get(k); }
}

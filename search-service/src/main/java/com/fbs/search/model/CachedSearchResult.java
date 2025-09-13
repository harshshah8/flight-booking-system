package com.fbs.search.model;

import java.time.LocalDateTime;
import java.util.List;

public class CachedSearchResult {
    private List<CachedFlightPath> paths;
    private LocalDateTime computedAt;

    public CachedSearchResult() {}

    public CachedSearchResult(List<CachedFlightPath> paths) {
        this.paths = paths;
        this.computedAt = LocalDateTime.now();
    }

    public List<CachedFlightPath> getPaths() {
        return paths;
    }

    public void setPaths(List<CachedFlightPath> paths) {
        this.paths = paths;
    }

    public LocalDateTime getComputedAt() {
        return computedAt;
    }

    public void setComputedAt(LocalDateTime computedAt) {
        this.computedAt = computedAt;
    }
}
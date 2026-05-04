package com.jpromi.darts.backend.registry;

import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class GameLockRegistry {

    private final ConcurrentHashMap<UUID, ReentrantLock> locks = new ConcurrentHashMap<>();

    public ReentrantLock get(UUID id) {
        return locks.computeIfAbsent(id, k -> new ReentrantLock());
    }

    public void cleanup(UUID id, ReentrantLock lock) {
        if (!lock.hasQueuedThreads()) {
            locks.remove(id, lock);
        }
    }

}

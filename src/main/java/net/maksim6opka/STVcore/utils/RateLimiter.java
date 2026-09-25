package net.maksim6opka.STVcore.utils;

import java.time.Duration;
import java.util.Deque;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Usage
 *
 * <pre>{@code
 * if (!RateLimiter.tryAcquire(player.getUniqueId(), 5, Duration.ofSeconds(10))) {
 *                 Messages.sendDM(mm.deserialize("<red>Ліміт перевищено. Зачекайте пару секунд..."), ctx.getSource().getSender(), "fun", "warn");
 *                 return 1;
 *             }
 * }</pre>
 */


public final class RateLimiter {

    private static final Map<UUID, Deque<Long>> uses = new ConcurrentHashMap<>();

    public static boolean tryAcquire(UUID uuid, int limit, Duration window) {
        long now = System.nanoTime();
        long windowNanos = window.toNanos();

        Deque<Long> queue = uses.computeIfAbsent(
                uuid,
                ignored -> new ConcurrentLinkedDeque<>()
        );

        while (true) {
            Long first = queue.peekFirst();

            if (first == null || now - first < windowNanos) {
                break;
            }

            queue.pollFirst();
        }

        if (queue.size() >= limit) {
            return false;
        }

        queue.addLast(now);
        return true;
    }
}

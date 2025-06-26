package net.omc.util;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class PlayerUtil {

    // returns nearby players who have nearchat enabled
    public static Set<Player> getNearbyPlayers(Player player, double base, Predicate<Player> checker) {
        // REF: https://www.spigotmc.org/threads/performance-friendly-entity-finding.504599/

        Set<Player> nearbyPlayers = new HashSet<>();
        Location loc = player.getLocation();

        if (loc.getWorld() == null)
            return nearbyPlayers;

        Chunk chunk = loc.getChunk();

        for (Player p : chunk.getPlayersSeeingChunk()) {
            if (p != null && !p.getName().equals(player.getName()) && checker.test(p) &&
                    p.getLocation().distanceSquared(loc) <= Math.pow(base, 2))
                nearbyPlayers.add(p);
        }

        return nearbyPlayers;
    }
}

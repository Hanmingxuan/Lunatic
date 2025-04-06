package org.evlis.lunamatic.events;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import io.papermc.paper.world.MoonPhase;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.plugin.Plugin;
import org.evlis.lunamatic.GlobalVars;
import org.evlis.lunamatic.Lunamatic;
import org.evlis.lunamatic.triggers.NightEffects;
import org.evlis.lunamatic.utilities.PlayerMessage;
import org.evlis.lunamatic.utilities.ResetFlags;
import org.evlis.lunamatic.utilities.LangManager;
import org.jetbrains.annotations.NotNull;

public class PlayerJoin implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        LangManager langManager = LangManager.getInstance();
        Plugin plugin = Lunamatic.getInstance();
        Player player = event.getPlayer();
        World world = player.getWorld();
        if (world.getPlayers().isEmpty()) {
            // If the world was empty, there is a chance flags are stuck from
            // the world state the last player left at. Clear all flags to
            // prevent invalid moon states.
            ResetFlags.resetAll();
        } else {
            @NotNull MoonPhase moonPhase = world.getMoonPhase();
            long time = world.getTime();
            // harvest moon & blood moon are subsets of the full and new moons,
            // currently cannot be separated without a code rewrite.
            if (moonPhase == MoonPhase.FULL_MOON) {
                if (GlobalVars.harvestMoonToday) {
                    PlayerMessage.Send(player, langManager.getTranslation("harvest_moon_tonight"), NamedTextColor.GOLD);
                } else {
                    PlayerMessage.Send(player, langManager.getTranslation("full_moon_tonight"), NamedTextColor.YELLOW);
                }
                if (time >= 12610) {
                    NightEffects.ApplyMoonlight(plugin, player, MoonPhase.FULL_MOON, (24000 - (int)time));
                }
            } else if (moonPhase == MoonPhase.NEW_MOON) {
                if (GlobalVars.bloodMoonToday) {
                    PlayerMessage.Send(player, langManager.getTranslation("blood_moon_tonight"), NamedTextColor.DARK_RED);
                } else {
                    PlayerMessage.Send(player, langManager.getTranslation("new_moon_tonight"), NamedTextColor.DARK_GRAY);
                }
                if (time >= 12610) {
                    NightEffects.ApplyMoonlight(plugin, player, MoonPhase.NEW_MOON, (24000 - (int)time));
                }
            } else {
                // Catch for stuck flags during wrong moon-phase..
                // TO-DO: find out why this is needed?
                ResetFlags.resetAll();
            }
        }
    }
}

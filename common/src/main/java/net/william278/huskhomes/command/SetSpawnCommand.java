package net.william278.huskhomes.command;

import net.william278.huskhomes.HuskHomes;
import net.william278.huskhomes.player.OnlineUser;
import net.william278.huskhomes.position.PositionMeta;
import net.william278.huskhomes.position.Warp;
import net.william278.huskhomes.util.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SetSpawnCommand extends CommandBase {

    protected SetSpawnCommand(@NotNull HuskHomes implementor) {
        super("setspawn", Permission.COMMAND_SET_SPAWN, implementor);
    }

    @Override
    public void onExecute(@NotNull OnlineUser onlineUser, @NotNull String[] args) {
        if (args.length > 0) {
            plugin.getLocales().getLocale("error_invalid_syntax", "/setspawn")
                    .ifPresent(onlineUser::sendMessage);
            return;
        }

        onlineUser.getPosition().thenAccept(position -> {
            if (plugin.getSettings().crossServer && plugin.getSettings().globalSpawn) {
                final Optional<Warp> warp = plugin.getDatabase().getWarp(plugin.getSettings().globalSpawnName).join();
                if (warp.isPresent()) {
                    plugin.getSavedPositionManager().updateWarpPosition(warp.get(), position).join();
                } else {
                    plugin.getSavedPositionManager().setWarp(new PositionMeta(plugin.getSettings().globalSpawnName,
                            plugin.getLocales().getRawLocale("spawn_warp_default_description")
                                    .orElse("")), position).join();
                }
            } else {
                plugin.setServerSpawn(position);
            }
        }).thenRun(() -> plugin.getLocales().getLocale("set_spawn_success").ifPresent(onlineUser::sendMessage));
    }
}
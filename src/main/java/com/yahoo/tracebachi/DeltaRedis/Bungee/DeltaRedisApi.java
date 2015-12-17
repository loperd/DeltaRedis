package com.yahoo.tracebachi.DeltaRedis.Bungee;

import com.google.common.base.Preconditions;
import com.yahoo.tracebachi.DeltaRedis.Shared.Redis.Channels;
import com.yahoo.tracebachi.DeltaRedis.Shared.Redis.DRCommandSender;
import net.md_5.bungee.BungeeCord;

/**
 * Created by Trace Bachi (tracebachi@yahoo.com, BigBossZee) on 12/11/15.
 */
public class DeltaRedisApi
{
    public static final String SEND_MESSAGE_CHANNEL = "DR-SendMess";

    private DRCommandSender deltaSender;
    private DeltaRedisPlugin plugin;

    public DeltaRedisApi(DRCommandSender deltaSender, DeltaRedisPlugin plugin)
    {
        this.deltaSender = deltaSender;
        this.plugin = plugin;
    }

    public void shutdown()
    {
        this.deltaSender = null;
        this.plugin = null;
    }

    /**
     * Publishes a message to Redis.
     *
     * @param destination Server to send message to.
     * @param channel Channel of the message.
     * @param message The actual message.
     */
    public void publish(String destination, String channel, String message)
    {
        Preconditions.checkNotNull(destination, "Destination cannot be null.");
        Preconditions.checkNotNull(channel, "Channel cannot be null.");
        Preconditions.checkNotNull(message, "Message cannot be null.");

        if(destination.equals(deltaSender.getServerName()))
        {
            throw new IllegalArgumentException("Target channel cannot be " +
                "the same as the server's own channel.");
        }

        BungeeCord.getInstance().getScheduler().runAsync(plugin, () ->
        {
            deltaSender.publish(destination, channel, message);
        });
    }

    /**
     * Sends a message to a player in the specified server.
     *
     * @param server Name of the server to send the message to.
     * @param playerName Name of the player to send message to.
     * @param message Message to send.
     */
    public void sendMessageToPlayer(String server, String playerName, String message)
    {
        Preconditions.checkNotNull(playerName, "Player name cannot be null.");
        Preconditions.checkNotNull(message, "Message cannot be null.");
        Preconditions.checkArgument(!server.equals(Channels.BUNGEECORD), "Server cannot be BungeeCord.");

        BungeeCord.getInstance().getScheduler().runAsync(plugin, () -> {
            deltaSender.publish(server, SEND_MESSAGE_CHANNEL,
                playerName + "/\\" + message);
        });
    }
}

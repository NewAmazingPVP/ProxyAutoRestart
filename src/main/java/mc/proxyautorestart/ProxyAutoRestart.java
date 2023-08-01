package mc.proxyautorestart;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class ProxyAutoRestart extends Plugin {

    private final long[] warningTimes = {10, 7, 5, 3, 2, 1, 30};
    private final String[] warningMessages = {
            "Server will restart in 10 minutes!",
            "Server will restart in 7 minutes!",
            "Server will restart in 5 minutes!",
            "Server will restart in 3 minutes!",
            "Server will restart in 2 minutes!",
            "Server will restart in 1 minute!",
            "Server will restart in 30 seconds!"
    };
    private ScheduledTask restartTask;

    @Override
    public void onEnable() {
        scheduleRestart();
    }

    private void scheduleRestart() {
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
        Calendar restartTime = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
        restartTime.set(Calendar.HOUR_OF_DAY, 3);
        restartTime.set(Calendar.MINUTE, 0);
        restartTime.set(Calendar.SECOND, 0);

        if (now.after(restartTime)) {
            restartTime.add(Calendar.DAY_OF_MONTH, 1);
        }

        long initialDelay = restartTime.getTimeInMillis() - now.getTimeInMillis();

        for (int i = 0; i < warningTimes.length; i++) {
            long warningDelay = initialDelay - (warningTimes[i] * 60 * 1000);
            scheduleWarning(warningDelay, warningMessages[i]);
        }

        restartTask = getProxy().getScheduler().schedule(this, () -> {
            restartServer();
        }, initialDelay, TimeUnit.MILLISECONDS);
    }

    private void scheduleWarning(long delay, String message) {
        getProxy().getScheduler().schedule(this, () -> {
            broadcastWarning(message);
        }, delay, TimeUnit.MILLISECONDS);
    }

    private void broadcastWarning(String message) {
        getProxy().broadcast(ChatColor.RED + message);
    }

    private void restartServer() {
        getProxy().broadcast(ChatColor.RED + "Restarting the proxy...");
        getProxy().stop();
    }
}

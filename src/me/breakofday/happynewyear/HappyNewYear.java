package me.breakofday.happynewyear;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import me.breakofday.happynewyear.config.ConfigNodes;
import me.breakofday.happynewyear.config.Configuration;
import me.breakofday.happynewyear.config.enums.DisplayBar;
import me.breakofday.happynewyear.config.enums.DisplayMillis;
import me.breakofday.happynewyear.event.NewYearEvent;
import me.breakofday.happynewyear.util.EnumGetter;
import me.breakofday.happynewyear.util.Formatter;
import me.breakofday.happynewyear.util.TimeRemaining;
import org.apache.commons.lang.text.StrSubstitutor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.Note.Tone;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class HappyNewYear extends JavaPlugin implements Listener, CommandExecutor {

	private static final Calendar newYear = new GregorianCalendar(2022, Calendar.JANUARY, 1, 0, 0, 0);
	private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
	private static HappyNewYear plugin;
	private BossBar bossBar;
	private String prefix = "§cHappy§eNew§fYear§8》§f";

	public HappyNewYear() {
		plugin = this;
	}

	public static HappyNewYear getPlugin() {
		if (plugin != null) {
			return plugin;
		}
		throw new IllegalStateException("플러그인이 아직 초기화되지 않았습니다.");
	}

	private final Set<UUID> hid = new HashSet<>();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(Formatter.formatTitle(ChatColor.GOLD, ChatColor.YELLOW, "HappyNewYear"));
			sender.sendMessage("§e버전§7: §f" + plugin.getDescription().getVersion());
			sender.sendMessage("§b개발자§7: §fDaybreak 새벽 §8(§7디스코드: §f새벽#0833§8)");
			sender.sendMessage("§3§o/" + label + " help §7§o로 명령어 도움말을 확인하세요.");
			return true;
		}
		if ("help".equalsIgnoreCase(args[0])) {
			sender.sendMessage(new String[]{Formatter.formatTitle(ChatColor.GOLD, ChatColor.YELLOW, "HappyNewYear"),
					Formatter.formatCommand(label, "toggle", "보스바를 토글합니다.", false),
					Formatter.formatCommand(label, "simulate", "시뮬레이션용 명령어", true)});
			return true;
		} else if ("toggle".equalsIgnoreCase(args[0])) {
			if (bossBar == null) return false;
			if (sender instanceof Player) {
				final Player player = (Player) sender;
				if (hid.remove(player.getUniqueId())) {
					bossBar.addPlayer(player);
					player.sendMessage(Configuration.get(ConfigNodes.MSG_BAR_ON, String.class).replace("${prefix}", prefix));
				} else {
					final DisplayBar displayBar = EnumGetter.getOrDefault(DisplayBar.class, Configuration.get(ConfigNodes.BAR_DISPLAY, String.class), DisplayBar.FORCE);
					if (displayBar.isForced(new TimeRemaining(newYear.getTimeInMillis() - System.currentTimeMillis()))) {
						player.sendMessage(prefix + Configuration.get(ConfigNodes.MSG_BAR_OFF_FAILED, String.class));
					} else {
						hid.add(player.getUniqueId());
						bossBar.removePlayer(player);
						player.sendMessage(prefix + Configuration.get(ConfigNodes.MSG_BAR_OFF, String.class));
					}
				}
			} else sender.sendMessage(prefix + "콘솔에서 사용할 수 없습니다.");
			return true;
		} else if ("simulate".equalsIgnoreCase(args[0])) {
			if (sender.isOp()) {
				sender.sendMessage(simpleDateFormat.format(new Date(System.currentTimeMillis() + 90000)));
			} else sender.sendMessage(prefix + Configuration.get(ConfigNodes.MSG_NEED_OP, String.class));
		} else {
			sender.sendMessage(prefix + Configuration.get(ConfigNodes.MSG_NO_SUCH_COMMAND, String.class));
		}
		return true;
	}

	public void onEnable() {
		try {
			Configuration.load();
		} catch (IOException | InvalidConfigurationException ex) {
			getLogger().log(Level.SEVERE, "콘피그를 불러오는 도중 오류가 발생하였습니다.");
			Bukkit.getPluginManager().disablePlugin(this);
		}
		try {
			newYear.setTime(simpleDateFormat.parse(Configuration.get(ConfigNodes.THE_MOMENT)));
		} catch (ParseException ignored) {
		}
		this.prefix = Configuration.get(ConfigNodes.MSG_PREFIX, String.class);
		this.bossBar = Bukkit.createBossBar(
				"",
				EnumGetter.getOrDefault(BarColor.class, Configuration.get(ConfigNodes.BAR_COLOR_COUNT), BarColor.WHITE),
				EnumGetter.getOrDefault(BarStyle.class, Configuration.get(ConfigNodes.BAR_COLOR_COUNT), BarStyle.SOLID)
		);
		Bukkit.getPluginManager().registerEvents(this, this);

		new BukkitRunnable() {
			private final DisplayMillis displayMillis = EnumGetter.getOrDefault(DisplayMillis.class, Configuration.get(ConfigNodes.BAR_MESSAGE_PLACEHOLDER_DISPLAY_MILLIS, String.class), DisplayMillis.ALWAYS);
			private final DisplayBar displayBar = EnumGetter.getOrDefault(DisplayBar.class, Configuration.get(ConfigNodes.BAR_DISPLAY, String.class), DisplayBar.FORCE);
			private final long sunsetDiff = getSunsetDiff();
			private final Set<String> exceptedWorlds = new HashSet<>(Configuration.getList(ConfigNodes.TIME_INTERLOCK_EXCEPTED_WORLDS, String.class));
			private final Note note = Note.flat(1, Tone.G);
			private long lastSecond;

			private long getSunsetDiff() {
				final String sunset = Configuration.get(ConfigNodes.TIME_INTERLOCK_SUNSET, String.class);
				try {
					final String[] split = sunset.split(":");
					return 13200 - ((Integer.parseInt(split[0]) * 60L + Integer.parseInt(split[1])) * 24000 / 1440);
				} catch (IndexOutOfBoundsException | NumberFormatException ignored) {}
				return -3800;
			}

			@Override
			public void run() {
				final Calendar today = Calendar.getInstance();
				if (Configuration.get(ConfigNodes.TIME_INTERLOCK_ENABLE)) {
					long tick = ((today.get(Calendar.HOUR_OF_DAY) * 3600 + today.get(Calendar.MINUTE) * 60 + today.get(Calendar.SECOND)) * 24000 / 86400) + sunsetDiff;
					if (tick < 0) tick = 24000 - tick;
					else if (tick > 24000) tick = tick - 24000;
					for (World world : Bukkit.getWorlds()) {
						if (exceptedWorlds.contains(world.getName())) continue;
						world.setTime(tick);
					}
				}
				final TimeRemaining time = new TimeRemaining(newYear.getTimeInMillis() - today.getTimeInMillis());

				if (time.getDays() <= 0 && time.getHours() <= 0 && time.getMinutes() <= 0 && time.getSeconds() <= 0 && time.getMillis() <= 0) {
					cancel();
					bossBar.setColor(EnumGetter.getOrDefault(BarColor.class, Configuration.get(ConfigNodes.BAR_COLOR_DONE), BarColor.WHITE));
					bossBar.setStyle(EnumGetter.getOrDefault(BarStyle.class, Configuration.get(ConfigNodes.BAR_STYLE_DONE), BarStyle.SOLID));
					bossBar.setTitle(Configuration.get(ConfigNodes.BAR_MESSAGE_DONE));
					bossBar.setProgress(1.0);

					final String title = Configuration.get(ConfigNodes.TITLE_DONE_TITLE, String.class);
					final String subTitle = Configuration.get(ConfigNodes.TITLE_DONE_SUBTITLE, String.class);
					for (Player player : Bukkit.getOnlinePlayers()) {
						if (hid.contains(player.getUniqueId())) continue;
						player.sendTitle(title, subTitle,30, 200, 30);
						player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10f, 1.25f);
					}

					for (String cmd : Configuration.getList(ConfigNodes.COMMANDS_TO_RUN, String.class)) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
					}

					Bukkit.getPluginManager().callEvent(new NewYearEvent());
					return;
				}

				final Builder<String, String> placeholder = ImmutableMap.builder();
				{
					final String seconds;
					if (displayMillis.display(time)) {
						seconds = Configuration.get(ConfigNodes.BAR_MESSAGE_PLACEHOLDER_SECONDS, String.class).replace("${seconds}", time.getSeconds() + ":" + String.format("%02d", time.getMillis() / 10));
					} else {
						seconds = Configuration.get(ConfigNodes.BAR_MESSAGE_PLACEHOLDER_SECONDS, String.class).replace("${seconds}", String.valueOf(time.getSeconds()));
					}
					placeholder.put("seconds", seconds);
				}
				if (time.getDays() == 0) {
					placeholder.put("days", "");
				} else {
					placeholder.put("days", Configuration.get(ConfigNodes.BAR_MESSAGE_PLACEHOLDER_DAYS, String.class).replace("${days}", String.valueOf(time.getDays())));
				}

				if (time.getHours() == 0) {
					placeholder.put("hours", "");
				} else {
					placeholder.put("hours", Configuration.get(ConfigNodes.BAR_MESSAGE_PLACEHOLDER_HOURS, String.class).replace("${hours}", String.valueOf(time.getHours())));
				}

				if (time.getMinutes() == 0) {
					placeholder.put("minutes", "");
				} else {
					placeholder.put("minutes", Configuration.get(ConfigNodes.BAR_MESSAGE_PLACEHOLDER_MINUTES, String.class).replace("${minutes}", String.valueOf(time.getMinutes())));
				}
				bossBar.setTitle(StrSubstitutor.replace(
						Configuration.get(ConfigNodes.BAR_MESSAGE_COUNT),
						placeholder.build(), "${", "}"
				));
				if (time.getDays() == 0 && time.getHours() == 0 && time.getMinutes() == 0) {
					if (time.getSeconds() <= 59) {
						if (displayBar.isForced(time)) {
							if (!hid.isEmpty()) {
								hid.clear();
								for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
									bossBar.addPlayer(onlinePlayer);
								}
							}
						}
						if (time.getSeconds() <= 9) {
							bossBar.setProgress(Math.min(Math.max(time.getRaw() / 10000.0, 0), 1.0));
							final String title = Configuration.get(ConfigNodes.TITLE_COUNT_TITLE, String.class).replace("${seconds}", String.valueOf(time.getSeconds() + 1));
							final String subTitle = Configuration.get(ConfigNodes.TITLE_COUNT_SUBTITLE, String.class).replace("${seconds}", String.valueOf(time.getSeconds() + 1));
							for (Player player : Bukkit.getOnlinePlayers()) {
								if (hid.contains(player.getUniqueId())) continue;
								player.sendTitle(title, subTitle,0, 30, 0);
								if (this.lastSecond != time.getSeconds()) {
									player.playNote(player.getLocation(), Instrument.BELL, note);
								}
							}
						} else {
							bossBar.setProgress(Math.min(Math.max(time.getRaw() / 60000.0, 0), 1.0));
						}
					}
				}
				this.lastSecond = time.getSeconds();
			}
		}.runTaskTimer(this, 0, 1);
		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if (hid.contains(onlinePlayer.getUniqueId())) continue;
			bossBar.addPlayer(onlinePlayer);
		}
		getCommand("hny").setExecutor(this);
	}

	@EventHandler
	private void onPlayerJoin(final PlayerJoinEvent e) {
		if (bossBar != null && !hid.contains(e.getPlayer().getUniqueId())) {
			bossBar.addPlayer(e.getPlayer());
		}
	}

	public void onDisable() {
		if (bossBar != null) {
			bossBar.removeAll();
		}
	}

}

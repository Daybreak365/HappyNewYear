package me.breakofday.happynewyear.util;

import com.google.common.base.Strings;
import org.bukkit.ChatColor;

public class Formatter {

	private Formatter() {}

	/**
	 * @param bracketColor 괄호 색
	 * @param titleColor   제목 색
	 * @param title        제목
	 */
	public static String formatTitle(int length, ChatColor bracketColor, ChatColor titleColor, String title) {
		String base = Strings.repeat("_", length);
		int pivot = base.length() / 2;
		String center = "[ " + titleColor + title + bracketColor + " ]§m§l";
		String result = bracketColor + "§m§l" + base.substring(0, Math.max(0, (pivot - center.length() / 2))) + "§r" + bracketColor;
		result += center + base.substring(pivot + center.length() / 2);
		return result;
	}

	/**
	 * @param bracketColor 괄호 색
	 * @param titleColor   제목 색
	 * @param title        제목
	 */
	public static String formatTitle(ChatColor bracketColor, ChatColor titleColor, String title) {
		return formatTitle(49, bracketColor, titleColor, title);
	}

	public static String formatCommand(String label, String command, String help, boolean admin) {
		return (admin ? ChatColor.RED + "관리자: " : (ChatColor.GREEN + "유  저: ")) + ChatColor.GOLD + "/" + label + " " + ChatColor.YELLOW + command + " " + ChatColor.GRAY + ": " + ChatColor.WHITE + help;
	}

}

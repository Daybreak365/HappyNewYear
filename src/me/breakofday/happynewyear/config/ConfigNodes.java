package me.breakofday.happynewyear.config;

import com.google.common.collect.ImmutableList;

public enum ConfigNodes {

	THE_MOMENT("the-moment", "2022:1:1:00:00:00",
			"# 기다리고 있는 순간을 입력하세요.",
			"# 연:월:일:시:분:초 형식으로 입력해야 합니다.",
			"# ex: 2022:1:1:0:00:00"
	),
	BAR_DISPLAY("bar.display", "FORCE",
			"# 플레이어에게 보스바 표시 여부",
			"# FORCE (항상 표시)",
			"# OPTION (/hny 명령어로 숨길 수 있으나, 1분 남았을 때 표시됨)"
	),
	BAR_COLOR_COUNT("bar.color.count", "WHITE",
			"# 보스바 색 (카운트 중)",
			"# PINK, BLUE, RED, GREEN, YELLOW, PURPLE, WHITE 중 하나로 설정하세요."
	),
	BAR_COLOR_DONE("bar.color.done", "WHITE",
			"# 보스바 색 (카운트 완료)",
			"# PINK, BLUE, RED, GREEN, YELLOW, PURPLE, WHITE 중 하나로 설정하세요."
	),
	BAR_STYLE_COUNT("bar.style.count", "SOLID",
			"# 보스바 스타일 (카운트 중)",
			"# SOLID, SEGMENTED_6, SEGMENTED_10, SEGMENTED_12, SEGMENTED_20 중 하나로 설정하세요."
	),
	BAR_STYLE_DONE("bar.style.done", "SOLID",
			"# 보스바 스타일 (카운트 완료)",
			"# SOLID, SEGMENTED_6, SEGMENTED_10, SEGMENTED_12, SEGMENTED_20 중 하나로 설정하세요."
	),
	BAR_MESSAGE_COUNT("bar.message.count", "§d2022§f년까지${days}${hours}${minutes}${seconds}남았습니다.",
			"# 보스바 메시지 (카운트 중)",
			"# 사용 가능 플레이스홀더: ${days}, ${hours}, ${minutes}, ${seconds}"
	),
	BAR_MESSAGE_PLACEHOLDER_DAYS("bar.message.placeholder.days", " §e${days}§f일",
			"# days 플레이스홀더",
			"# 사용 가능 플레이스홀더: ${days}"
	),
	BAR_MESSAGE_PLACEHOLDER_HOURS("bar.message.placeholder.hours", " §e${hours}§f시간",
			"# hours 플레이스홀더",
			"# 사용 가능 플레이스홀더: ${hours}"
	),
	BAR_MESSAGE_PLACEHOLDER_MINUTES("bar.message.placeholder.minutes", " §e${minutes}§f분",
			"# minutes 플레이스홀더",
			"# 사용 가능 플레이스홀더: ${minutes}"
	),
	BAR_MESSAGE_PLACEHOLDER_SECONDS("bar.message.placeholder.seconds", " §e${seconds}§f초 ",
			"# minutes 플레이스홀더",
			"# 사용 가능 플레이스홀더: ${seconds}"
	),
	BAR_MESSAGE_PLACEHOLDER_DISPLAY_MILLIS("bar.message.placeholder.display-millis", "ALWAYS",
			"# ${seconds}에 밀리초 표시 여부",
			"# ALWAYS (항상 표시)",
			"# NEVER (항상 숨김)",
			"# FINALE (1분 남았을 때 표시)"
	),
	BAR_MESSAGE_DONE("bar.message.done", "§d임인년 §8(§7壬寅年§8) §f새해 복 많이 받으세요!",
			"# 보스바 메시지 (카운트 완료)"
	),
	TITLE_COUNT_TITLE("title.count.title", "§6${seconds}",
			"# 10초 카운트다운 title",
			"# 사용 가능 플레이스홀더: ${seconds}"
	),
	TITLE_COUNT_SUBTITLE("title.count.subtitle", "2021",
			"# 10초 카운트다운 subtitle",
			"# 사용 가능 플레이스홀더: ${seconds}"
	),
	TITLE_DONE_TITLE("title.done.title", "§6새해 복 많이 받으세요!",
			"# 카운트다운 완료 후 title"
	),
	TITLE_DONE_SUBTITLE("title.done.subtitle", "2022",
			"# 카운트다운 완료 후 subtitle"
	),
	COMMANDS_TO_RUN("commands-to-run", ImmutableList.of(
			"",
			"",
			""
	),
			"# 카운트다운 완료 후 콘솔에서 실행할 명령어"
	),
	MSG_PREFIX("msg.prefix", "§cHappy§eNew§fYear§8》§f"),
	MSG_BAR_OFF("msg.bar-off", "보스바가 더 이상 §c보이지 않습니다§f."),
	MSG_BAR_OFF_FAILED("msg.bar-off-failed", "서버 설정에 의해 보스바를 숨길 수 없습니다."),
	MSG_BAR_ON("msg.bar-on", "보스바가 이제 §a보입니다§f."),
	MSG_NEED_OP("msg.need-op", "관리자 권한이 필요합니다."),
	MSG_NO_SUCH_COMMAND("msg.no-such-command", "존재하지 않는 서브 명령어입니다."),
	TIME_INTERLOCK_ENABLE("time-interlock.enable", true, "# 마인크래프트 시간과 실제 시간을 연동합니다."),
	TIME_INTERLOCK_SUNSET("time-interlock.sunset", "17:23",
			"# 일몰 시각, 실제 시간 기준 (시:분)",
			"# https://hinode.pics/lang/ko-kr/maps/sun"
	),
	TIME_INTERLOCK_EXCEPTED_WORLDS("time-interlock.excepted-worlds", ImmutableList.of(
			"world_nether", "world_the_end"
	), "# 시간 연동 미적용 월드 목록");

	private final String path;
	private final Object defaultValue;
	private final String[] comments;

	ConfigNodes(String path, Object defaultValue, String... comments) {
		this.path = path;
		this.defaultValue = defaultValue;
		this.comments = comments;
	}

	public String getPath() {
		return path;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}

	public String[] getComments() {
		return comments;
	}

}

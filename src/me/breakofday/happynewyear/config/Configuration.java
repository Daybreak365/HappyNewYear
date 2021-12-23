package me.breakofday.happynewyear.config;

import me.breakofday.happynewyear.util.Files;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Configuration {

	private static final Logger logger = Logger.getLogger(Configuration.class.getName());
	private static File file = null;
	private static long lastModified;
	private static CommentedConfiguration config = null;

	public static boolean isLoaded() {
		return file != null && config != null;
	}

	public static void load() throws IOException, InvalidConfigurationException {
		if (!isLoaded()) {
			file = Files.newFile("config.yml");
			lastModified = file.lastModified();
			config = new CommentedConfiguration(file);
			update();
		}
	}

	public static void update() throws IOException, InvalidConfigurationException {
		config.load();

		for (Map.Entry<ConfigNodes, Cache> entry : cache.entrySet()) {
			Cache cache = entry.getValue();
			if (cache.isModifiedValue()) {
				config.set(entry.getKey().getPath(), cache.getValue());
			}
		}

		cache.clear();
		for (ConfigNodes node : ConfigNodes.values()) {
			Object value = config.get(node.getPath());
			config.addComment(node.getPath(), node.getComments());
			if (value != null) {
				cache.put(node, new Cache(false, value));
			} else {
				config.set(node.getPath(), node.getDefaultValue());
				cache.put(node, new Cache(false, node.getDefaultValue()));
			}
		}
		config.save();
		lastModified = file.lastModified();
	}

	private static final EnumMap<ConfigNodes, Cache> cache = new EnumMap<>(ConfigNodes.class);

	@SuppressWarnings("unchecked")
	public static <T> T get(ConfigNodes configNode) throws IllegalStateException {
		if (!isLoaded()) {
			logger.log(Level.SEVERE, "콘피그가 불러와지지 않은 상태에서 접근을 시도하였습니다.");
			throw new IllegalStateException("콘피그가 아직 불러와지지 않았습니다.");
		}
		if (lastModified != file.lastModified()) {
			try {
				update();
			} catch (IOException | InvalidConfigurationException e) {
				logger.log(Level.SEVERE, "콘피그를 다시 불러오는 도중 오류가 발생하였습니다.");
			}
		}
		return (T) cache.get(configNode).getValue();
	}

	@SuppressWarnings("unchecked")
	public static <T> T get(ConfigNodes configNode, Class<T> clazz) throws IllegalStateException {
		if (!isLoaded()) {
			logger.log(Level.SEVERE, "콘피그가 불러와지지 않은 상태에서 접근을 시도하였습니다.");
			throw new IllegalStateException("콘피그가 아직 불러와지지 않았습니다.");
		}
		if (lastModified != file.lastModified()) {
			try {
				update();
			} catch (IOException | InvalidConfigurationException e) {
				logger.log(Level.SEVERE, "콘피그를 다시 불러오는 도중 오류가 발생하였습니다.");
			}
		}
		return (T) cache.get(configNode).getValue();
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> getList(ConfigNodes configNode, Class<T> clazz) throws IllegalStateException {
		List<?> list = get(configNode);
		List<T> newList = new ArrayList<>();
		for (Object object : list) {
			if (object != null && clazz.isAssignableFrom(object.getClass())) {
				newList.add((T) object);
			}
		}
		return newList;
	}

	public static void modifyProperty(ConfigNodes node, Object value) {
		cache.put(node, new Cache(true, value));
	}

	private static class Cache {

		private final boolean isModifiedValue;
		private final Object value;

		Cache(boolean isModifiedValue, Object value) {
			this.isModifiedValue = isModifiedValue;
			this.value = value;
		}

		boolean isModifiedValue() {
			return isModifiedValue;
		}

		Object getValue() {
			return value;
		}

	}
}
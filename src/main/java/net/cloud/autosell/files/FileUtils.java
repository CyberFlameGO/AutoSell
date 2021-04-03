package net.cloud.autosell.files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import net.cloud.autosell.AutoSell;
import net.cloud.autosell.files.enums.CloudFileType;
import org.bukkit.configuration.file.FileConfiguration;

public class FileUtils {

	private AutoSell plugin;
	public FileUtils(AutoSell plugin) {
		this.plugin = plugin;
		init();
	}

	Map<CloudFileType, FileConfiguration> configMaps = new HashMap<CloudFileType, FileConfiguration>();
	Map<CloudFileType, File> fileMaps = new HashMap<CloudFileType, File>();

	public void init() {
		for(CloudFileType type : CloudFileType.values()) {
			configMaps.put(type, plugin.getFileManager().getConfig(type.getName()));
			fileMaps.put(type, plugin.getFileManager().getFile(type.getName()));
		}
		reload();
	}

	public void reload() {
		for(CloudFileType type : CloudFileType.values()) {
			if(!((configMaps.containsKey(type)) || (fileMaps.containsKey(type)))) {
				createFileByType(type);
			} else {
				reloadFile(configMaps.get(type), fileMaps.get(type));
			}
		}
	}

	public void destroy() {
		for(CloudFileType type : CloudFileType.values()) {
			if(!((configMaps.containsKey(type)) || (fileMaps.containsKey(type)))) continue;

			configMaps.remove(type);
			fileMaps.remove(type);
		}
	}

	public void reloadFile(FileConfiguration config, File file) {
		plugin.getFileManager().reloadConfig(config, file);
	}

	public void saveFile(FileConfiguration config, File file) {
		plugin.getFileManager().saveConfig(config, file);
	}

	public void createFileByType(CloudFileType type) {
		configMaps.put(type, plugin.getFileManager().getConfig(type.getName()));
		fileMaps.put(type, plugin.getFileManager().getFile(type.getName()));
	}

	public FileConfiguration getFileByType(CloudFileType type) {
		if(!((configMaps.containsKey(type)) || (fileMaps.containsKey(type)))) {
			createFileByType(type);
		}

		return configMaps.get(type);
	}
	
	public File getAFileByType(CloudFileType type) {
		if(!((configMaps.containsKey(type)) || (fileMaps.containsKey(type)))) {
			createFileByType(type);
		}

		return fileMaps.get(type);
	}

	public void writeToLogsFile(String name, String line) {
		String fileName = plugin.getDataFolder() + File.separator + "logs" + File.separator + name + ".txt";
		PrintWriter printWriter = null;
		File file = new File(fileName);
		try {
			if(!file.exists()) {
				file.createNewFile();
			}

			printWriter = new PrintWriter(new FileOutputStream(fileName, true));
			printWriter.write(line + System.lineSeparator());
		} catch (IOException ioex) {
			ioex.printStackTrace();
		} finally {
			if (printWriter != null) {
				printWriter.flush();
				printWriter.close();
			}	
		}
	}

	public void saveFileByType(CloudFileType type) {
		if(!(configMaps.containsKey(type) || fileMaps.containsKey(type))) {
			saveFile(plugin.getFileManager().getConfig(type.getName()), plugin.getFileManager().getFile(type.getName()));
		} else {
			saveFile(configMaps.get(type), fileMaps.get(type));
		}
	}

}

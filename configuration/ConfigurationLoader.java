package configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ConfigurationLoader {

	private static final String FILENAME = "configuration/config.properties";

	private ConfigurationLoader() {
	}

	public static Properties load() {
		System.out.println("Loadind file - Working directory : " + System.getProperty("user.dir"));
		Properties props = new Properties();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(ConfigurationLoader.FILENAME);
			props.load(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return props;
	}
}

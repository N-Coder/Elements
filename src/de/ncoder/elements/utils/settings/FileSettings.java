package de.ncoder.elements.utils.settings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class FileSettings extends AbstractSettings {
	private Properties properties;

	public FileSettings(File f) throws FileNotFoundException, IOException {
		super();
		properties = new Properties();
		properties.load(new FileReader(f));
	}

	public FileSettings(String f) throws FileNotFoundException, IOException {
		this(new File(f));
	}

	@Override
	public String getValue(String key) {
		return properties.getProperty(key, getDefaults().getValue(key));
	}

	@Override
	public void setValue(String key, String value) {
		properties.setProperty(key, value);
	}

	@Override
	public void resetValue(String key) {
		properties.remove(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getKeys() {
		return new ArrayList<String>((Collection<? extends String>) properties.keys());
	}

}

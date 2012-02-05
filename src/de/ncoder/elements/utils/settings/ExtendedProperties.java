package de.ncoder.elements.utils.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtendedProperties extends Properties {
	private static final long serialVersionUID = 4695882101450085983L;
	public static final String LIST_SEPERATOR = ";";
	public static final List<SettingsFunction> FUNCTIONS = new ArrayList<SettingsFunction>();

	static {
		FUNCTIONS.add(SettingsFunction.IF);
		FUNCTIONS.add(SettingsFunction.NN);
		FUNCTIONS.add(SettingsFunction.MAX);
		FUNCTIONS.add(SettingsFunction.MIN);
	}

	public ExtendedProperties(String file) throws IOException {
		this(new File(file));
	}

	public ExtendedProperties(File file) throws IOException {
		super();
		load(file);
	}

	public ExtendedProperties(InputStream in) throws IOException {
		super();
		load(in);
	}

	public ExtendedProperties(String file, Properties defaults) throws IOException {
		this(new File(file), defaults);
	}

	public ExtendedProperties(File file, Properties defaults) throws IOException {
		super(defaults);
		try {
			load(file);
		} catch (FileNotFoundException e) {
			//We don't care, because we have defaults
		}
	}

	public ExtendedProperties(InputStream in, Properties defaults) throws IOException {
		super(defaults);
		load(in);
	}

	@Override
	public String getProperty(String key) {
		String value = parseProperty(super.getProperty(key));
		return value;
	}

	public String parseProperty(String value) {
		if (value != null) {
			for (String k : this.stringPropertyNames()) {
				if (value.contains("%" + k + "%")) {
					value = value.replaceAll("%" + k + "%", getProperty(k));
				}
				if (value.contains("#" + k + "#")) {
					//Windows Seperator is interpreted as escape symbol when double-parsed
					value = value.replaceAll("#" + k + "#", getFile(k).toString().replaceAll("\\\\", "\\\\\\\\"));
				}
			}
			for (SettingsFunction f : FUNCTIONS) {
				Pattern p = Pattern.compile("\\%" + f.getName() + "\\{(.*)\\}");
				Matcher m = p.matcher(value);
				StringBuffer bob = new StringBuffer(value.length());
				while (m.find()) {
					m.appendReplacement(bob, m.group(1));
				}
				m.appendTail(bob);
				value = bob.toString();
			}
		}
		return value;
	}

	@Override
	public String getProperty(String key, String defaultValue) {
		String prop = getProperty(key);
		if (prop == null || prop.isEmpty()) {
			prop = defaultValue;
		}
		return prop;
	}

	public List<String> getPropertyList(String key) {
		List<String> values = Arrays.asList(getProperty(key).split(LIST_SEPERATOR));
		return values;
	}

	public void setPropertyList(String key, List<String> values) {
		StringBuilder bob = new StringBuilder();
		for (String s : values) {
			if (bob.length() > 0) {
				bob.append(LIST_SEPERATOR);
			}
			bob.append(s);
		}
		setProperty(key, bob.toString());
	}

	public File getFile(String key) {
		return new File(getProperty(key));
	}

	public boolean getBoolean(String key) {
		String value = getProperty(key).toLowerCase().trim();
		return value.startsWith("t") || value.startsWith("1");
	}

	public void load(File file) throws IOException {
		FileInputStream in = new FileInputStream(file);
		load(in);
		in.close();
	}

	public void store(File file) throws IOException {
		if (!file.exists()) {
			file.createNewFile();
		}
		FileOutputStream out = new FileOutputStream(file);
		store(out, "");
		out.close();
	}

}

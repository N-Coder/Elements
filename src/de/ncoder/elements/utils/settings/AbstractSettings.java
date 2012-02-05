package de.ncoder.elements.utils.settings;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractSettings {
	private static List<SettingsFunction> functions;
	private AbstractSettings defaults;

	public AbstractSettings() {}

	static {
		addFunction(SettingsFunction.IF);
		addFunction(SettingsFunction.NN);
		addFunction(SettingsFunction.MIN);
		addFunction(SettingsFunction.MAX);
	}

	public abstract String getValue(String key);

	public abstract void setValue(String key, String value);

	public abstract void resetValue(String key);

	public abstract List<String> getKeys();

	public String parseValue(String value) {
		if (value != null) {
			for (String k : this.getKeys()) {
				value = value.replaceAll("%" + k + "%", getValue(k));
				//Windows Seperator is interpreted as escape symbol when double-parsed
				value = value.replaceAll("#" + k + "#", getFile(k).toString().replaceAll("\\\\", "\\\\\\\\"));
			}
			for (SettingsFunction f : getFunctions()) {
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

	public File getFile(String key) {
		return new File(getValue(key));
	}

	public boolean getBoolean(String key) {
		String v = getValue(key);
		return v.equals("true") || v.equals("yes") || v.equals("1");
	}

	public int getInteger(String key) {
		return Integer.parseInt(getValue(key));
	}

	public double getDouble(String key) {
		return Double.parseDouble(getValue(key));
	}

	public List<String> getAllKeys() {
		List<String> keys = getKeys();
		if (getDefaults() != null) {
			keys.addAll(getDefaults().getAllKeys());
		}
		return keys;
	}

	public AbstractSettings getDefaults() {
		return defaults;
	}

	public void setDefaults(AbstractSettings defaults) {
		this.defaults = defaults;
	}

	public static List<SettingsFunction> getFunctions() {
		return Collections.unmodifiableList(functions);
	}

	public static SettingsFunction getFunction(String name) {
		for (SettingsFunction f : functions) {
			if (f.getName().equals(name)) {
				return f;
			}
		}
		return null;
	}

	public static boolean removeFunction(String name) {
		for (SettingsFunction f : functions) {
			if (f.getName().equals(name)) {
				return removeFunction(f);
			}
		}
		return false;
	}

	public static boolean removeFunction(SettingsFunction f) {
		return functions.remove(f);
	}

	public static void addFunction(SettingsFunction f) {
		SettingsFunction old = getFunction(f.getName());
		if (old != null) {
			removeFunction(old);
		}
		functions.add(f);
	}

}

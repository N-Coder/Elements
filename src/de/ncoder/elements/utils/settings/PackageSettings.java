package de.ncoder.elements.utils.settings;

import java.util.List;
import java.util.prefs.Preferences;

public class PackageSettings extends AbstractSettings{
	private Preferences preferences;
	
	public PackageSettings(Class<?> c, boolean local) {
		if(local) {
			preferences = Preferences.userNodeForPackage(c);
		} else {
			preferences = Preferences.systemNodeForPackage(c);
		}
	}
	
	@Override
	public String getValue(String key) {
		return preferences.get(key, getDefaults().getValue(key));
	}

	@Override
	public void setValue(String key, String value) {
		preferences.put(key, value);
	}

	@Override
	public void resetValue(String key) {
		preferences.
	}

	@Override
	public List<String> getKeys() {
		// TODO Auto-generated method stub
		return null;
	}

}

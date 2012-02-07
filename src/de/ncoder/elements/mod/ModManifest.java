package de.ncoder.elements.mod;

import java.util.HashMap;
import java.util.Map;

public final class ModManifest {
	private String modName;
	private String modClass;
	private String modVersionName;
	private int modVersion;
	private String modDescription;
	private Map<String, Object> modAdditional = new HashMap<String, Object>();

	@SuppressWarnings("unused")
	private ModManifest() {}

	public ModManifest(String modName, String modClass, String modVersionName, int modVersion, String modDescription) {
		this.modName = modName;
		this.modClass = modClass;
		this.modVersionName = modVersionName;
		this.modVersion = modVersion;
		this.modDescription = modDescription;
		System.out.println(this);
	}

	public String getModName() {
		return modName;
	}

	public String getModClass() {
		return modClass;
	}

	public String getModVersionName() {
		return modVersionName;
	}

	public int getModVersion() {
		return modVersion;
	}

	public String getModDescription() {
		return modDescription;
	}

	public Object getAdditional(String key) {
		return modAdditional.get(key);
	}

	public void setAdditional(String key, Object value) {
		modAdditional.put(key, value);
	}

	@Override
	public String toString() {
		return getModName() + " " + getModVersionName() + "\n" + getModDescription() + "\n--" + getModClass() + ":" + getModVersion();
	}
}

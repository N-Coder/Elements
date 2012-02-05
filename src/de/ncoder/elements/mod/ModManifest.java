package de.ncoder.elements.mod;



public final class ModManifest {
	private String modName;
	private String modClass;
	private String modVersionName;
	private int modVersion;
	private String modDescription;

	@SuppressWarnings("unused")
	private ModManifest() {}

	public ModManifest(String modName, String modClass, String modVersionName, int modVersion, String modDescription) {
		this.modName = modName;
		this.modClass = modClass;
		this.modVersionName = modVersionName;
		this.modVersion = modVersion;
		this.modDescription = modDescription;
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

	@Override
	public String toString() {
		return getModName() + " " + getModVersionName() + "\n" + getModDescription() + "\n--" + getModClass() + ":" + getModVersion();
	}
}

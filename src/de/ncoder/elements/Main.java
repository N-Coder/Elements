package de.ncoder.elements;

import java.io.FileWriter;

import de.ncoder.elements.mod.ModManifest;
import de.ncoder.elements.mod.SimpleMod;

public class Main {
	public static void main(String[] args) throws Exception {
		Manager manager = new Manager(args);
		manager.create();
		ModManifest m = new ModManifest("abc", SimpleMod.class.getName(), "1", 1, "desc");
		m.setAdditional(SimpleMod.KEYS_ADDITIONAL_NAME, manager.getGuiManager().getSelectActions());
		manager.getXStream().toXML(m, new FileWriter("/home/niko/manifest.xml"));
	}
}

package de.ncoder.elements.mod;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.ncoder.elements.Manager;
import de.ncoder.nlib.ListeningList;

public class ModManager {
	private Manager manager;

	private ListeningList<Mod> mods = new ListeningList<Mod>();

	public ModManager(Manager manager) {
		this.manager = manager;
	}

	public void create() {

	}

	public void refresh() {

	}

	public ListeningList<Mod> getMods() {
		return mods;
	}

	public Mod addMod(Mod mod) {
		mods.add(mod);
		return mod;
	}

	public Mod addMod(URL... locations) throws IllegalArgumentException, SecurityException, ReflectiveOperationException, IOException {
		return addMod(loadMod(locations));
	}

	public void removeMod(Mod mod) {
		mod.setActive(false);
		mods.remove(mod);
	}
	
	public Mod getMod(String name) {
		for(Mod m:mods) {
			if(m.getModManifest().getModName().equals(name)) {
				return m;
			}
		}
		return null;
	}
	
	public Mod getMod(Class<? extends Mod> c) {
		for(Mod m:mods) {
			if(m.getClass().isAssignableFrom(c)) {
				return m;
			}
		}
		return null;
	}
	

	@SuppressWarnings("unchecked")
	public Mod loadMod(URL... locations) throws ReflectiveOperationException, IllegalArgumentException, SecurityException, IOException {
		URLClassLoader loader = new URLClassLoader(locations, getClass().getClassLoader());
		ModManifest manifest = loadManifest(loader);
		Class<? extends Mod> c = (Class<? extends Mod>) loader.loadClass(manifest.getModClass());
		Mod mod = c.newInstance();
		mod.setModManifest(manifest);
		mod.setManager(manager);
		mod.setUrls(Arrays.asList(locations));
		return mod;
	}

	public Map<URL, Exception> loadMods() throws FileNotFoundException {
		return loadMods(new FileInputStream(manager.getProperties().getProperty("mod.index")));
	}

	public Map<URL, Exception> loadMods(InputStream in) {
		return loadMods(readMods(in));
	}

	public Map<URL, Exception> loadMods(List<URL> modURLs, boolean everyMod) {
		Map<URL, Boolean> modMap = new HashMap<URL, Boolean>();
		for (URL url : modURLs) {
			modMap.put(url, everyMod);
		}
		return loadMods(modMap);
	}

	public Map<URL, Exception> loadMods(Map<URL, Boolean> modURLs) {
		Map<URL, Exception> exceptions = new HashMap<URL, Exception>();
		for (Entry<URL, Boolean> e : modURLs.entrySet()) {
			try {
				Mod m = addMod(e.getKey());
				m.setActive(e.getValue());
			} catch (Exception ex) {
				exceptions.put(e.getKey(), ex);
			}
		}
		return exceptions;
	}
	
	public void saveMods() throws FileNotFoundException {
		saveMods(new FileOutputStream(manager.getProperties().getProperty("mod.index")));
	}
	
	public void saveMods(OutputStream out) {
		Map<URL, Boolean> modMap = new HashMap<URL, Boolean>();
		for (Mod mod : mods) {
			modMap.put(mod.getUrls().get(0), mod.isActive());
		}
		writeMods(modMap, out);
	}

	public ModManifest loadManifest(URLClassLoader loader) throws IOException {
		URL manifestURL = loader.findResource("manifest.xml");
		if (manifestURL == null) {
			manifestURL = loader.findResource("/manifest.xml");
			if (manifestURL == null) {
				throw new FileNotFoundException("manifest.xml couldn't be found by URLClassLoader");
			}
		}
		manager.updateXStream();
		return (ModManifest) manager.getXStream().fromXML(manifestURL.openConnection().getInputStream());
	}

	@SuppressWarnings("unchecked")
	public Map<URL, Boolean> readMods(InputStream in) {
		manager.updateXStream();
		return (Map<URL, Boolean>) manager.getXStream().fromXML(in);
	}
	
	public void writeMods(Map<URL, Boolean> map, OutputStream out) {
		manager.getXStream().toXML(map, out);
	}

	public void reloadMods() throws FileNotFoundException {
		reloadMods(new FileInputStream(manager.getProperties().getProperty("mod.index")));
	}

	public void reloadMods(InputStream in) {
		for (Mod m : mods) {
			m.setActive(false);
		}
		mods.clear();
		loadMods(in);
	}

	public URL[] getActiveModURLs() {
		List<URL> urls = new ArrayList<URL>();
		for (Mod m : mods) {
			if (m.isActive()) {
				urls.addAll(m.getUrls());
			}
		}
		URL[] urlArray = new URL[urls.size()];
		urls.toArray(urlArray);
		return urlArray;
	}

	public Manager getManager() {
		return manager;
	}
}

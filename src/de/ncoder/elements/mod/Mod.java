package de.ncoder.elements.mod;

import java.net.URL;
import java.util.List;

import javax.swing.JFrame;

import de.ncoder.elements.Manager;
import de.ncoder.elements.gui.frame.ManifestFrame;

public abstract class Mod {
	private ModManifest modManifest;
	private List<URL> urls;
	private boolean active;
	protected Manager manager;

	public Mod() {}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
		activeChanged();
	}

	public abstract void activeChanged();

	public final ModManifest getModManifest() {
		return modManifest;
	}

	public final void setModManifest(ModManifest modManifest) {
		this.modManifest = modManifest;
	}

	public List<URL> getUrls() {
		return urls;
	}

	public void setUrls(List<URL> urls) {
		this.urls = urls;
	}

	public final Manager getManager() {
		return manager;
	}

	public final void setManager(Manager manager) {
		this.manager = manager;
	}
	
	public JFrame getInfoFrame() {
		return new ManifestFrame(getModManifest());
	}
}

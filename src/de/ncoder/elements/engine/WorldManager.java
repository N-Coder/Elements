package de.ncoder.elements.engine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import de.ncoder.elements.Manager;
import de.ncoder.nlib.Timer;

/**
 * Manager for the World and the Timer controlling physics.
 * Additional, this Manager is able to save and load the World.
 * 
 * @author Niko Fink
 */
public class WorldManager {
	private Manager manager;
	private World world;
	private Timer timer;

	/**
	 * Default Listener causing the World's physics to be updated.
	 */
	private Runnable timerListener = new Runnable() {
		@Override
		public void run() {
			world.act();
		}
	};

	/**
	 * Makes a new WorldManager with an empty World and a Timer with the default TiemrListener
	 * 
	 * @param manager
	 *            The parent Manager
	 */
	public WorldManager(Manager manager) {
		this.manager = manager;
		world = new World();
		timer = new Timer();
		timer.addListener(timerListener);
	}

	/**
	 * Starts the timer
	 */
	public void create() {
		timer.start();
	}

	/**
	 * Does nothing
	 */
	public void refresh() {

	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public Timer getTimer() {
		return timer;
	}

	/**
	 * @return Default Listener causing the World's physics to be updated.
	 */
	public Runnable getTimerListener() {
		return timerListener;
	}

	public Manager getManager() {
		return manager;
	}

	// ---------------------------------LOAD & SAVE-----------------------------

	/**
	 * Tries to uncompress the given file and load its contents using the <code>Manager.read(Reader)</code> method.
	 * If the uncompressing fails, it tries to load the file directly, additional the occured exception is sent to the Manager.
	 * If the World still couldn't be loaded, an Exception is thrown.
	 * 
	 * @param file
	 *            The file which should be loaded
	 * @throws IOException
	 * 
	 * @see {@link WorldManager#load(File)}
	 */
	public void load(File file) throws IOException {
		try {
			ZipFile zFile = new ZipFile(file);
			load(new InputStreamReader(zFile.getInputStream(zFile.getEntry("data.xml"))));
		} catch (ZipException e) {
			manager.exception(e, "Level seems to be saved by an old version, trying uncompressed mode. If this excpetion keeps occuring, try using the converter.");
			load(new FileReader(file));
		}
	}

	/**
	 * Reads an Object from the given Reader using Manager's XSTREAM and tries to set it as the new World.
	 * 
	 * @param read
	 *            The Reader to read the Obejct from
	 * @see {@link Manager#XSTREAM}
	 */
	public void load(Reader read) {
		synchronized (getWorld()) {
			manager.updateXStream();
			Object loaded = manager.getXStream().fromXML(read);
			if (loaded instanceof World) {
				setWorld((World) loaded);
			} else if (loaded instanceof Element[][]) {
				setWorld(new World((Element[][]) loaded));
			} else {
				throw new ClassCastException("Couldn't parse file contents as Elements Level");
			}
			getWorld().setActive(false);
		}
	}

	/**
	 * Saves the World using Zip compression to the given File. If it doesn't exist, a new file is created.
	 * 
	 * @param file
	 * @throws IOException
	 * @see {@link WorldManager#save(Writer)}
	 */
	public void save(File file) throws IOException {
		file = checkFile(file);
		if (!file.exists()) {
			file.createNewFile();
		}
		ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(file));
		ZipEntry entry = new ZipEntry("data.xml");
		zout.putNextEntry(entry);
		save(new OutputStreamWriter(zout));
		zout.close();
	}

	/**
	 * Saves the World to the given Writer using the Manager's XSTREAM
	 * 
	 * @param writer
	 * @see {@link Manager#XSTREAM}
	 */
	public void save(Writer writer) {
		synchronized (getWorld()) {
			manager.getXStream().toXML(getWorld(), writer);
		}
	}
	
	/**
	 * Checks a file for the correct extension
	 * 
	 * @param file The file to check
	 * @return The file with the extension corrected, if required
	 */
	public File checkFile(File file) {
		if(!file.getPath().endsWith("."+manager.getProperties().getProperty("save.extension"))) {
			file = new File(file.toString()+"."+manager.getProperties().getProperty("save.extension"));
		}
		return file;
	}
}

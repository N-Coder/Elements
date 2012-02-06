package de.ncoder.elements;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import de.ncoder.elements.engine.Element;
import de.ncoder.elements.engine.World;
import de.ncoder.elements.engine.WorldManager;
import de.ncoder.elements.gui.GUIManager;
import de.ncoder.elements.gui.action.Action;
import de.ncoder.elements.gui.action.SelectAction;
import de.ncoder.elements.mod.Mod;
import de.ncoder.elements.mod.ModManager;
import de.ncoder.elements.mod.ModManifest;
import de.ncoder.elements.mod.SimpleMod;
import de.ncoder.elements.utils.ColorConverter;
import de.ncoder.elements.utils.StringMapConverter;
import de.ncoder.elements.utils.settings.ExtendedProperties;
import de.ncoder.nlib.Key;
import de.ncoder.nlib.gui.NFrame;

/**
 * The main manager unifying managers for the World Engine, the GUI and Modifications.
 * Additionally, it loads Properties, manages the XSTREAM used for serialization and receives messages and exceptions.
 * Every Object should somehow be able to access this Manager.
 * 
 * @author Niko Fink
 */
public class Manager {
	/**
	 * Instance of the World Manager
	 * 
	 * @see {@link WorldManager }
	 */
	private WorldManager worldManager;
	/**
	 * Instance of the GUIManager
	 * 
	 * @see {@link GUIManager }
	 */
	private GUIManager guiManager;
	/**
	 * Instance of the ModManager
	 * 
	 * @see {@link ModManager }
	 */
	private ModManager modManager;
	/**
	 * Instance of the applications' properties.
	 * This points to the most recent instance, which receives all changes, but loads defaults from previous versions.
	 * 
	 * @see {@link ExtendedProperties }
	 */
	private ExtendedProperties properties;

	/**
	 * List of ExceptionListeners.
	 * By default, a Listener is added which simply prints messages to stderr.
	 * 
	 * @see {@link ExceptionListener }
	 * @see {@link Manager#defaultExceptionListener }
	 * @see {@link Manager#exception(Exception, String) }
	 * @see {@link Manager#addExceptionListener(ExceptionListener) }
	 * @see {@link Manager#removeExceptionListener(Object) }
	 */
	private List<ExceptionListener> exceptionListeners = new ArrayList<ExceptionListener>();
	/**
	 * List of MessageListeners.
	 * By default, a Listener is added which simply prints exceptions to stdout.
	 * 
	 * @see {@link MessageListener }
	 * @see {@link Manager#defaultMessageListener }
	 * @see {@link Manager#message(String, String) }
	 * @see {@link Manager#addMessageListener(MessageListener) }
	 * @see {@link Manager#removeMessageListener(Object) }
	 */
	private List<MessageListener> messageListeners = new ArrayList<MessageListener>();
	/**
	 * The default ExcpetionListener, which which simply prints messages to stderr.
	 * 
	 * @see {@link Manager#exceptionListeners }
	 * @see {@link Manager#getDefaultExceptionListener() }
	 */
	private ExceptionListener defaultExceptionListener = new ExceptionListener() {
		@Override
		public void exception(Exception exception, String message) {
			System.err.println(message);
			exception.printStackTrace();
		}
	};
	/**
	 * The default MessageListener, which which simply prints messages to stdout.
	 * 
	 * @see {@link Manager#messageListeners }
	 * @see {@link Manager#getDefaultMessageListener() }
	 */
	private MessageListener defaultMessageListener = new MessageListener() {
		@Override
		public void message(String message, String title) {
			System.out.println("---------------" + title + "---------------");
			System.out.println(message);
		}
	};

	/**
	 * A instance of the XStream used to serialize the World
	 * 
	 * @see {@link WorldManager#load(java.io.Reader) }
	 * @see {@link WorldManager#save(java.io.Writer) }
	 */
	private XStream xStream;
	/**
	 * The path to the properties file with the default Properties, as resource of the Manager's Class.
	 * 
	 * @see {@link Manager#Manager() }
	 */
	public static final String SETTINGS_DEFAULTS = "/defaults.properties";
	/**
	 * The path to the current local properties, as resource of the Manager's Class.
	 */
	public static final String SETTINGS_FILE = "/settings.properties";
	/**
	 * The file where a Map<Key, AbstractAction<?>> with all the Keys can be found, as resource of the Manager's Class.
	 * 
	 * @see {@link GUIManager#getAction(Key) }
	 * @see {@link GUIManager#getActionListener() }
	 */
	public static final String KEYS_FILE = "/defaultKeys.xml";

	/**
	 * Initializes all the Managers.
	 * The properties by default have the following structure:
	 * Settings>Settings-Defaults>System-Properties
	 * The two default Listeners are added.
	 * 
	 * @throws IOException
	 *             When Properties couldn't be loaded.
	 */
	public Manager() throws IOException {
		initXStream();
		ExtendedProperties defaults = new ExtendedProperties(getClass().getResourceAsStream(SETTINGS_DEFAULTS), System.getProperties());
		this.properties = new ExtendedProperties(getClass().getResourceAsStream(SETTINGS_FILE), defaults);
		NFrame.TITLE_PREFIX = "Elements";
		this.worldManager = new WorldManager(this);
		this.guiManager = new GUIManager(this);
		this.modManager = new ModManager(this);

		addExceptionListener(defaultExceptionListener);
		addMessageListener(defaultMessageListener);
	}

	/**
	 * After calling <code>Manager.Manager()</code> parses all the given arguments, looking for this values.
	 * <dl>
	 * <dt>-m</dt>
	 * <dd>The next value is the Path to a Modification, which will be loaded and activated.</dd>
	 * <dt>-l</dt>
	 * <dd>The next value is the File which should be loaded by default.</dd>
	 * <dt>-s</dt>
	 * <dd>The next value is the File with Properties, which should wrap the local ones.</dd>
	 * </dl>
	 * 
	 * @param args
	 *            Arguments, usually taken from console input.
	 * @throws IOException
	 *             When Properties couldn't be loaded.
	 * @see {@link Manager#Manager() }
	 */
	public Manager(String... args) throws IOException {
		this();
		Iterator<String> iter = Arrays.asList(args).iterator();
		while (iter.hasNext()) {
			String cur = iter.next();
			if (cur.startsWith("-")) {
				switch (cur.charAt(1)) {
				case 'm':
					if (iter.hasNext()) {
						String next = iter.next();
						try {
							File f = new File(next);
							Mod mod;
							if (f.isDirectory()) {
								mod = getModManager().addMod(f.toURI().toURL(), new File(f, "/bin/").toURI().toURL());
							} else {
								mod = getModManager().addMod(f.toURI().toURL());
							}
							mod.setActive(true);
						} catch (Exception e) {
							exception(e, "Mod " + next + " could not be loaded");
						}
					}
					break;
				case 'l':
					if (iter.hasNext()) {
						String next = iter.next();
						try {
							getWorldManager().load(new File(next));
						} catch (Exception e) {
							exception(e, "Level " + next + " could not be loaded");
						}
					}
					break;
				case 's':
					if (iter.hasNext()) {
						String next = iter.next();
						try {
							properties = new ExtendedProperties(next, properties);
						} catch (Exception e) {
							exception(e, "Propeties " + next + " could not be loaded");
						}
					}
					break;
				}
			}
		}
	}

	/**
	 * Creates non-existing resource declared via <code>create.folders</code> or <code>create.files</code> in the Properties and creates the three Managers
	 */
	public void create() {
		try {
			createResources();
		} catch (IOException e) {
			exception(e);
		}
		worldManager.create();
		guiManager.create();
		modManager.create();
	}

	/**
	 * Reads the content of the Properties <code>create.folders</code> and <code>create.files</code> and creates these Directories and Files.
	 * 
	 * @throws IOException
	 *             If a File couldn't be created
	 */
	public void createResources() throws IOException {
		for (String s : getProperties().getPropertyList("create.folders")) {
			if (s != null && !s.isEmpty()) {
				File f = new File(s);
				if (!f.exists()) {
					f.mkdirs();
					System.out.println("Created " + s);
					if (!f.exists()) {
						throw new FileNotFoundException(f.toString() + " couldn't be created.");
					}
				}
			}
		}
		for (String s : getProperties().getPropertyList("create.files")) {
			if (s != null && !s.isEmpty()) {
				File f = new File(s);
				if (!f.exists()) {
					f.createNewFile();
					System.out.println("Created " + s);
					if (!f.exists()) {
						throw new FileNotFoundException(f.toString() + " couldn't be created.");
					}
				}
			}
		}
	}

	/**
	 * @see {@link Manager#worldManager }
	 */
	public WorldManager getWorldManager() {
		return worldManager;
	}

	/**
	 * Convenience function
	 * 
	 * @return getWorldManager().getWorld()
	 * @see {@link WorldManager#getWorld() }
	 */
	public World getWorld() {
		return getWorldManager().getWorld();
	}

	/**
	 * @see {@link Manager#guiManager }
	 */
	public GUIManager getGuiManager() {
		return guiManager;
	}

	/**
	 * @see {@link Manager#modManager }
	 */
	public ModManager getModManager() {
		return modManager;
	}

	/**
	 * Instance of the applications' properties.
	 * This points to the most recent instance, which receives all changes, but loads defaults from previous versions.
	 * 
	 * @return Instance of the applications' properties.
	 * @see {@link Manager#properties }
	 */
	public ExtendedProperties getProperties() {
		return properties;
	}

	/**
	 * Notify all ExceptionListeners of this exception with an additional message.
	 * 
	 * @param exception
	 * @param message
	 */
	public void exception(Exception exception, String message) {
		for (ExceptionListener l : exceptionListeners) {
			l.exception(exception, message);
		}
	}

	/**
	 * Notify all ExceptionListeners of this exception with an the message <i>"A " + exception.getClass().getSimpleName() + " occured!"</i>.
	 * 
	 * @param exception
	 */
	public void exception(Exception exception) {
		exception(exception, "A " + exception.getClass().getSimpleName() + " occured!");
	}

	/**
	 * Send this message to all MessageListeners.
	 * 
	 * @param message
	 * @param title
	 */
	public void message(String message, String title) {
		for (MessageListener l : messageListeners) {
			l.message(message, title);
		}
	}

	/**
	 * Send this message to all MessageListeners with the default title <i>"Message"</i>
	 * 
	 * @param message
	 */
	public void message(String message) {
		message(message, "Message");
	}

	public boolean addExceptionListener(ExceptionListener l) {
		return exceptionListeners.add(l);
	}

	public boolean removeExceptionListener(Object l) {
		return exceptionListeners.remove(l);
	}

	public boolean addMessageListener(MessageListener l) {
		return messageListeners.add(l);
	}

	public boolean removeMessageListener(Object l) {
		return messageListeners.remove(l);
	}

	/**
	 * @return The default ExcpetionListener, which which simply prints messages to stderr.
	 */
	public ExceptionListener getDefaultExceptionListener() {
		return defaultExceptionListener;
	}

	/**
	 * @return The default MessageListener, which which simply prints messages to stdout.
	 */
	public MessageListener getDefaultMessageListener() {
		return defaultMessageListener;
	}

	/**
	 * Updates the XStream's ClassLoader to respect all loaded mods.
	 */
	public void updateXStream() {
		xStream.setClassLoader(new URLClassLoader(getModManager().getActiveModURLs(), getClass().getClassLoader()));
	}

	/**
	 * Updates the XStream's ClassLoader to respect the selected URL.
	 */
	public void updateXStream(URL... urls) {
		xStream.setClassLoader(new URLClassLoader(urls, getClass().getClassLoader()));
	}

	/**
	 * Initializes the XSTREAM with some default aliases and converters
	 */
	public void initXStream() {
		xStream = new XStream(new DomDriver());
		xStream.alias("Key", Key.class);
		xStream.alias("Element", Element.class);
		xStream.alias("World", World.class);
		xStream.alias("Action", Action.class);
		xStream.alias("SelectAction", SelectAction.class);
		xStream.alias("Manifest", ModManifest.class);
		xStream.alias("Mod", Mod.class);
		xStream.alias("SimpleMod", SimpleMod.class);
		xStream.aliasPackage("element", "de.ncoder.elements.element");

		xStream.useAttributeFor(Key.class, "character");
		xStream.useAttributeFor(Key.class, "ctrlDown");
		xStream.useAttributeFor(Key.class, "shiftDown");
		xStream.useAttributeFor(Key.class, "altDown");
		xStream.useAttributeFor(Action.class, "type");
		xStream.useAttributeFor(SelectAction.class, "additional");
		xStream.useAttributeFor(Element.class, "fluidity");
		xStream.useAttributeFor(Element.class, "gravity");
		xStream.useAttributeFor(Element.class, "color");
		xStream.useAttributeFor(World.class, "background");

		xStream.useAttributeFor(Boolean.class);
		xStream.useAttributeFor(Integer.class);
		xStream.useAttributeFor(Double.class);
		xStream.useAttributeFor(Character.class);
		xStream.useAttributeFor(Class.class);
		xStream.useAttributeFor(Color.class);
		xStream.useAttributeFor(String.class);

		xStream.registerConverter(new ColorConverter());
		xStream.registerLocalConverter(ModManifest.class, "modAdditional", new StringMapConverter<Object>(xStream.getMapper(), "key"));
	}

	public XStream getXStream() {
		return xStream;
	}

	public void setXStream(XStream xStream) {
		this.xStream = xStream;
	}

	public interface ExceptionListener {
		public void exception(Exception exception, String message);
	}

	public interface MessageListener {
		public void message(String message, String title);
	}
}

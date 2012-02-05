package de.ncoder.elements.gui.frame;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import de.ncoder.elements.Manager;
import de.ncoder.elements.element.Delete;
import de.ncoder.elements.element.Spawner;
import de.ncoder.elements.engine.Element;
import de.ncoder.elements.gui.ElementsCanvasFrame;
import de.ncoder.elements.gui.GUIManager.Lock;

public class CanvasFrame extends ElementsCanvasFrame implements MouseListener, MouseMotionListener, KeyListener {
	private static final long serialVersionUID = 1L;

	public CanvasFrame(Manager manager) {
		super(manager);
	}

	public void create() {
		setInnerSize(300, 300);

		manager.getWorldManager().getTimer().addListener(new Runnable() {
			@Override
			public void run() {
				repaint();
			}
		});
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				limitSize();
			}
		});
		manager.getGuiManager().addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	public void paint(Graphics2D g) {
		g.translate(-manager.getGuiManager().getCanvasOffsetX() * manager.getGuiManager().getCanvasElementWidth(), -manager.getGuiManager().getCanvasOffsetY() * manager.getGuiManager().getCanvasElementHeight());

		float alpha = 1;
		if (manager.getGuiManager().getCanvasTraces() == 1) {
			alpha = 0.7f;
		} else if (manager.getGuiManager().getCanvasTraces() == 2) {
			alpha = 0.5f;
		}
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		manager.getWorld().paintBackground((Graphics2D) g, manager.getGuiManager().getCanvasElementWidth(), manager.getGuiManager().getCanvasElementHeight());
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
		manager.getWorld().paint((Graphics2D) g, manager.getGuiManager().getCanvasElementWidth(), manager.getGuiManager().getCanvasElementHeight());

		if (manager.getGuiManager().getCanvasMousePosition() != null) {

			if (manager.getGuiManager().getSelected() == null) {
				manager.getGuiManager().setSelected(Delete.class);
			}

			try {
				Element selectedInstance;
				if (manager.getGuiManager().getSelected() != null) {
					selectedInstance = manager.getGuiManager().getSelected().newInstance();
				} else {
					selectedInstance = new Delete();
				}
				Point min = manager.getGuiManager().getBrushPointMin();
				Point max = manager.getGuiManager().getBrushPointMax();
				for (int x = min.x; x <= max.x; x++) {
					for (int y = min.y; y <= max.y; y++) {
						if (manager.getWorld().isInBounds(x, y)) {
							g.translate(x * manager.getGuiManager().getCanvasElementWidth(), y * manager.getGuiManager().getCanvasElementHeight());
							selectedInstance.paintCursor(g, manager.getWorld().getElementAt(x, y), manager.getGuiManager().getCanvasElementWidth(), manager.getGuiManager().getCanvasElementHeight());
							g.translate(-x * manager.getGuiManager().getCanvasElementWidth(), -y * manager.getGuiManager().getCanvasElementHeight());
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		limitSize();
	}

	public Point toPoint(Point coordinate) {
		Insets i = getInsets();
		int x = (int) Math.round((coordinate.getX() - i.left) / (double) manager.getGuiManager().getCanvasElementWidth()) + manager.getGuiManager().getCanvasOffsetX();
		int y = (int) Math.round((coordinate.getY() - i.top) / (double) manager.getGuiManager().getCanvasElementHeight()) + manager.getGuiManager().getCanvasOffsetY();
		x = Math.max(0, Math.min(manager.getWorld().getElementsXCount() - 1, x));
		y = Math.max(0, Math.min(manager.getWorld().getElementsYCount() - 1, y));
		return new Point(x, y);
	}

	public void mouseCreate(MouseEvent e) {
		mousePreview(e);

		Point min = manager.getGuiManager().getBrushPointMin();
		Point max = manager.getGuiManager().getBrushPointMax();
		for (int x = min.x; x <= max.x; x++) {
			for (int y = min.y; y <= max.y; y++) {
				if (manager.getWorld().isInBounds(x, y)) {
					if (manager.getGuiManager().getSelected() == null || manager.getGuiManager().getSelected().isAssignableFrom(Delete.class)) {
						manager.getWorld().setElementAt(x, y, null);
					} else if (manager.getWorld().getElementAt(x, y) instanceof Spawner && !manager.getGuiManager().getSelected().isAssignableFrom(Spawner.class)) {
						((Spawner) manager.getWorld().getElementAt(x, y)).setSpawnType(manager.getGuiManager().getSelected());
					} else {
						try {
							manager.getWorld().setElementAt(x, y, manager.getGuiManager().getSelected().newInstance());
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		}

		repaint();
	}

	public void mousePreview(MouseEvent e) {
		manager.getGuiManager().setCanvasMousePosition(toPoint(e.getPoint()));
		if (manager.getGuiManager().getCanvasMouseLock() == Lock.NONE) {
			manager.getGuiManager().setCanvasMouseLock(Lock.SET);
		} else if (e.isControlDown()) {
			switch (manager.getGuiManager().getCanvasMouseLock()) {
			case SET:
				if (!manager.getGuiManager().getCanvasMousePosition().equals(manager.getGuiManager().getCanvasLastMousePosition())) {
					if (manager.getGuiManager().getCanvasMousePosition().x == manager.getGuiManager().getCanvasLastMousePosition().x) {
						manager.getGuiManager().setCanvasMouseLock(Lock.VERTICAL);
					} else if (manager.getGuiManager().getCanvasMousePosition().y == manager.getGuiManager().getCanvasLastMousePosition().y) {
						manager.getGuiManager().setCanvasMouseLock(Lock.HORIZONTAL);
					}
				}
				break;
			case VERTICAL:
				manager.getGuiManager().getCanvasMousePosition().x = manager.getGuiManager().getCanvasLastMousePosition().x;
				break;
			case HORIZONTAL:
				manager.getGuiManager().getCanvasMousePosition().y = manager.getGuiManager().getCanvasLastMousePosition().y;
				break;
			}
		} else if (e.isShiftDown()) {
			switch (manager.getGuiManager().getCanvasMouseLock()) {
			case SET:
				if (!manager.getGuiManager().getCanvasMousePosition().equals(manager.getGuiManager().getCanvasLastMousePosition())) {
					if ((manager.getGuiManager().getCanvasMousePosition().x > manager.getGuiManager().getCanvasLastMousePosition().x && manager.getGuiManager().getCanvasMousePosition().y > manager.getGuiManager().getCanvasLastMousePosition().y) || (manager.getGuiManager().getCanvasMousePosition().x < manager.getGuiManager().getCanvasLastMousePosition().x && manager.getGuiManager().getCanvasMousePosition().y < manager.getGuiManager().getCanvasLastMousePosition().y)) {
						manager.getGuiManager().setCanvasMouseLock(Lock.DOWN);
					} else if ((manager.getGuiManager().getCanvasMousePosition().x > manager.getGuiManager().getCanvasLastMousePosition().x && manager.getGuiManager().getCanvasMousePosition().y < manager.getGuiManager().getCanvasLastMousePosition().y) || (manager.getGuiManager().getCanvasMousePosition().x < manager.getGuiManager().getCanvasLastMousePosition().x && manager.getGuiManager().getCanvasMousePosition().y > manager.getGuiManager().getCanvasLastMousePosition().y)) {
						manager.getGuiManager().setCanvasMouseLock(Lock.UP);
					}
				}
				break;
			case DOWN:
				manager.getGuiManager().getCanvasMousePosition().y = manager.getGuiManager().getCanvasLastMousePosition().y + (manager.getGuiManager().getCanvasMousePosition().x - manager.getGuiManager().getCanvasLastMousePosition().x);
				break;
			case UP:
				manager.getGuiManager().getCanvasMousePosition().y = manager.getGuiManager().getCanvasLastMousePosition().y - (manager.getGuiManager().getCanvasMousePosition().x - manager.getGuiManager().getCanvasLastMousePosition().x);
				break;
			}
		} else {
			manager.getGuiManager().setCanvasMouseLock(Lock.NONE);
		}
		repaint();
	}

	public void limitSize() {
		Insets i = getInsets();
		int insetsW = i.left + i.right;
		int insetsH = i.top + i.bottom;
		int maxW = manager.getWorld().getElementsXCount() * manager.getGuiManager().getCanvasElementWidth();
		int maxH = manager.getWorld().getElementsYCount() * manager.getGuiManager().getCanvasElementHeight();
		int width = Math.round((getWidth() - insetsW) / 5) * 5 + insetsW;
		int height = Math.round((getHeight() - insetsH) / 5) * 5 + insetsH;
		if (width - insetsW > maxW) {
			width = maxW + insetsW;
			manager.getGuiManager().setCanvasOffsetX(0);
		} else if (width - insetsW + manager.getGuiManager().getCanvasOffsetX() * manager.getGuiManager().getCanvasElementWidth() > maxW) {
			manager.getGuiManager().setCanvasOffsetX((maxW - width + insetsW) / manager.getGuiManager().getCanvasElementWidth());
		}
		if (height - insetsH > maxH) {
			height = maxH + insetsH;
			manager.getGuiManager().setCanvasOffsetY(0);
		} else if (height - insetsH + manager.getGuiManager().getCanvasOffsetY() * manager.getGuiManager().getCanvasElementHeight() > maxH) {
			manager.getGuiManager().setCanvasOffsetY((maxH - height + insetsH) / manager.getGuiManager().getCanvasElementHeight());
		}
		setSize(width, height);
		setMaximumSize(new Dimension(maxW + insetsW, height = maxH + insetsH));
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			manager.getGuiManager().setCanvasOffsetX(manager.getGuiManager().getCanvasOffsetX() - manager.getGuiManager().getCanvasSpeedX());
			break;
		case KeyEvent.VK_RIGHT:
			manager.getGuiManager().setCanvasOffsetX(manager.getGuiManager().getCanvasOffsetX() + manager.getGuiManager().getCanvasSpeedX());
			break;
		case KeyEvent.VK_UP:
			manager.getGuiManager().setCanvasOffsetY(manager.getGuiManager().getCanvasOffsetY() - manager.getGuiManager().getCanvasSpeedY());
			break;
		case KeyEvent.VK_DOWN:
			manager.getGuiManager().setCanvasOffsetY(manager.getGuiManager().getCanvasOffsetY() + manager.getGuiManager().getCanvasSpeedY());
			break;
		case KeyEvent.VK_F12:
			System.out.println(manager.getWorld().getElementAt(manager.getGuiManager().getCanvasMousePosition().x, manager.getGuiManager().getCanvasMousePosition().y));
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseCreate(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mousePreview(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		mousePreview(e);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		mousePreview(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mouseCreate(e);
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}
}

package de.ncoder.elements.gui.frame;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import de.ncoder.elements.Manager;
import de.ncoder.elements.gui.ElementsCanvasFrame;

public class MapFrame extends ElementsCanvasFrame implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;
	private Point mousePosition;
	private Point offsetStart;

	public MapFrame(Manager manager) {
		super(manager);
	}

	public void create() {
		super.create();
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
				resize();
			}
		});
		addMouseListener(this);
		addMouseMotionListener(this);
		setResizable(false);
		setLocationRelativeTo(manager.getGuiManager().getFrame(CanvasFrame.class));
		setLocation(manager.getGuiManager().getFrame(CanvasFrame.class).getWidth(), manager.getGuiManager().getFrame(MenuFrame.class).getHeight());
	}

	public void paint(Graphics2D g) {
		resize();

		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getInnerWidth(), getInnerHeight());

		int canvasWidth = (int) (manager.getGuiManager().getFrame(CanvasFrame.class).getInnerWidth() * (manager.getGuiManager().getMapElementWidth() / (double) manager.getGuiManager().getCanvasElementWidth()));
		int canvasHeight = (int) (manager.getGuiManager().getFrame(CanvasFrame.class).getInnerHeight() * (manager.getGuiManager().getMapElementHeight() / (double) manager.getGuiManager().getCanvasElementHeight()));
		int mapWidth = manager.getGuiManager().getMapElementWidth() * manager.getWorld().getElementsXCount();
		int mapHeight = manager.getGuiManager().getMapElementHeight() * manager.getWorld().getElementsYCount();

		manager.getWorld().paintBackground((Graphics2D) g, manager.getGuiManager().getMapElementWidth(), manager.getGuiManager().getMapElementHeight());
		manager.getWorld().paint((Graphics2D) g, manager.getGuiManager().getMapElementWidth(), manager.getGuiManager().getMapElementHeight());

		g.setColor(manager.getGuiManager().getMapFrameColor());
		g.drawRect(manager.getGuiManager().getCanvasOffsetX(), manager.getGuiManager().getCanvasOffsetY(), canvasWidth - 1, canvasHeight - 1);
		g.setColor(Color.DARK_GRAY);
		g.drawRect(-1, -1, mapWidth + 2, mapHeight + 2);
	}

	public void resize() {
		int width = manager.getGuiManager().getMapElementWidth() * manager.getWorld().getElementsXCount();
		int height = manager.getGuiManager().getMapElementHeight() * manager.getWorld().getElementsYCount();

		setInnerSize(width, height);
	}

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
	}

	public Point getMousePosition() {
		return mousePosition;
	}

	public void setMousePosition(Point mousePosition) {
		this.mousePosition = mousePosition;
		offsetStart = new Point(manager.getGuiManager().getCanvasOffsetX(), manager.getGuiManager().getCanvasOffsetY());
	}

	public void setMousePosition(MouseEvent e) {
		setMousePosition(e.getPoint());
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		manager.getGuiManager().setCanvasOffsetX(e.getX() - mousePosition.x + offsetStart.x);
		manager.getGuiManager().setCanvasOffsetY(e.getY() - mousePosition.y + offsetStart.y);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		setMousePosition(e);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		setMousePosition(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		setMousePosition(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		setMousePosition(e);
	}
}

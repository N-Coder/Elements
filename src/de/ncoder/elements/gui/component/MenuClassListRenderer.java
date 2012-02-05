package de.ncoder.elements.gui.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;

import de.ncoder.elements.Manager;
import de.ncoder.elements.element.Delete;
import de.ncoder.elements.engine.Element;
import de.ncoder.elements.gui.action.SelectAction;

public class MenuClassListRenderer implements ListCellRenderer<SelectAction> {
	public Manager manager;

	public MenuClassListRenderer(Manager manager) {
		this.manager = manager;
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends SelectAction> list, SelectAction value, int index, boolean isSelected, boolean hasFocus) {
		JPanel panel = new JPanel();

		BufferedImage img = new BufferedImage(manager.getGuiManager().getPreviewElementWidth(), manager.getGuiManager().getPreviewElementHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) img.createGraphics();

		JLabel name = new JLabel("Unbennant");
		JLabel keycode = new JLabel();
		JLabel preview = new JLabel();

		keycode.setForeground(Color.GRAY);
		keycode.setAlignmentX(Component.RIGHT_ALIGNMENT);
		keycode.setHorizontalAlignment(JLabel.RIGHT);

		Border border = BorderFactory.createEmptyBorder(3, 3, 3, 3);
		if (isSelected) {
			border = BorderFactory.createLineBorder(Color.ORANGE, 3);
		}

		panel.setLayout(new BorderLayout(3, 3));
		panel.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(3, 3, 3, 3)));
		panel.add(name, BorderLayout.CENTER);
		panel.add(keycode, BorderLayout.EAST);
		panel.add(preview, BorderLayout.WEST);

		if (value.additional == null || value.additional == Delete.class) {
			name.setText("Delete");
			keycode.setText("SPACE");
			preview.setIcon(new ImageIcon(img));
		} else {
			try {
				Element instance = value.additional.newInstance();
				instance.paintPreview(g, manager.getGuiManager().getPreviewElementWidth(), manager.getGuiManager().getPreviewElementHeight());
				name.setText(instance.getName());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (value.getKey() != null) {
				keycode.setText(value.getKey().toString());
			}
			preview.setIcon(new ImageIcon(img));
		}

		return panel;
	}
}

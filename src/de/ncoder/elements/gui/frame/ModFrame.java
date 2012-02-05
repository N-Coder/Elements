package de.ncoder.elements.gui.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.ncoder.elements.Manager;
import de.ncoder.elements.mod.Mod;

public class ModFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private JList<Mod> list;
	private JButton btnInfo;
	private JButton btnToggle;
	private JButton btnDelete;
	private JButton btnAdd;
	private JButton btnOk;
	private JButton btnDispose;
	private Manager manager;

	public ModFrame(Manager manager) {
		this.manager = manager;
	}

	public void create() {
		createLayout();
	}

	public void createLayout() {
		getContentPane().setLayout(new BorderLayout(0, 0));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		list = new JList<Mod>();
		list.setModel(manager.getModManager().getMods());
		list.setCellRenderer(new ModListRenderer());
		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event) {
				if (list.getModel().getSize() > 0) {
					if (event.getClickCount() >= 2) {
						int index = list.locationToIndex(event.getPoint());
						if (index >= 0 && index < list.getModel().getSize()) {
							Mod item = (Mod) list.getModel().getElementAt(index);
							item.setActive(!item.isActive());
							manager.getModManager().getMods().elementChanged(index);
						}
					}
				}
			}
		});
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent event) {
				if (list.getModel().getSize() > 0 && !list.isSelectionEmpty()) {
					btnInfo.setEnabled(true);
					btnToggle.setEnabled(true);
					btnDelete.setEnabled(true);
				} else {
					btnInfo.setEnabled(false);
					btnToggle.setEnabled(false);
					btnDelete.setEnabled(false);
				}
			}
		});
		list.setOpaque(false);
		getContentPane().add(new JScrollPane(list), BorderLayout.CENTER);

		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(panel, BorderLayout.EAST);
		GridBagLayout gbl_panel = new GridBagLayout();
		panel.setLayout(gbl_panel);

		btnInfo = new JButton("Info");
		GridBagConstraints gbc_btnInfo = new GridBagConstraints();
		gbc_btnInfo.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnInfo.insets = new Insets(0, 0, 5, 0);
		gbc_btnInfo.gridx = 0;
		gbc_btnInfo.gridy = GridBagConstraints.RELATIVE;
		btnInfo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (list.getModel().getSize() > 0 && !list.isSelectionEmpty()) {
					list.getSelectedValue().getInfoFrame().setVisible(true);
				}
			}
		});
		btnInfo.setEnabled(false);
		panel.add(btnInfo, gbc_btnInfo);

		btnToggle = new JButton("Toggle");
		GridBagConstraints gbc_btnToggle = new GridBagConstraints();
		gbc_btnToggle.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnToggle.insets = new Insets(0, 0, 5, 0);
		gbc_btnToggle.gridx = 0;
		gbc_btnToggle.gridy = GridBagConstraints.RELATIVE;
		btnToggle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (list.getModel().getSize() > 0 && !list.isSelectionEmpty()) {
					manager.getModManager().getMods().elementChanged(list.getSelectedIndex());
					list.getSelectedValue().setActive(!list.getSelectedValue().isActive());
				}
			}
		});
		btnToggle.setEnabled(false);
		panel.add(btnToggle, gbc_btnToggle);

		btnDelete = new JButton("Delete");
		GridBagConstraints gbc_btnDelete = new GridBagConstraints();
		gbc_btnDelete.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnDelete.insets = new Insets(0, 0, 5, 0);
		gbc_btnDelete.gridx = 0;
		gbc_btnDelete.gridy = GridBagConstraints.RELATIVE;
		btnDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (list.getModel().getSize() > 0 && !list.isSelectionEmpty()) {
					Mod mod = list.getSelectedValue();
					list.clearSelection();
					manager.getModManager().getMods().remove(mod);
				}
			}
		});
		btnDelete.setEnabled(false);
		panel.add(btnDelete, gbc_btnDelete);

		Component verticalGlue = Box.createVerticalGlue();
		GridBagConstraints gbc_verticalGlue = new GridBagConstraints();
		gbc_verticalGlue.weighty = 1.0;
		gbc_verticalGlue.fill = GridBagConstraints.BOTH;
		gbc_verticalGlue.gridx = 0;
		gbc_verticalGlue.gridy = GridBagConstraints.RELATIVE;
		panel.add(verticalGlue, gbc_verticalGlue);

		btnAdd = new JButton("Add");
		GridBagConstraints gbc_btnAdd = new GridBagConstraints();
		gbc_btnAdd.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnAdd.insets = new Insets(0, 0, 5, 0);
		gbc_btnAdd.gridx = 0;
		gbc_btnAdd.gridy = GridBagConstraints.RELATIVE;
		btnAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int result = manager.getGuiManager().getFileChooser().showOpenDialog(manager.getGuiManager().getParentFrame());
				if (result == JFileChooser.APPROVE_OPTION) {
					File modFile = manager.getGuiManager().getFileChooser().getSelectedFile();
					try {
						manager.getModManager().addMod(modFile.toURI().toURL());
					} catch (Exception e1) {
						manager.exception(e1, "Mod couldn't be loaded");
					}
				}
			}
		});
		panel.add(btnAdd, gbc_btnAdd);

		btnOk = new JButton("Ok");
		GridBagConstraints gbc_btnOk = new GridBagConstraints();
		gbc_btnOk.insets = new Insets(0, 0, 5, 0);
		gbc_btnOk.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnOk.gridx = 0;
		gbc_btnOk.gridy = GridBagConstraints.RELATIVE;
		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		panel.add(btnOk, gbc_btnOk);

		btnDispose = new JButton("Cancel");
		GridBagConstraints gbc_btnDispose = new GridBagConstraints();
		gbc_btnDispose.insets = new Insets(0, 0, 5, 0);
		gbc_btnDispose.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnDispose.gridx = 0;
		gbc_btnDispose.gridy = GridBagConstraints.RELATIVE;
		btnDispose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		panel.add(btnDispose, gbc_btnDispose);

		setSize(400, 300);
	}

	public void refresh() {

	}

	public void save() {
		try {
			manager.getModManager().saveMods();
			dispose();
		} catch (IOException e1) {
			manager.exception(e1);
		}
	}

	public class ModListRenderer implements ListCellRenderer<Mod> {
		@Override
		public Component getListCellRendererComponent(JList<? extends Mod> list, Mod value, int index, boolean isSelected, boolean hasFocus) {
			JCheckBox checkbox = new JCheckBox(value.getModManifest().getModName());
			checkbox.setSelected(value.isActive());
			if (isSelected) {
				checkbox.setBackground(Color.ORANGE);
			}
			return checkbox;
		}
	}
}

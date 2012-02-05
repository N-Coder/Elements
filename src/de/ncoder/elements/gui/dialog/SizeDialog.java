package de.ncoder.elements.gui.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import de.ncoder.elements.Manager;

public class SizeDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private JSpinner ySize;
	private JSpinner xSize;
	private JPanel panel;
	private JButton btnOk;
	private JButton btnCancel;
	private JLabel lblCurrent;
	private JLabel currentX;
	private JLabel currentY;
	private JLabel lblX_1;

	private boolean send;
	private Manager manager;

	public SizeDialog(Window owner, Manager manager) {
		super(owner, "Resize", Dialog.ModalityType.APPLICATION_MODAL);
		this.manager = manager;
		create();
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				returnResult(false);
			}
		});
	}

	public void create() {
		JPanel pane = new JPanel();
		setContentPane(pane);
		pane.setBorder(new EmptyBorder(10, 10, 10, 10));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 200, 7, 200, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);

		JLabel lblSize = new JLabel("Please enter the size for the new Canvas in Elements");
		GridBagConstraints gbc_lblSize = new GridBagConstraints();
		gbc_lblSize.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblSize.insets = new Insets(0, 0, 5, 0);
		gbc_lblSize.gridwidth = 3;
		gbc_lblSize.gridx = 0;
		gbc_lblSize.gridy = 0;
		getContentPane().add(lblSize, gbc_lblSize);

		xSize = new JSpinner();
		xSize.setModel(new SpinnerNumberModel(50, 0, 1000, 1));
		GridBagConstraints gbc_xSize = new GridBagConstraints();
		gbc_xSize.anchor = GridBagConstraints.NORTH;
		gbc_xSize.fill = GridBagConstraints.HORIZONTAL;
		gbc_xSize.insets = new Insets(0, 0, 5, 5);
		gbc_xSize.gridx = 0;
		gbc_xSize.gridy = 1;
		getContentPane().add(xSize, gbc_xSize);

		JLabel lblX = new JLabel("x");
		GridBagConstraints gbc_lblX = new GridBagConstraints();
		gbc_lblX.anchor = GridBagConstraints.EAST;
		gbc_lblX.insets = new Insets(0, 0, 5, 5);
		gbc_lblX.gridx = 1;
		gbc_lblX.gridy = 1;
		getContentPane().add(lblX, gbc_lblX);

		ySize = new JSpinner();
		ySize.setModel(new SpinnerNumberModel(50, 0, 1000, 1));
		GridBagConstraints gbc_ySize = new GridBagConstraints();
		gbc_ySize.anchor = GridBagConstraints.NORTH;
		gbc_ySize.fill = GridBagConstraints.HORIZONTAL;
		gbc_ySize.insets = new Insets(0, 0, 5, 0);
		gbc_ySize.gridx = 2;
		gbc_ySize.gridy = 1;
		getContentPane().add(ySize, gbc_ySize);

		lblCurrent = new JLabel("Current");
		lblCurrent.setForeground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_lblCurrent = new GridBagConstraints();
		gbc_lblCurrent.anchor = GridBagConstraints.NORTH;
		gbc_lblCurrent.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblCurrent.insets = new Insets(0, 0, 5, 5);
		gbc_lblCurrent.gridx = 0;
		gbc_lblCurrent.gridy = 2;
		getContentPane().add(lblCurrent, gbc_lblCurrent);

		currentX = new JLabel("currentX");
		currentX.setAlignmentX(Component.RIGHT_ALIGNMENT);
		currentX.setForeground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_currentX = new GridBagConstraints();
		gbc_currentX.anchor = GridBagConstraints.NORTHEAST;
		gbc_currentX.insets = new Insets(0, 0, 5, 5);
		gbc_currentX.gridx = 0;
		gbc_currentX.gridy = 3;
		getContentPane().add(currentX, gbc_currentX);

		lblX_1 = new JLabel("x");
		lblX_1.setForeground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_lblX_1 = new GridBagConstraints();
		gbc_lblX_1.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblX_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblX_1.gridx = 1;
		gbc_lblX_1.gridy = 3;
		getContentPane().add(lblX_1, gbc_lblX_1);

		currentY = new JLabel("currentY");
		currentY.setForeground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_currentY = new GridBagConstraints();
		gbc_currentY.anchor = GridBagConstraints.NORTHEAST;
		gbc_currentY.insets = new Insets(0, 0, 5, 0);
		gbc_currentY.gridx = 2;
		gbc_currentY.gridy = 3;
		getContentPane().add(currentY, gbc_currentY);

		panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridwidth = 3;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 5;
		getContentPane().add(panel, gbc_panel);

		btnOk = new JButton("Ok");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				returnResult(true);
			}
		});
		panel.add(btnOk);

		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				returnResult(false);
			}
		});
		panel.add(btnCancel);

		pack();
	}

	private void returnResult(boolean send) {
		this.send = send;
		setVisible(false);
	}

	@Override
	public void setVisible(boolean b) {
		if (b) {
			xSize.setValue(manager.getWorld().getElementsXCount());
			ySize.setValue(manager.getWorld().getElementsYCount());
			currentX.setText(manager.getWorld().getElementsXCount() + "");
			currentY.setText(manager.getWorld().getElementsYCount() + "");
		}
		super.setVisible(b);
	}

	public Dimension getNewSize() {
		if (send) {
			return new Dimension((Integer) xSize.getValue(), (Integer) ySize.getValue());
		} else {
			return null;
		}
	}
}

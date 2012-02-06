package de.ncoder.elements.gui.frame;

import de.ncoder.nlib.gui.NFrame;
import javax.swing.JEditorPane;
import java.awt.BorderLayout;
import java.io.IOException;

public class InfoFrame extends NFrame {
	private static final long serialVersionUID = 1L;

	public InfoFrame() {
		super();
		JEditorPane editorPane;
		try {
			editorPane = new JEditorPane(getClass().getResource("/info.html"));
		} catch (IOException e) {
			editorPane = new JEditorPane();
			editorPane
					.setText("<html><body><h1>Info file not found!</h1></body></html>");
		}
		editorPane.setOpaque(false);
		getContentPane().add(editorPane, BorderLayout.CENTER);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

}

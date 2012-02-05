package de.ncoder.elements.gui.frame;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import de.ncoder.elements.mod.ModManifest;

public class ManifestFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	public ManifestFrame(ModManifest manifest) {
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		JLabel label = new JLabel(createText(manifest));
		label.setVerticalTextPosition(SwingConstants.TOP);
		label.setVerticalAlignment(SwingConstants.TOP);
		label.setHorizontalTextPosition(SwingConstants.LEFT);
		label.setAlignmentY(Component.TOP_ALIGNMENT);
		scrollPane.setViewportView(label);
		setSize(300, 300);
	}

	public String createText(ModManifest manifest) {
		StringBuilder bob = new StringBuilder();
		bob.append("<html><body>");
		bob.append("<h1>" + manifest.getModName() + " (" + manifest.getModVersionName() + ")</h1>");
		bob.append(manifest.getModDescription());
		bob.append("<br/><br/><hr/><i>" + manifest.getModClass() + ":" + manifest.getModVersion() + "</i>");
		bob.append("</body></html>");
		return bob.toString();
	}
}

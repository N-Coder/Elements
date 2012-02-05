package de.ncoder.elements.gui.action;

import de.ncoder.elements.engine.Element;
import de.ncoder.nlib.Key;

public class SelectAction extends AbstractAction<Class<? extends Element>>{
	public Class<? extends Element> additional;

	@SuppressWarnings("unused")
	private SelectAction() {
		super();
	}

	public SelectAction(Class<? extends Element> additional) {
		this(additional, null);
	}

	public SelectAction(Class<? extends Element> additional, Key key) {
		super();
		this.additional = additional;
		setKey(key);
	}

	public ActionType getType() {
		return ActionType.SELECT;
	}

	public Class<? extends Element> getAdditional() {
		return additional;
	}

	public void setAdditional(Class<? extends Element> additional) {
		this.additional = additional;
	}
}
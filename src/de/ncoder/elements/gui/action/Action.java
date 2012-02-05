package de.ncoder.elements.gui.action;

import de.ncoder.nlib.Key;

public class Action<T> extends AbstractAction<T> {
	public ActionType type;
	public T additional;

	@SuppressWarnings("unused")
	private Action() {
		super();
	}
	
	public Action(ActionType type) {
		this(type, null);
	}

	public Action(ActionType type, T additional) {
		this(type, additional, null);
	}

	public Action(ActionType type, T additional, Key key) {
		this.type = type;
		this.additional = additional;
		setKey(key);
	}

	public ActionType getType() {
		return type;
	}

	public void setType(ActionType type) {
		this.type = type;
	}

	public T getAdditional() {
		return additional;
	}

	public void setAdditional(T additional) {
		this.additional = additional;
	}
}

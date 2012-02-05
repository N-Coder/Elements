package de.ncoder.elements.gui.action;

import de.ncoder.nlib.Key;

public abstract class AbstractAction<T> {
	private Key key;

	protected AbstractAction() {}

	public abstract ActionType getType();

	public abstract T getAdditional();

	public abstract void setAdditional(T additional);

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AbstractAction)) {
			return false;
		}
		AbstractAction<?> other = (AbstractAction<?>) obj;
		return getType() == other.getType() && getAdditional() == other.getAdditional();
	}
}
package de.ncoder.elements.mod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.ncoder.elements.gui.action.SelectAction;

public class SimpleMod extends Mod {
	private List<SelectAction> actions = new ArrayList<SelectAction>();
	public static final String KEYS_ADDITIONAL_NAME = "keys";

	public SimpleMod() {
		super();
	}
	
	public SimpleMod(SelectAction... actions) {
		this();
		this.actions.addAll(Arrays.asList(actions));
	}

	public SimpleMod(List<SelectAction> actions) {
		this();
		this.actions.addAll(actions);
	}

	@SuppressWarnings("unchecked")
	public void init() {
		Object keysObject = getModManifest().getAdditional(KEYS_ADDITIONAL_NAME);
		if(keysObject!=null && keysObject instanceof List) {
			List<SelectAction> keys = (List<SelectAction>) keysObject;
			this.actions.addAll(keys);
		}
	}

	@Override
	public void activeChanged() {
		if (isActive()) {
			getManager().getGuiManager().addActions(actions);
		} else {
			getManager().getGuiManager().removeActions(actions);
		}
	}
}

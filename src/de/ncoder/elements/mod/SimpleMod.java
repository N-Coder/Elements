package de.ncoder.elements.mod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.ncoder.elements.gui.action.SelectAction;

public class SimpleMod extends Mod {
	private List<SelectAction> actions = new ArrayList<SelectAction>();
	
	public SimpleMod(SelectAction... actions) {
		super();
		this.actions.addAll(Arrays.asList(actions));
	}

	@Override
	public void activeChanged() {
		if(isActive()) {
			getManager().getGuiManager().addActions(actions);
		} else {
			getManager().getGuiManager().removeActions(actions);
		}
	}

	
}

package org.dawb.common.ui.tree;

import java.util.EventListener;

import javax.measure.quantity.Quantity;

public interface UnitListener extends EventListener {

	void unitChanged(UnitEvent<? extends Quantity> evt);
}

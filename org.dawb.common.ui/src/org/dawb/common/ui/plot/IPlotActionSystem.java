package org.dawb.common.ui.plot;

import org.dawb.common.ui.plot.tool.IToolPage.ToolPageRole;
import org.eclipse.jface.action.IContributionManager;

/**
 * Interface for giving access to filling custom actions used in the plotting.
 * 
 * The action filled will be exactly the same one as the plotting uses. Therefore
 * visible, enabled settings will take effect across all IContributionManagers
 * 
 * @author fcp94556
 *
 */
public interface IPlotActionSystem {

	/**
	 * Gets the zoom actions and fills them into this contribution manager.
	 * @param man
	 */
	public void fillZoomActions(IContributionManager man);
	

	/**
	 * Fill for region actions.
	 * @param man
	 */
	public void fillRegionActions(IContributionManager man);

	
	/**
	 * Fill with undo/redo actions
	 * @param man
	 */
	public void fillUndoActions(IContributionManager man);

	
	/**
	 * Fill with print actions.
	 * @param man
	 */
	public void fillPrintActions(IContributionManager man);
	
	/**
	 * Fill with annotation actions
	 * @param man
	 */
	public void fillAnnotationActions(IContributionManager man);
	
	/**
	 * Fill the action(s) for a given tool type
	 * @param man
	 */
	public void fillToolActions(IContributionManager man, ToolPageRole role);

}
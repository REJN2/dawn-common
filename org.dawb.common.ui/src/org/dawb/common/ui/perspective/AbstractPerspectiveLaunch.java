package org.dawb.common.ui.perspective;

import org.dawb.common.ui.ServiceLoader;
import org.eclipse.dawnsci.analysis.api.EventTracker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

/**
 * This class is used to launch a perspective given a unique String ID
 *
 */
public abstract class AbstractPerspectiveLaunch implements IWorkbenchWindowActionDelegate {

	/**
	 * Returns the perspective ID
	 * 
	 * @return ID
	 */
	public abstract String getID();

	@Override
	public void run(IAction action) {
		try {
			PlatformUI.getWorkbench().showPerspective(getID(),
					PlatformUI.getWorkbench().getActiveWorkbenchWindow());

			// track perspective launch with perspective name
			IPerspectiveRegistry reg = PlatformUI.getWorkbench()
					.getPerspectiveRegistry();
			IPerspectiveDescriptor per = reg.findPerspectiveWithId(getID());
			String perspectiveName = "NA";
			if (per != null)
				perspectiveName = per.getLabel();
			EventTracker tracker = ServiceLoader.getEventTrackerService();
			if (tracker != null)
				tracker.trackPerspectiveEvent(perspectiveName);
		} catch (WorkbenchException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}
}

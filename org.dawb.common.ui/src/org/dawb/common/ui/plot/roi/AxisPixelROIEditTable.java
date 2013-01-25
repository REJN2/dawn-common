package org.dawb.common.ui.plot.roi;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.dawb.common.ui.databinding.AbstractModelObject;
import org.dawb.common.ui.plot.AbstractPlottingSystem;
import org.dawb.common.ui.plot.trace.IImageTrace;
import org.dawb.common.ui.plot.trace.ITrace;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.databinding.viewers.ViewerSupport;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset;
import uk.ac.diamond.scisoft.analysis.roi.ROIBase;
import uk.ac.diamond.scisoft.analysis.roi.RectangularROI;
import uk.ac.gda.richbeans.components.cell.FieldComponentCellEditor;
import uk.ac.gda.richbeans.components.wrappers.FloatSpinnerWrapper;
import uk.ac.gda.richbeans.components.wrappers.SpinnerWrapper;

/**
 * Class to create a TableViewer that shows ROI information<br> 
 * both with real (axis) and pixel values.<br>
 * This table uses JFace data binding to update its content.
 * TODO make it work with all ROIs (only working for RectangularROI currently)
 * @author wqk87977
 *
 */
public class AxisPixelROIEditTable {

	private TableViewer regionViewer;

	private AxisPixelTableViewModel viewModel;

	private AbstractPlottingSystem plottingSystem;

	private boolean isProfile = false;

	private Logger logger = LoggerFactory.getLogger(AxisPixelROIEditTable.class);

	private ROIBase roi;

	private IObservableList values;

	private int precision = 5;

	/**
	 * 
	 * @param parent
	 * @param plottingSystem
	 */
	public AxisPixelROIEditTable(Composite parent, AbstractPlottingSystem plottingSystem) {
		this.plottingSystem = plottingSystem;
		this.viewModel = new AxisPixelTableViewModel();

		buildControls(parent);
	}

	protected void buildControls(Composite parent) {

		final Table table = new Table(parent, SWT.FULL_SELECTION | SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
		regionViewer = buildAndLayoutTable(table);
		
//		final Label clickToEdit = new Label(parent, SWT.WRAP);
//		clickToEdit.setText("* Click to change");
//		clickToEdit.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 2, 1));

		// Data binding
		// ViewerSupport.bind takes care of the TableViewer input, 
		// the Label and Content providers and the databinding
		ViewerSupport.bind(regionViewer, viewModel.getValues(),
				BeanProperties.values(new String[] { "name", "start", "end", "diff" }));

	}

	private TableViewer buildAndLayoutTable(final Table table) {

		TableViewer tableViewer = new TableViewer(table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, true, false, 2, 2));

		TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE, 0); 
		viewerColumn.getColumn().setText("Name");
		viewerColumn.getColumn().setWidth(80);
		RegionEditingSupport regionEditor = new RegionEditingSupport(tableViewer, 0);
		viewerColumn.setEditingSupport(regionEditor);
		
		viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE, 1); 
		viewerColumn.getColumn().setText("Start");
		viewerColumn.getColumn().setWidth(100);
		regionEditor = new RegionEditingSupport(tableViewer, 1);
		viewerColumn.setEditingSupport(regionEditor);

		viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE, 2); 
		viewerColumn.getColumn().setText("End");
		viewerColumn.getColumn().setWidth(100);
		regionEditor = new RegionEditingSupport(tableViewer, 2);
		viewerColumn.setEditingSupport(regionEditor);

		viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE, 3); 
		viewerColumn.getColumn().setText("Width(X), Height(Y)");
		viewerColumn.getColumn().setWidth(150);
		regionEditor = new RegionEditingSupport(tableViewer, 3);
		viewerColumn.setEditingSupport(regionEditor);

		return tableViewer;
	}

	/**
	 * EditingSupport Class
	 *
	 */
	private class RegionEditingSupport extends EditingSupport {

		private int column;

		public RegionEditingSupport(ColumnViewer viewer, int col) {
			super(viewer);
			this.column = col;
		}
		@Override
		protected CellEditor getCellEditor(final Object element) {
			
			FieldComponentCellEditor ed = null;
			try {
				ed = new FieldComponentCellEditor(((TableViewer)getViewer()).getTable(), 
						                     FloatSpinnerWrapper.class.getName(), SWT.RIGHT);
			} catch (ClassNotFoundException e) {
				logger.error("Cannot get FieldComponentCellEditor for "+SpinnerWrapper.class.getName(), e);
				return null;
			}
			
			final FloatSpinnerWrapper   rb = (FloatSpinnerWrapper)ed.getFieldWidget();
			if (rb.getPrecision() < 3)
				rb.setFormat(rb.getWidth(), 3);
			
			rb.setMaximum(Double.MAX_VALUE);
			rb.setMinimum(-Double.MAX_VALUE);

			rb.setButtonVisible(false);
			rb.setActive(true);
			
			((Spinner) rb.getControl())
					.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							setValue(element, rb.getValue(), false);
						}
					});	
			return ed;
		}

		@Override
		protected boolean canEdit(Object element) {
			if (column==0) return false;
			else return true;
		}

		@Override
		protected Object getValue(Object element) {
			final AxisPixelRowDataModel row = (AxisPixelRowDataModel)element;
			switch (column){
			case 0:
				return row.getName();
			case 1:
				return row.getStart();
			case 2:
				return row.getEnd();
			case 3:
				return row.getDiff();
			default:
				return null;
			}
		}

		@Override
		protected void setValue(Object element, Object value) {
			this.setValue(element, value, true);
		}
		
		//@SuppressWarnings("unchecked")
		protected void setValue(Object element, Object value, boolean tableRefresh) {

			final AxisPixelRowDataModel row = (AxisPixelRowDataModel) element;
			
			switch (column){
			case 0:
				row.setName((String)value);
				break;
			case 1:
				row.setStart((Double)value);
				break;
			case 2:
				row.setEnd((Double)value);
				// set new diff
				double diff = ((Double)row.getEnd()) - ((Double)row.getStart());
				row.setDiff(diff);
				break;
			case 3:
				row.setDiff((Double)value);
				// set new end
				double end = ((Double)row.getStart()) + ((Double)row.getDiff());
				row.setEnd(end);
				break;
			default:
				break;
			}

			if (tableRefresh) {
				getViewer().refresh();
			}

			roi = createRoi(viewModel.getValues());
			setTableValues(roi);
		}

	}

	/**
	 * Method that rounds a value to the n precision decimals
	 * @param value
	 * @param precision
	 * @return double
	 */
	private double roundDouble(double value, int precision){
		int rounder = (int)Math.pow(10, precision);
		return (double)Math.round(value * rounder) / rounder;
	}

	/**
	 * Method that creates a ROI using the input of the table viewer
	 * @param rows
	 * @return ROIBase
	 */
	private ROIBase createRoi(IObservableList rows) {
		double ptx = 0, pty = 0, width = 0, height = 0, angle = 0;
		ROIBase ret = null; 
		if (roi == null)
			roi = plottingSystem.getRegions().iterator().next().getROI();
		if (roi instanceof RectangularROI) {
			if(rows.get(2) instanceof AxisPixelRowDataModel){
				AxisPixelRowDataModel xPixelRow = (AxisPixelRowDataModel) rows.get(2);
				ptx = xPixelRow.getStart();
				width = xPixelRow.getDiff();
			}
			if(rows.get(3) instanceof AxisPixelRowDataModel){
				AxisPixelRowDataModel yPixelRow = (AxisPixelRowDataModel) rows.get(3);
				pty = yPixelRow.getStart();
				height = yPixelRow.getDiff();
			}

			RectangularROI rr = new RectangularROI(ptx, pty, width, height, angle);
			ret = rr;
		}
		return ret;
	}

	public IStructuredSelection getSelection() {
		return (IStructuredSelection) regionViewer.getSelection();
	}

	public void setSelection(IStructuredSelection selection) {
		regionViewer.setSelection(selection, true);
	}

	/**
	 * Method that returns the TableViewer
	 * @return TableViewer
	 */
	public TableViewer getTableViewer(){
		return regionViewer;
	}

	/**
	 * Method that sets the table viewer values given a Region of Interest
	 * @param region
	 */
	public void setTableValues(ROIBase region) {
		roi = region;
		values = viewModel.getValues();

		AxisPixelRowDataModel xAxisRow = (AxisPixelRowDataModel)values.get(0);
		AxisPixelRowDataModel yAxisRow = (AxisPixelRowDataModel)values.get(1);
		AxisPixelRowDataModel xPixelRow = (AxisPixelRowDataModel)values.get(2);
		AxisPixelRowDataModel yPixelRow = (AxisPixelRowDataModel)values.get(3);

		RectangularROI rroi = (RectangularROI)roi;
		double xStart = roi.getPointX();
		double yStart = roi.getPointY();
		double xEnd = rroi.getEndPoint()[0];
		double yEnd = rroi.getEndPoint()[1];

		if(!isProfile){
			try{
				// We get the axes data to convert from the axis pixel to data values
				Collection<ITrace> traces = plottingSystem.getTraces();
				Iterator<ITrace> it = traces.iterator();
				while(it.hasNext()){
					ITrace trace = it.next();
					if(trace instanceof IImageTrace){
						IImageTrace image = (IImageTrace)trace;
						List<AbstractDataset> axes = image.getAxes();
						// x axis and width
						double xAxisStart = axes.get(0).getElementDoubleAbs((int)Math.round(xStart));
						double xAxisEnd =axes.get(0).getElementDoubleAbs((int)(int)Math.round(xEnd));
						xAxisRow.setStart(roundDouble(xAxisStart, precision));
						xAxisRow.setEnd(roundDouble(xAxisEnd, precision));
						xAxisRow.setDiff(roundDouble(xAxisEnd-xAxisStart, precision));
						// yaxis and height
						double yAxisStart = axes.get(1).getElementDoubleAbs((int)Math.round(yStart));
						double yAxisEnd =axes.get(1).getElementDoubleAbs((int)(int)Math.round(yEnd));
						yAxisRow.setStart(roundDouble(yAxisStart, precision));
						yAxisRow.setEnd(roundDouble(yAxisEnd, precision));
						yAxisRow.setDiff(roundDouble(yAxisEnd-yAxisStart, precision));
					}
				}

				xPixelRow.setStart(roundDouble(xStart, precision));
				xPixelRow.setEnd(roundDouble(xEnd, precision));
				xPixelRow.setDiff(roundDouble(xEnd-xStart, precision));
				yPixelRow.setStart(roundDouble(yStart, precision));
				yPixelRow.setEnd(roundDouble(yEnd, precision));
				yPixelRow.setDiff(roundDouble(yEnd-yStart, precision));
			} catch (ArrayIndexOutOfBoundsException ae) {
				// do nothing
			} catch (Exception e) {
				logger .debug("Error while updating the ROITableInfo:"+ e);
			}
		} 
	}

	/**
	 * Methods that returns the current ROI
	 * @return ROIBase
	 */
	public ROIBase getROI(){
		return roi;
	}

	public void setValues(IObservableList values) {
		this.values = values;
	}

	/**
	 * Method to add a SelectionChangedListener to the TableViewer
	 * @param listener
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener){
		regionViewer.addSelectionChangedListener(listener);
	}

	/**
	 * Method to remove a SelectionChangedListener from the TableViewer
	 * @param listener
	 */
	public void removeSelectionChangedListener(ISelectionChangedListener listener){
		regionViewer.removeSelectionChangedListener(listener);
	}

	/**
	 * View Model of  AxisPixel Table
	 *
	 */
	private class AxisPixelTableViewModel {

		private IObservableList rows = new WritableList();

		private AxisPixelRowDataModel xAxisRow;
		private AxisPixelRowDataModel yAxisRow;
		private AxisPixelRowDataModel xPixelRow;
		private AxisPixelRowDataModel yPixelRow;

		{
			xAxisRow = new AxisPixelRowDataModel(new String("X Axis"), new Double(0), new Double(0), new Double(0));
			yAxisRow = new AxisPixelRowDataModel(new String("Y Axis"), new Double(0), new Double(0), new Double(0)); 
			xPixelRow = new AxisPixelRowDataModel(new String("X Pixel"), new Double(0), new Double(0), new Double(0)); 
			yPixelRow = new AxisPixelRowDataModel(new String("Y Pixel"), new Double(0), new Double(0), new Double(0)); 

			rows.add(xAxisRow);
			rows.add(yAxisRow);
			rows.add(xPixelRow);
			rows.add(yPixelRow);
		}

		public IObservableList getValues() {
			return rows;
		}
	}

	/**
	 * Model object for a Region Of Interest row used in an AxisPixel Table
	 * @author wqk87977
	 *
	 */
	private class AxisPixelRowDataModel extends AbstractModelObject {
		private String name;
		private double start;
		private double end;
		private double diff;

		public AxisPixelRowDataModel(String name, double start, double end, double diff) {
			this.name = name;
			this.start = start;
			this.end = end;
			this.diff = diff;
		}

		public String getName() {
			return name;
		}

		public double getStart() {
			return start;
		}

		public double getEnd() {
			return end;
		}

		public double getDiff() {
			return diff;
		}

		public void setName(String name){
			String oldValue = this.name;
			this.name = name;
			firePropertyChange("name", oldValue, this.name);
		}

		public void setStart(double start) {
			double oldValue = this.start;
			this.start = start;
			firePropertyChange("start", oldValue, this.start);
		}

		public void setEnd(double end) {
			double oldValue = this.end;
			this.end = end;
			firePropertyChange("end", oldValue, this.end);
		}

		public void setDiff(double diff) {
			double oldValue = this.diff;
			this.diff = diff;
			firePropertyChange("diff", oldValue, this.diff);
		}
	}
}
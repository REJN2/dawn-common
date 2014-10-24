/*-
 * Copyright (c) 2014 Diamond Light Source Ltd.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.dawnsci.conversion.ui.pages;

import java.io.File;

import org.dawb.common.services.conversion.IConversionContext;
import org.dawb.common.ui.wizard.ResourceChoosePage;
import org.dawb.common.util.io.FileUtils;
import org.dawnsci.conversion.converters.ImagesToStitchedConverter.ConversionStitchedBean;
import org.dawnsci.conversion.ui.IConversionWizardPage;
import org.eclipse.dawnsci.analysis.api.dataset.IDataset;
import org.eclipse.dawnsci.analysis.api.image.IImageTransform;
import org.eclipse.dawnsci.analysis.api.io.IDataHolder;
import org.eclipse.dawnsci.analysis.dataset.impl.Activator;
import org.eclipse.dawnsci.analysis.dataset.roi.EllipticalROI;
import org.eclipse.dawnsci.plotting.api.IPlottingSystem;
import org.eclipse.dawnsci.plotting.api.PlotType;
import org.eclipse.dawnsci.plotting.api.PlottingFactory;
import org.eclipse.dawnsci.plotting.api.region.IRegion;
import org.eclipse.dawnsci.plotting.api.region.IRegion.RegionType;
import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.nebula.widgets.formattedtext.NumberFormatter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.io.LoaderFactory;

/**
 * 
 * 
 * @author wqk87977
 *
 */
public class ImagesToStitchedConversionPage extends ResourceChoosePage
		implements IConversionWizardPage {

	private static final Logger logger = LoggerFactory.getLogger(ImagesToStitchedConversionPage.class);

	private IConversionContext context;
	private Spinner rowsSpinner;
	private Spinner columnsSpinner;
	private FormattedText angleText;
	private FormattedText fovText;
	private FormattedText xTranslationText;
	private FormattedText yTranslationText;
	private boolean hasCropping = true;
	private boolean hasFeatureAssociated = true;
	private ExpandableComposite plotExpandComp;
	private IPlottingSystem plotSystem;
	private Composite container;

	private IDataset firstImage;

	private IImageTransform transformer;

	public ImagesToStitchedConversionPage() {
		super("Convert image directory", null, null);
		setDirectory(false);
		setFileLabel("Stitched image output file");
		setNewFile(true);
		setOverwriteVisible(true);
		setPathEditable(true);
		setDescription("Returns a stitched image given a stack of images");
	}

	@Override
	public IConversionContext getContext() {
		if (context == null)
			return null;
		context.setOutputPath(getAbsoluteFilePath());
		final File dir = new File(getSourcePath(context)).getParentFile();
		context.setWorkSize(dir.list().length);

		final ConversionStitchedBean bean = new ConversionStitchedBean();
		bean.setRows(rowsSpinner.getSelection());
		bean.setColumns(columnsSpinner.getSelection());
		Number angle = (Number) angleText.getValue();
		bean.setAngle(angle.doubleValue());
		Number fov = (Number) fovText.getValue();
		bean.setFieldOfView(fov.doubleValue());
		if (hasCropping)
			bean.setRoi(plotSystem.getRegion("Cropping"));
		bean.setFeatureAssociated(hasFeatureAssociated);
		Number xTrans = (Number) xTranslationText.getValue();
		Number yTrans = (Number) yTranslationText.getValue();
		bean.setTranslations(new double[] { xTrans.doubleValue(),
				yTrans.doubleValue() });
		context.setUserObject(bean);

		return context;
	}

	@Override
	protected void createContentAfterFileChoose(Composite container) {
		this.container = container;
		Composite controlComp = new Composite(container, SWT.NONE);
		controlComp.setLayout(new GridLayout(8, false));
		controlComp.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 4, 1));

		final Label labelRow = new Label(controlComp, SWT.NONE);
		labelRow.setText("Rows");
		labelRow.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		rowsSpinner = new Spinner(controlComp, SWT.BORDER);
		rowsSpinner.setMinimum(1);
		rowsSpinner.setSelection(3);
		rowsSpinner.setToolTipText("Number of rows for the resulting stitched image");
		rowsSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		final Label labelAngle = new Label(controlComp, SWT.NONE);
		labelAngle.setText("Rotation angle");
		labelAngle.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		angleText = new FormattedText(controlComp, SWT.BORDER);
		NumberFormatter formatter = new NumberFormatter("-##0.0");
		formatter.setFixedLengths(false, true);
		angleText.setFormatter(formatter);
		angleText.getControl().setToolTipText("Rotation angle in degrees");
		angleText.setValue(new Double(0.0));
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false); 
		gridData.widthHint = 50;
		angleText.getControl().setLayoutData(gridData);

		final Label xTranslationLabel = new Label(controlComp, SWT.NONE);
		xTranslationLabel.setText("X translation");
		xTranslationLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		xTranslationText = new FormattedText(controlComp, SWT.BORDER);
		formatter = new NumberFormatter("-##0.0");
		formatter.setFixedLengths(false, true);
		xTranslationText.setFormatter(formatter);
		xTranslationText.getControl().setToolTipText("Expected translation in microns in the X direction");
		xTranslationText.setValue(new Double(25));
		xTranslationText.getControl().setEnabled(!hasFeatureAssociated);
		gridData = new GridData(SWT.FILL, SWT.CENTER, true, false); 
		gridData.widthHint = 30;
		xTranslationText.getControl().setLayoutData(gridData);

		new Label(controlComp, SWT.NONE);
		new Label(controlComp, SWT.NONE);

		final Label labelColumn = new Label(controlComp, SWT.NONE);
		labelColumn.setText("Columns");
		labelColumn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		columnsSpinner = new Spinner(controlComp, SWT.BORDER);
		columnsSpinner.setMinimum(1);
		columnsSpinner.setSelection(3);
		columnsSpinner.setToolTipText("Number of columns for the resulting stitched image");
		columnsSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		final Label labelFOV = new Label(controlComp, SWT.NONE);
		labelFOV.setText("Field of view");
		labelFOV.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		fovText = new FormattedText(controlComp, SWT.BORDER);
		formatter = new NumberFormatter("-##0.0");
		formatter.setFixedLengths(false, true);
		fovText.setFormatter(formatter);
		fovText.getControl().setToolTipText("Field of view in microns");
		fovText.setValue(new Double(50));
		gridData = new GridData(SWT.FILL, SWT.CENTER, true, false); 
		gridData.widthHint = 50;
		fovText.getControl().setLayoutData(gridData);

		final Label yTranslationLable = new Label(controlComp, SWT.NONE);
		yTranslationLable.setText("Y translation");
		yTranslationLable.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		yTranslationText = new FormattedText(controlComp, SWT.BORDER);
		formatter = new NumberFormatter("-##0.0");
		formatter.setFixedLengths(false, true);
		yTranslationText.setFormatter(formatter);
		yTranslationText.getControl().setToolTipText("Expected translation in microns in the y direction");
		yTranslationText.setValue(new Double(25));
		yTranslationText.getControl().setEnabled(!hasFeatureAssociated);
		gridData = new GridData(SWT.FILL, SWT.CENTER, true, false); 
		gridData.widthHint = 30;
		yTranslationText.getControl().setLayoutData(gridData);

		new Label(controlComp, SWT.NONE);
		new Label(controlComp, SWT.NONE);

		final Button croppingButton = new Button(controlComp, SWT.CHECK);
		croppingButton.setText("Crop selected images");
		croppingButton.setToolTipText("If selected, the largest rectangle area "
				+ "inside of the ellipse will be the image on which the stitching "
				+ "process will be done.");
		croppingButton.setSelection(hasCropping);
		croppingButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 4, 1));
		croppingButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				hasCropping = croppingButton.getSelection();
				plotExpandComp.setEnabled(hasCropping);
				plotExpandComp.setExpanded(hasCropping);
			}
		});
		final Button featureButton = new Button(controlComp, SWT.CHECK);
		featureButton.setText("Use feature association");
		featureButton
				.setToolTipText("If selected, automatic feature association will "
						+ "be done. If not, only the motor positions and angle of "
						+ "images will be taken into account.");
		featureButton.setSelection(hasFeatureAssociated);
		featureButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 4, 1));
		featureButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				hasFeatureAssociated = featureButton.getSelection();
				xTranslationText.getControl().setEnabled(!hasFeatureAssociated);
				yTranslationText.getControl().setEnabled(!hasFeatureAssociated);
			}
		});

		// create plot system in expandable composite
		plotExpandComp = new ExpandableComposite(container, SWT.NONE);
		plotExpandComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
		plotExpandComp.setLayout(new GridLayout(1, false));
		plotExpandComp.setText("Pre-process Plot");
		plotExpandComp.setEnabled(hasCropping);

		Composite plotComp = new Composite(plotExpandComp, SWT.NONE);
		plotComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		plotComp.setLayout(new GridLayout(1, false));

		Composite subComp = new Composite(plotComp, SWT.NONE);
		subComp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		subComp.setLayout(new GridLayout(2, false));
		
		Label description = new Label(subComp, SWT.WRAP);
		description.setText("Press 'Rotate' to select the region on the rotated image then select an "
				+ "elliptical region which will be used to generate a rectangular sub-image.");
		description.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button rotateButton = new Button(subComp, SWT.NONE);
		rotateButton.setText("Rotate");
		rotateButton.setToolTipText("Rotates the original image by n degrees");
		rotateButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				createImageTransformer();
				try {
					Object val = angleText.getValue();
					IDataset rotated = null;
					if (val instanceof Long) {
						rotated = transformer.rotate(firstImage, ((Long)val).doubleValue());
					} else if (val instanceof Double) {
						rotated = transformer.rotate(firstImage, ((Double)val).doubleValue());
					}
					plotSystem.updatePlot2D(rotated, null, null);
				} catch (Exception e1) {
					logger.error("Error rotating image:" + e1.getMessage());
				}
			}
		});
		try {
			plotSystem = PlottingFactory.createPlottingSystem();
			plotSystem.createPlotPart(plotComp, "Preprocess", null, PlotType.IMAGE, null);
			plotSystem.getPlotComposite().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			IDataHolder holder = LoaderFactory.getData(getSelectedPaths()[0]);
			firstImage = holder.getDataset(0);
			plotSystem.createPlot2D(firstImage, null, null);
			createRegion(firstImage);
		} catch (Exception e) {
			logger.error("Error creating the plotting system:" + e.getMessage());
			e.printStackTrace();
		}
		plotExpandComp.setClient(plotComp);
		plotExpandComp.addExpansionListener(createExpansionAdapter());
		plotExpandComp.setExpanded(hasCropping);
	}

	private void createRegion(IDataset data) {
		String regionName = "Cropping";
		try {
			IRegion region = plotSystem.getRegion(regionName);
			if (region == null) {
				region = plotSystem
						.createRegion(regionName, RegionType.ELLIPSE);
				double width, height;
				EllipticalROI eroi = null;
				if (data != null) {
					width = data.getShape()[0];
					height = data.getShape()[1];
					double bufferX = width / 100;
					double bufferY = height / 100;
					double centreX = width / 2;
					double centreY = height / 2;
					if (width >= height) {
						eroi = new EllipticalROI((width - bufferX) / 2,
								(height - bufferY) / 2, 0, centreX, centreY);
					} else {
						eroi = new EllipticalROI((height - bufferY) / 2,
								(width - bufferX) / 2, 0, centreX, centreY);
					}
					eroi.setName(regionName);
					eroi.setPlot(true);
					region.setROI(eroi);
					region.setUserRegion(true);
					region.setMobile(true);
					plotSystem.addRegion(region);
				} else {
					logger.error("Could not create Elliptical region");
				}
			}
		} catch (Exception e) {
			logger.error("Cannot create region for perimeter PlotView!");
		}
	}

	private void createImageTransformer() {
		if (transformer == null) {
			transformer = (IImageTransform) Activator
					.getService(IImageTransform.class);
		}
	}

	private ExpansionAdapter createExpansionAdapter() {
		return new ExpansionAdapter() {
			@Override
			public void expansionStateChanged(ExpansionEvent e) {
				container.layout();
			}
		};
	}

	@Override
	public void setContext(IConversionContext context) {
		if (context != null && context.equals(this.context))
			return;

		this.context = context;
		setErrorMessage(null);
		if (context == null) { // new context being prepared.
			setPageComplete(false);
			return;
		}

		final File dir = new File(getSourcePath(context)).getParentFile();
		setPath(FileUtils.getUnique(dir, "StitchedImage", "tif").getAbsolutePath());

	}

	@Override
	public boolean isOpen() {
		return true;
	}

	public void pathChanged() {
		final String p = getAbsoluteFilePath();
		if (p == null || p.length() == 0) {
			setErrorMessage("Please select a file to export to.");
			return;
		}
		final File path = new File(p);
		if (path.exists()) {

			if (!overwrite.getSelection()) {
				setErrorMessage("The file " + path.getName()
						+ " already exists.");
				return;
			}

			if (!path.canWrite()) {
				setErrorMessage("Please choose another location to export to; this one is read only.");
				return;
			}
		}
		setErrorMessage(null);
	}
}
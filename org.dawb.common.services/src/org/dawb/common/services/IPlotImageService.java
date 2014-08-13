/*-
 * Copyright (c) 2013 Diamond Light Source Ltd.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */ 
package org.dawb.common.services;

import java.io.File;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.services.IDisposable;

import uk.ac.diamond.scisoft.analysis.dataset.IDataset;

/**
 * A service for getting thumbnails from datasets as images and for
 * getting system file icons for File objects.
 * 
 * The implementor or this service contributes using an eclipse extension
 * point and then later any plugin may ask for an implementation of the service.
 * 
 * @author fcp94556
 */
public interface IPlotImageService extends IFileIconService{

	/**
	 * Create a square image from a specified file, f of given side size, size in pixels.
	 * @param f
	 * @param size
	 * @return
	 */
	public Image createImage(final File f, final int width, int height);

	/**
	 * Get a thumbnail Dataset of square shape.
	 * @param set - must be 2D set
	 * @param size
	 * @return
	 */
	public IDataset getThumbnail(IDataset set, final int width, int height);
	
	/**
	 * Main method for thumbnails and other images of plots which must have
	 * a certain size. Deals with 1D and 2D, including surfaces.
	 * 
	 * THREAD SAFE
	 * 
	 * @param set
	 * @param size
	 * @return
	 */
	public Image getImage(PlotImageData data) throws Exception;
	
	/**
	 * Creates an object which may be used to cache the plotting system
	 * when looping over a stack and getting many images. For instance when
	 * exporting surface or 1D plots. This  IDisposable is then set in the
	 * call to PlotImageData to make it more efficient.
	 * 
	 * THREAD SAFE
	 * 
	 * @param plotName to use to look up plotting system (can be null)
	 * @return
	 * @throws Exception
	 */
	public IDisposable createPlotDisposable(String plotName) throws Exception;
	
}

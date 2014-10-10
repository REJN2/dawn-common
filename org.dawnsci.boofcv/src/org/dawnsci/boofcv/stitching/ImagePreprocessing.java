/*-
 * Copyright (c) 2014 Diamond Light Source Ltd.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.dawnsci.boofcv.stitching;

import java.util.List;

import org.eclipse.dawnsci.analysis.api.dataset.IDataset;
import org.eclipse.dawnsci.analysis.dataset.impl.Dataset;

import boofcv.struct.image.ImageBase;

/**
 * Static methods used to pre-process the images
 * 
 * @authors Baha El-Kassaby
 *
 */
public class ImagePreprocessing {

	/**
	 * Image cropped is a rectangle inside of the circular image
	 * 
	 * if centre of ellipse is (0, 0) then (x, y) = (a/sqrt(2), b/sqrt(2)) where a = xdiameter/2 and b = ydiameter/2<br>
	 * With origin of image being top left of the image, (x, y) the top left corner of the rectangle becomes
	 * (buffer + (a * (sqrt(2)-1)/sqrt(2) , buffer + (b * (sqrt(2)-1)/sqrt(2))
	 * and width = 2*a/sqrt(2) and height = 2*b/sqrt(2)
	 * 
	 * @param image
	 * @param xdiameter
	 * @param ydiameter
	 * @param buffer
	 * @return
	 */
	public static <T extends ImageBase<?>> T maxRectangleFromEllipticalImage(T image, int xdiameter, int ydiameter, int buffer) {
		// maximum rectangle dimension
		int a = xdiameter / 2;
		int b = ydiameter / 2;
		int width = (int) (xdiameter / Math.sqrt(2));
		int height = (int) (ydiameter / Math.sqrt(2));

		// find the top left corner of the largest square within the circle
		int cornerx = (int) (buffer + (a * (Math.sqrt(2)-1)/Math.sqrt(2)));
		int cornery = (int) (buffer + (b * (Math.sqrt(2)-1)/Math.sqrt(2)));

		T cropped = (T) image.subimage(cornerx, cornery, cornerx + width, cornery + height, null);
		T result = (T) cropped.clone();
		return result;
	}

	/**
	 * 
	 * @param input
	 * @param rows
	 * @param columns
	 * @return Ordered array of Dataset
	 * @throws Exception
	 */
	public static IDataset[][] ListToArray(List<IDataset> input, int rows, int columns) {
		IDataset[][] images = new Dataset[rows][columns];
		for (int i = 0; i < rows; i++) {
			for(int j = 0; j < columns; j++) {
				images[i][j] = input.get((i * columns) + j);
				//	images[i][j].setMetadata(getUniqueMetadata(i+1, j+1));
			}
		}
		return images;
	}
}

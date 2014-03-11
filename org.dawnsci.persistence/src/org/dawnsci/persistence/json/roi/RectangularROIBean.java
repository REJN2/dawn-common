/*-
 * Copyright (c) 2013 Diamond Light Source Ltd.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.dawnsci.persistence.json.roi;

import java.util.Arrays;

import uk.ac.diamond.scisoft.analysis.roi.IROI;
import uk.ac.diamond.scisoft.analysis.roi.RectangularROI;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class RectangularROIBean extends ROIBean{

	public static final String TYPE = "RectangularROI";

	private double[] lengths; // width and height

	private double angle;   // angle in radians

	private double[] endPoint; // end point

	public RectangularROIBean(){
		type = TYPE;
	}

	/**
	 * Returns the lengths (width[0] and height[1])
	 * @return double[]
	 */
	public double[] getLengths(){
		return lengths;
	}

	/**
	 * Returns the angle
	 * @return double
	 */
	public double getAngle(){
		return angle;
	}

	/**
	 * Returns the End point of the rectangle
	 * @return double[]
	 */
	public double[] getEndPoint(){
		return endPoint;
	}

	/**
	 * Set the width[0] and height[1] 
	 * @param lengths
	 */
	public void setLengths(double[] lengths){
		this.lengths = lengths;
	}

	/**
	 * Set the angle
	 * @param angle
	 */
	public void setAngle(double angle){
		this.angle = angle;
	}

	/**
	 * Set the end point of the Rectangle
	 * @param endPoint
	 */
	public void setEndPoint(double[] endPoint){
		this.endPoint = endPoint;
	}

	@Override
	public String toString(){
		return String.format("{\"type\": \"%s\", \"name\": \"%s\", \"startPoint\": \"%s\", \"endPoint\": \"%s\", \"angle\": \"%s\"}", 
				type, name, Arrays.toString(startPoint), Arrays.toString(endPoint), angle);
	}

	@Override
	@JsonIgnore
	public IROI getROI() {
		RectangularROI rroi = new RectangularROI(this.getStartPoint()[0], 
				this.getStartPoint()[1], this.getLengths()[0], 
				this.getLengths()[1], this.getAngle());
		rroi.setName(this.getName());
		return rroi;
	}
}
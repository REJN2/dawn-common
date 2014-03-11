/*-
 * Copyright (c) 2013 Diamond Light Source Ltd.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.dawnsci.persistence.json.roi;

import uk.ac.diamond.scisoft.analysis.roi.IROI;
import uk.ac.diamond.scisoft.analysis.roi.LinearROI;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class LinearROIBean extends ROIBean {

	public static final String TYPE = "LinearROI";
	private double len;    // length
	private double ang;    // angle in radians
	private double[] endPoint;

	public LinearROIBean() {
		type = TYPE;
	}

	public double getLength() {
		return len;
	}
	public void setLength(double len) {
		this.len = len;
	}
	public double getAngle() {
		return ang;
	}
	public void setAngle(double ang) {
		this.ang = ang;
	}

	public double[] getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(double[] endPoint) {
		this.endPoint = endPoint;
	}

	@Override
	@JsonIgnore
	public IROI getROI() {
		LinearROI lroi = new LinearROI();
		lroi.setPoint(this.getStartPoint());
		lroi.setEndPoint(this.getEndPoint());
		lroi.setName(this.getName());
		return lroi;
	}


}
/*
 * Copyright (c) 2012 European Synchrotron Radiation Facility,
 *                    Diamond Light Source Ltd.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */ 
package org.dawb.hdf5.nexus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.dawb.hdf5.HierarchicalDataFactory;
import org.dawb.hdf5.IHierarchicalDataFile;

import ncsa.hdf.object.Attribute;
import ncsa.hdf.object.Dataset;
import ncsa.hdf.object.Datatype;
import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.Group;
import ncsa.hdf.object.HObject;
import ncsa.hdf.object.h5.H5Datatype;

/**
 * Class used to mark groups in the hdf5 tree with nexus attributes.
 * 
 * This is a way not to use the nexus API.
 * 
 * @author gerring
 *
 */
public class NexusUtils {

	private static String NXCLASS = "NX_class";	
	
	private static String AXIS   = "axis";	
	private static String LABEL  = "label";	
	private static String PRIM   = "primary";	
	private static String UNIT   = "unit";	

	/**
	 * Sets the nexus attribute so that if something is looking for them,
	 * then they are there.
	 * 
	 * @param file
	 * @param entry
	 * @param entryKey
	 * @throws Exception
	 */
	public static void setNexusAttribute(final FileFormat file, 
			                             final HObject    entry,
			                             final String     entryKey) throws Exception {
		
		// Check if attribute is already there
		final List attrList = entry.getMetadata();
		if (attrList!=null) for (Object object : attrList) {
			if (object instanceof Attribute) {
				final Attribute a      = (Attribute)object;
				final String[]  aValue = (String[])a.getValue();
				if (NXCLASS.equals(a.getName()) && entryKey.equals(aValue[0])) return;
			}
		}
		
		final int id = entry.open();
		try {
	        String[] classValue = {entryKey};
	        Datatype attrType = new H5Datatype(Datatype.CLASS_STRING, classValue[0].length()+1, -1, -1);
	        Attribute attr = new Attribute(NXCLASS, attrType, new long[]{1});
	        attr.setValue(classValue);
			
	        file.writeAttribute(entry, attr, false);

	        if (entry instanceof Group) {
	        	attrList.add(attr);
				((Group)entry).writeMetadata(attrList);
	        } else if (entry instanceof Dataset) {
	        	attrList.add(attr);
				((Dataset)entry).writeMetadata(attrList);
	        }
		        
		    
		} finally {
			entry.close(id);
		}
	}
	
	/**
	 * Gets the nexus axes from the data node, if there are any there
	 * 
	 * TODO Deal with label attribute?
	 * 
	 * @param dataNode
	 * @param dimension we want the axis for 1, 2, 3 etc.
	 * @return
	 * @throws Exception 
	 */
	public static List<Dataset> getAxes(final Group dataNode, int dimension) throws Exception {
		
		final List<Dataset>         axesTmp = new ArrayList<Dataset>(3);
        final Map<Integer, Dataset> axesMap = new TreeMap<Integer, Dataset>();
		
        final List<HObject> children = dataNode.getMemberList();
		for (HObject hObject : children) {
			final List<?> att = hObject.getMetadata();
			if (!(hObject instanceof Dataset)) continue;
			
			Dataset axis = null;
			int     pos  = -1;
			for (Object object : att) {
				if (object instanceof Attribute) {
					Attribute attribute = (Attribute)object;
					if (AXIS.equals(attribute.getName())) {
						int iaxis = getAttributeIntValue(attribute);
						if (iaxis == dimension) axis = (Dataset)hObject;
						
					} else if (PRIM.equals(attribute.getName())) {
						pos = getAttributeIntValue(attribute);
					}
				}
			}
			
			if (axis!=null) {
				if (pos<0) {
					axesTmp.add(axis);
				} else {
					axesMap.put(pos, axis);
				}
			}
		}
		
		final List<Dataset>         axes = new ArrayList<Dataset>(3);
		if (!axesMap.isEmpty()) {
			for (Integer pos : axesMap.keySet()) {
				axes.add(axesMap.get(pos));
			}
		}
		axes.addAll(axesTmp);
		
		if (axes.isEmpty()) return null;
		
		return axes;
	}

	/**
	 * Gets the int value or returns -1 (Can only be used for values which are not allowed to be -1!)
	 * @param attribute
	 * @return
	 */
	private static int getAttributeIntValue(Attribute attribute) {
		final Object ob = attribute.getValue();
		if (ob instanceof int[]) {
			int[] ia = (int[])ob;
			return ia[0];
		} else if (ob instanceof String[]) {
			String[] sa = (String[])ob;
			try {
				return Integer.parseInt(sa[0]);
			} catch (Throwable ne) {
				return -1;
			}
		}

		return -1;
	}

	/**
	 * Returns names of axes in group at same level as name passed in.
	 * 
	 * This opens and safely closes a nexus file if one is not already open for
	 * this location.
	 * 
	 * @param filePath
	 * @param nexusPath
	 * @param dimension, the dimension we want the axis for.
	 * @return
	 * @throws Exception
	 */
	public static List<String> getAxisNames(String filePath, String nexusPath, int dimension) throws Exception {

		if (filePath==null || nexusPath==null) return null;
		if (dimension<1) return  null;
       	IHierarchicalDataFile file = null;
        try {
        	file = HierarchicalDataFactory.getReader(filePath);
        	final List<Dataset> axes = file.getNexusAxes(nexusPath, dimension);
        	if (axes==null) return null;
       
        	final List<String> names = new ArrayList<String>(axes.size());
        	for (Dataset ds : axes) names.add(ds.getName());
        	
        	return names;
        } finally {
        	if (file!=null) file.close();
        }
	}

}

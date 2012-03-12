package org.dawb.common.ui.plot.trace;

import java.util.ArrayList;
import java.util.List;

import org.dawb.common.ui.plot.region.RegionBounds;
import org.eclipse.swt.graphics.PaletteData;

import uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset;

public interface IImageTrace extends ITrace {

	public enum ImageOrigin {
		TOP_LEFT("Top left"), TOP_RIGHT("Top right"), BOTTOM_LEFT("Bottom left"), BOTTOM_RIGHT("Bottom right");
		
		public static List<ImageOrigin> origins;
		static {
			origins = new ArrayList<ImageOrigin>();
			origins.add(TOP_LEFT);
			origins.add(TOP_RIGHT);
			origins.add(BOTTOM_LEFT);
			origins.add(BOTTOM_RIGHT);
		}

		private String label;
		public String getLabel() {
			return label;
		}
		
		ImageOrigin(String label) {
			this.label = label;
		}
		
		public static ImageOrigin forLabel(String label) {
			for (ImageOrigin o : origins) {
				if (o.label.equals(label)) return o;
			}
			return null;
		}
	}

	/**
	 * Pulls a data set out of the image data for
	 * a given selection. For instance getting the bounds
	 * of a box to slice and return the data.
	 * 
	 * @param bounds
	 * @return
	 */
	AbstractDataset slice(RegionBounds bounds);

	/**
	 * Default is TOP_LEFT unlike normal plotting
	 * @return
	 */
	public ImageOrigin getImageOrigin();
	
	/**
	 * Repaints the axes and the image to the new origin.
	 * @param origin
	 */
	public void setImageOrigin(final ImageOrigin origin);
	
	/**
	 * PaletteData for creating the image from the AbstractDataset
	 * @return
	 */
	public PaletteData getPaletteData();
	
	/**
	 * Setting palette data causes the image to redraw with the new palette.
	 * @param paletteData
	 */
	public void setPaletteData(PaletteData paletteData);
	
}

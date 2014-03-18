package org.benetech.daisyimgexample;

import java.util.List;

public class ImageGroup {
	
	private List<DaisyImage> images;
	// For now as we are only talking about prodnotes of type xlp (and not bbx),
	// I am keeping this as type string.
	private String prodNotes;
	private String caption;
	
	public ImageGroup(List<DaisyImage> images, String prodNotes, String caption) {
		super();
		this.images = images;
		this.prodNotes = prodNotes;
		this.caption = caption;
	}

	public List<DaisyImage> getImages() {
		return images;
	}

	public String getProdNotes() {
		return prodNotes;
	}

	public String getCaption() {
		return caption;
	}
	
	
	
}

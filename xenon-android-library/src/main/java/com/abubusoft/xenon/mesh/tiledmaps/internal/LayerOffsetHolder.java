package com.abubusoft.xenon.mesh.tiledmaps.internal;

import com.abubusoft.xenon.math.Point2;

/**
 * Rappresenta per un layer, il suo punto di riferimento per quel che riguarda il primo tile da disegnare
 * 
 * @author xcesco
 *
 */
public class LayerOffsetHolder {
	public int tileIndexX;
	public int tileIndexY;

	public int offsetX;
	public int offsetY;

	public void setOffset(Point2 offsetPoint) {
		offsetX = (int) offsetPoint.x;
		offsetY = (int) offsetPoint.y;
	}

}
package com.abubusoft.xenon.game.ui;

import java.util.ArrayList;

import com.abubusoft.xenon.mesh.tiledmaps.TiledMap;

/**
 * <p>Rappresenta una scena.</p>
 * 
 * @author Francesco Benincasa
 *
 */
public class Scene {
	
	/**
	 * <p>tiledMap</p>
	 */
	public TiledMap tiledMap;

	/**
	 * <p></p>
	 */
	public ArrayList<UIViewLayer> layers;
	
	public Scene()
	{
		layers=new ArrayList<>();
	}
	
	/**
	 * <p>Imposta il tiledMap.</p>
	 * 
	 * @param value
	 */
	public void setTiledMap(TiledMap value)
	{
		tiledMap=value;
	}
}

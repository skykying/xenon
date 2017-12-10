package com.abubusoft.xenon.render;

import com.abubusoft.xenon.Camera;

public interface SceneDrawer {

	public void drawScene(Camera camera, long enlapsedTime, float speedAdapter);

}

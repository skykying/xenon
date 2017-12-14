package com.abubusoft.xenon.game.ui;

import com.abubusoft.xenon.texture.Texture;

public class UIPanelView extends UIViewContainer {

	private UIImageView background;

	public UIPanelView(int widthValue, int heightValue, Texture texture)
	{
		super();
		witdh=widthValue;
		height=heightValue;
		
		background = new UIImageView();		
		background.setImage(texture, witdh, height);
		
		type=UIViewComponentType.PANEL;
	}
	
	/**
	 * <p>
	 * Id all'interno del layer
	 * </p>
	 */
	public int uid;

	@Override
	public void update(long enlapsedTime, float speedAdapter) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resetUdate() {
		UIViewComponent view;

		int n = views.size();
		for (int i = 0; i < n; i++) {
			view = views.valueAt(i);

			view.resetUdate();
		}

	}

	@Override
	protected void prepareToDraw(float windowWitdh, float windowHeight) {
		// TODO Auto-generated method stub

	}

	public void setImage(Texture texture) {
		
	}
}

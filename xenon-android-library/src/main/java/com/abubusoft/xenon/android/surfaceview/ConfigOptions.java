package com.abubusoft.xenon.android.surfaceview;

import android.graphics.PixelFormat;

public class ConfigOptions {

	/**
	 * Indica il tipo di risoluzione da adottare. L'<bold>high</bold> è tipicamente la risoluzione RGBA_8888. La bassa risoluzione è quella che permette di consumare di meno in termini di memoria.
	 *
	 */
	public enum DisplayFormatType {
		DONT_CARE(0,0,0,0, PixelFormat.OPAQUE), RGB_565(5,6,5,0, PixelFormat.RGB_565), RGBA_8888(8,8,8,8, PixelFormat.RGBA_8888);
		
		DisplayFormatType(int r, int g, int b, int a, int pixelFormat)
		{
			this.r=r;
			this.g=g;
			this.b=b;
			this.a=a;
			this.pixelFormat=pixelFormat;
		}
		
		public final int r;
		public final int g;
		public final int b;
		public final int a;
		public final int pixelFormat;
	}

	public enum DepthSizeType {
		DONT_CARE(-1), NONE(0), DEPTH_SIZE_16(16), DEPTH_SIZE_24(24);
		
		DepthSizeType(int value)
		{
			this.value=value;			
		}
		
		public final int value;		
		
	}

	public enum StencilSizeType {
		DONT_CARE(-1), NONE(0), STENCIL_SIZE_8(8);
		
		StencilSizeType(int value)
		{
			this.value=value;			
		}
		
		public final int value;		
	}

	public enum ClientVersionType {
		OPENGL_ES_2, OPENGL_ES_3, OPENGL_ES_3_1
	}

	public enum MultiSampleType {
		DONT_CARE, ENABLED, NONE
	}

	/**
	 * Configurazione del display. Si può lasciare che sia il sistema a decidere quale risoluzione utilizzare
	 * o si può forzare la risoluzione impostando un valore diverso da DONT_CARE
	 */
	public DisplayFormatType displayFormat = DisplayFormatType.DONT_CARE;

	public DepthSizeType depthSize = DepthSizeType.NONE;

	public StencilSizeType stencilSize = StencilSizeType.NONE;

	public ClientVersionType clientVersion = ClientVersionType.OPENGL_ES_2;

	public MultiSampleType multiSample = MultiSampleType.NONE;

	/**
	 * Configurazione del display. Si può lasciare che sia il sistema a decidere quale risoluzione utilizzare
	 * o si può forzare la risoluzione impostando un valore diverso da DONT_CARE
	 * @param value
	 * 		opzione lato client
	 * @return
	 * 		format del display
	 */
	public ConfigOptions displayFormat(DisplayFormatType value) {
		displayFormat = value;
		return this;
	}

	public ConfigOptions depthSize(DepthSizeType value) {
		depthSize = value;
		return this;
	}

	public ConfigOptions stencilSize(StencilSizeType value) {
		stencilSize = value;
		return this;
	}

	public ConfigOptions clientVersion(ClientVersionType value) {
		clientVersion = value;
		return this;
	}

	public ConfigOptions multiSample(MultiSampleType value) {
		multiSample = value;
		return this;
	}

	/**
	 * <ul>
	 * <li>DisplayFormat = DisplayFormat.DONT_CARE</li>
	 * <li>DepthSize = DepthSize.DONT_CARE</li>
	 * <li>StencilSize = StencilSize.DONT_CARE</li>
	 * <li>ClientVersion = ClientVersion.OPENGL_ES_2</li>
	 * <li>MultiSample = MultiSample.DONT_CARE,</li>
	 * </ul>
	 * 
	 * @return
	 * 		istanza delle opzioni da usare per creare le surfaceView di XenonGL
	 */
	public static ConfigOptions build() {
		return new ConfigOptions();
	}
}

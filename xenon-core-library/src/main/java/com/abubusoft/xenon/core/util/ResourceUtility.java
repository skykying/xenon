/**
 * 
 */
package com.abubusoft.xenon.core.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * @author Francesco Benincasa
 *
 */
public abstract class ResourceUtility {

	/**
	 * <p>Dato un context ed una stringa con il nome di una risorsa, ricaviamo
	 * l'address. Supporta tutti i tipi di risorsa. Può avere davanti il @, ma anche no.</p>
	 * 
	 * <p>Se non trova l'elemento, viene restituito 0.</p>
	 * 
	 * 
	 * @param context
	 * @param resourceName
	 * @return
	 */
	public static int resolveAddress(Context context, String resourceName)
	{
		if (resourceName.startsWith("@"))
		{
			resourceName=resourceName.substring(1);
		} else if (resourceName.startsWith("res"))
		{
			// nel caso in cui sia /res/drawble, lo trasforma in una stringa utilizzabile					
			resourceName=resourceName.substring(4);
			//resourceName=resourceName.replace("drawable-nodpi","drawable");
			
			if (resourceName.indexOf("-")>-1)
			{
				resourceName=resourceName.substring(0, resourceName.indexOf("-"))+resourceName.substring(resourceName.indexOf('/') );
			}
		}
		
		String valueName = resourceName.substring(resourceName.lastIndexOf('/') + 1);
		if (valueName.indexOf(".")>0)
		{
			valueName=valueName.substring(0,valueName.indexOf("."));
		}
		
		String valueType=resourceName.substring(0, resourceName.lastIndexOf('/'));
		
		
		int resourceId = context.getResources().getIdentifier(valueName, valueType, context.getPackageName());
		
		return resourceId;
	}
	
	/**
	 * Recupera il valore della stringa ricercata
	 * 
	 * @param context
	 * @param address
	 * @return
	 */
	public static String resolveString(Context context, int address)
	{
		return context.getResources().getString(address);		
	}
	
	/**
	 * <p>Recupera l'elenco delle stringhe</p>
	 * 
	 * @param context
	 * @param address
	 * @return
	 */
	public static String[] resolveArrayOfString(Context context, int address)
	{
		return context.getResources().getStringArray(address);
	}
	
	/**
	 * Recupera il valore della stringa ricercata partendo dalla stringa che rappresenta
	 * l'indirizzo della risorsa. Se non esiste, restituisce null
	 * 
	 * @param context
	 * @param address
	 * @return
	 */
	public static String resolveString(Context context, String namedAddress)
	{
		int address=resolveAddress(context, namedAddress);
		
		if (address!=0)
		return context.getResources().getString(address);
		
		return null;
	}
	
	/**
	 * Converte una dimensione in dp nei relativi pixel
	 * @param context
	 * @param dp
	 * @return
	 */
	public static float convertDp2Pixels(Context context, int dp)
	{
		Resources r = context.getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
		
		
		
		return px;
	}
	
	/**
	 * Misura le dimensioni di un testo con una determinata fonte
	 * @param input
	 * @param fontSizeInPixel
	 * @return
	 */
	public static float measureTextWidth(String input, float fontSizeInPixel)
	{
		Paint paint=new Paint();		
		paint.setTextSize(fontSizeInPixel);
		float res=paint.measureText(input);
		//"xxxxxxxxxxxxxxxxxx"
		
		paint=null;
		return res;
	}
	
	/**
	 * Converte degli sp in pixels. Il text small ad esempio sono 14sp.
	 * 
	 * Vedi
	 * http://stackoverflow.com/questions/6263250/convert-pixels-to-sp
	 *  
	 * @param context
	 * @param sp
	 * @return
	 */
	public static int convertSpToPixels(Context context, float sp) {
	    float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
	    return (int) (sp*scaledDensity);
	}
	
	/**
	 * This method converts dp unit to equivalent pixels, depending on device density. 
	 * 
	 * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
	 * @param context Context to get resources and device specific display metrics
	 * @return A float value to represent px equivalent to dp depending on device density
	 */
	public static float convertDpToPixel(Context context, float dp){
	    Resources resources = context.getResources();
	    DisplayMetrics metrics = resources.getDisplayMetrics();
	    float px = dp * (metrics.densityDpi / 160f);
	    return px;
	}

	/**
	 * This method converts device specific pixels to density independent pixels.
	 * 
	 * @param px A value in px (pixels) unit. Which we need to convert into db
	 * @param context Context to get resources and device specific display metrics
	 * @return A float value to represent dp equivalent to px value
	 */
	public static float convertPixelsToDp(Context context, float px){
	    Resources resources = context.getResources();
	    DisplayMetrics metrics = resources.getDisplayMetrics();
	    float dp = px / (metrics.densityDpi / 160f);
	    return dp;
	}

}

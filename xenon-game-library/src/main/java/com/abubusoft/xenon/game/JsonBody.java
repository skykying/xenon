package com.abubusoft.xenon.game;

import java.util.ArrayList;

import com.abubusoft.kripton.annotation.BindType;
import com.abubusoft.xenon.core.Uncryptable;

@Uncryptable
@BindType
public class JsonBody {

	public JsonBody()
	{
		vertices=new ArrayList<JsonPoint2D>();
	}
	
	public String type;
	
	public JsonPoint2D center;
	
	public ArrayList<JsonPoint2D> vertices;
	
	public float getBoundingBoxArea()
	{
		float maxX, maxY, minX, minY;
		
		minX=vertices.get(0).x;
		minY=vertices.get(0).y;
		maxX=minX;
		maxY=minY;
		
		for (int i=1; i<vertices.size();i++)
		{
			minX=Math.min(minX, vertices.get(i).x);
			minY=Math.min(minY, vertices.get(i).y);
			
			maxX=Math.max(maxX, vertices.get(i).x);
			maxY=Math.max(maxY, vertices.get(i).y);
		}
		
		float area=Math.abs((maxX-minX)*(maxY-minY));
		
		return area;
	}
}

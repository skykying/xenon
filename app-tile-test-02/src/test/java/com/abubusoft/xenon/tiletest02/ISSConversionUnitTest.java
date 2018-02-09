package com.abubusoft.xenon.tiletest02;

import com.abubusoft.xenon.android.XenonLogger;
import com.abubusoft.xenon.math.XenonMath;
import com.abubusoft.xenon.mesh.tiledmaps.internal.LayerOffsetHolder;
import com.abubusoft.xenon.mesh.tiledmaps.isostaggered.ISSMapHandler;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ISSConversionUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        int pos[][]={
                {0,0},
                {16,0},
                {32,0},
                {48,0},
                {96,0},
                {0,96},
                {128,0}
        };

        LayerOffsetHolder holder=new LayerOffsetHolder();

        for (int i=0; i<pos.length;i++) {
            convertMap2ViewLayer(holder, pos[i][0], pos[i][1]);
        }
    }

    private int tileHeight=32;
    private int tileWidth=32;

    public void convertMap2ViewLayer(LayerOffsetHolder offsetHolder, int mapX, int mapY) {
        int ix, iy;
        ix = (mapX + 2 * mapY) / 2;
        iy = (-mapX + 2 * mapY) /2;

        // inverso
        // x = xi - yi
        // y= (xi+yi) / 2

        float temp;

        // v2: ok
        // passiamo da map a iso diamond
        offsetHolder.tileIndexX = XenonMath.floorDiv(ix , tileHeight);
        offsetHolder.tileIndexY = XenonMath.floorDiv(iy , tileHeight);

        int sx, sy;
        sx = Math.abs(ix % tileHeight);
        sy = Math.abs(iy % tileHeight);
      /*  if (sx<0) sx=map.tileHeight+sx;
        if (sy<0) sy=map.tileHeight+sy;*/
        // sx, sy contiene resto delle iso

        int a = offsetHolder.tileIndexX;
        int b = offsetHolder.tileIndexY;

        // passiamo da diamon a staggered
        offsetHolder.tileIndexX = XenonMath.floorDiv((a - b + Math.abs((a + b) % 2)) , 2);
        offsetHolder.tileIndexY = a + b;

        int ox = offsetHolder.tileIndexX;
        int oy = offsetHolder.tileIndexY;

        // v2
        offsetHolder.screenOffsetX = mapX % tileWidth;
        offsetHolder.screenOffsetY = mapY % tileHeight;

        //v3
        ISSMapHandler.Status volo = ISSMapHandler.Status.STANDARD;

        if (Math.abs(offsetHolder.tileIndexY % 2) == 1) {
            // if (sx<0) sx*=-1;//map.tileHeight-sx;
            //if (sy<0) sy*=-1;

            volo = ISSMapHandler.Status.UNSPOSTR;
            if (sx - sy < 0) {
                if (sx + sy < tileHeight) {
                    volo = ISSMapHandler.Status.AREA_A;

                    offsetHolder.tileIndexY--;
                    offsetHolder.screenOffsetX-=tileWidth;
                } else {
                    volo = ISSMapHandler.Status.AREA_D;

                    offsetHolder.tileIndexY++;
                    offsetHolder.screenOffsetX-=tileWidth;
                }
            } else {
                if (sy + sx < tileHeight) {
                    volo = ISSMapHandler.Status.AREA_B;

                    offsetHolder.tileIndexY++;
                    offsetHolder.tileIndexX--;
                } else {
                    volo = ISSMapHandler.Status.AREA_C;

                    //offsetHolder.screenOffsetY+=map.tileHeight;
                    offsetHolder.tileIndexY++;
                    //offsetHolder.tileIndexX--;
                }
            }
        }

        System.out.println(String.format("map[%s, %s] -> iso[%s, %s], tiles I[%s, %s] -> S[%s, %s] (OS[%s, %s]), map off x,y (%s, %s) [%s]", mapX, mapY, ix, iy, a, b, offsetHolder.tileIndexX, offsetHolder.tileIndexY, ox, oy, offsetHolder.screenOffsetX, offsetHolder.screenOffsetY, volo));

        // inverte y
        offsetHolder.screenOffsetY = -offsetHolder.screenOffsetY;
    }
}
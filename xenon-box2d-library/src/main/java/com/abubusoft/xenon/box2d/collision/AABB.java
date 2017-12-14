/*******************************************************************************
 * Copyright (c) 2013, Daniel Murphy
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 	* Redistributions of source code must retain the above copyright notice,
 * 	  this list of conditions and the following disclaimer.
 * 	* Redistributions in binary form must reproduce the above copyright notice,
 * 	  this list of conditions and the following disclaimer in the documentation
 * 	  and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package com.abubusoft.xenon.box2d.collision;

import com.abubusoft.xenon.box2d.common.MathUtils;
import com.abubusoft.xenon.box2d.common.Settings;
import com.abubusoft.xenon.box2d.common.Vec2;
import com.abubusoft.xenon.box2d.pooling.IWorldPool;
import com.abubusoft.xenon.box2d.pooling.normal.DefaultWorldPool;

/** An axis-aligned bounding box. */
public class AABB {
  /** Bottom left vertex of bounding box. */
  //public final Vec2 lowerBound;
  public float lowerBoundX;
  public float lowerBoundY;
  /** Top right vertex of bounding box. */
  //public final Vec2 upperBound;
  public float upperBoundX;
  public float upperBoundY;

  /**
   * Creates the default object, with vertices at 0,0 and 0,0.
   */
  public AABB() {
    //lowerBound = new Vec2();
    //upperBound = new Vec2();
  }


  /**
   * Creates an AABB object using the given bounding vertices.
   *
   * @param lowerVertex the bottom left vertex of the bounding box
   * @param maxVertex the top right vertex of the bounding box
   */
  public AABB(final float lowerVertexX, final float lowerVertexY,final float upperVertexX, final float upperVertexY) {
    this.lowerBoundX = lowerVertexX;
    this.lowerBoundY= lowerVertexY;
    this.upperBoundX = upperVertexX;
    this.upperBoundY=upperVertexY;
  }

  /**
   * Sets this object from the given object
   *
   * @param aabb the object to copy from
   */
  public final void set(final AABB aabb) {
    this.lowerBoundX = aabb.lowerBoundX;
    this.lowerBoundY= aabb.lowerBoundY;
    this.upperBoundX = aabb.upperBoundX;
    this.upperBoundY=aabb.upperBoundY;
  }

  /** Verify that the bounds are sorted */
  public final boolean isValid() {
    final float dx = upperBoundX - lowerBoundX;
    if (dx < 0f) {
      return false;
    }
    final float dy = upperBoundY - lowerBoundY;
    if (dy < 0) {
      return false;
    }
    return isValidLowerBound() && isValidUpperBound();
  }

  /** True if the vector represents a pair of valid, non-infinite floating point numbers. */
  public final boolean isValidLowerBound() {
    return !Float.isNaN(lowerBoundX) && !Float.isInfinite(lowerBoundX) && !Float.isNaN(lowerBoundY) && !Float.isInfinite(lowerBoundY);
  }

  /** True if the vector represents a pair of valid, non-infinite floating point numbers. */
  public final boolean isValidUpperBound() {
    return !Float.isNaN(upperBoundX) && !Float.isInfinite(upperBoundX) && !Float.isNaN(upperBoundY) && !Float.isInfinite(upperBoundY);
  }

  /**
   * Get the center of the AABB
   *
   * @return
   */
  public final Vec2 getCenter() {
    final Vec2 center = new Vec2(lowerBoundX, lowerBoundY);
    center.addLocal(upperBoundX, upperBoundY);
    center.mulLocal(.5f);
    return center;
  }

  public final void getCenterToOut(final Vec2 out) {
    out.x = (lowerBoundX + upperBoundX) * .5f;
    out.y = (lowerBoundY + upperBoundY) * .5f;
  }

  /**
   * Get the extents of the AABB (half-widths).
   *
   * @return
   */
  public final Vec2 getExtents() {
    final Vec2 center = new Vec2(upperBoundX, upperBoundY);
    center.subLocal(lowerBoundX, lowerBoundY);
    center.mulLocal(.5f);
    return center;
  }

  public final void getExtentsToOut(final Vec2 out) {
    out.x = (upperBoundX - lowerBoundX) * .5f;
    out.y = (upperBoundY - lowerBoundY) * .5f; // thanks FDN1
  }

  public final void getVertices(Vec2[] argRay) {
    argRay[0].set(lowerBoundX, lowerBoundY);
    argRay[1].set(lowerBoundX, lowerBoundY);
    argRay[1].x += upperBoundX - lowerBoundX;
    argRay[2].set(upperBoundX, upperBoundY);
    argRay[3].set(upperBoundX, upperBoundY);
    argRay[3].x -= upperBoundX - lowerBoundX;
  }

  /**
   * Combine two AABBs into this one.
   *
   * @param aabb1
   * @param aab
   */
  public final void combine(final AABB aabb1, final AABB aab) {
    lowerBoundX = aabb1.lowerBoundX < aab.lowerBoundX ? aabb1.lowerBoundX : aab.lowerBoundX;
    lowerBoundY = aabb1.lowerBoundY < aab.lowerBoundY ? aabb1.lowerBoundY : aab.lowerBoundY;
    upperBoundX = aabb1.upperBoundX > aab.upperBoundX ? aabb1.upperBoundX : aab.upperBoundX;
    upperBoundY = aabb1.upperBoundY > aab.upperBoundY ? aabb1.upperBoundY : aab.upperBoundY;
  }

  /**
   * Gets the perimeter length
   *
   * @return
   */
  public final float getPerimeter() {
    return 2.0f * (upperBoundX - lowerBoundX + upperBoundY - lowerBoundY);
  }

  /**
   * Combines another aabb with this one
   *
   * @param aabb
   */
  public final void combine(final AABB aabb) {
    lowerBoundX = lowerBoundX < aabb.lowerBoundX ? lowerBoundX : aabb.lowerBoundX;
    lowerBoundY = lowerBoundY < aabb.lowerBoundY ? lowerBoundY : aabb.lowerBoundY;
    upperBoundX = upperBoundX > aabb.upperBoundX ? upperBoundX : aabb.upperBoundX;
    upperBoundY = upperBoundY > aabb.upperBoundY ? upperBoundY : aabb.upperBoundY;
  }

  /**
   * Does this aabb contain the provided AABB.
   *
   * @return
   */
  public final boolean contains(final AABB aabb) {
    /*
     * boolean result = true; result = result && lowerBound.x <= aabb.lowerBound.x; result = result
     * && lowerBound.y <= aabb.lowerBound.y; result = result && aabb.upperBound.x <= upperBound.x;
     * result = result && aabb.upperBound.y <= upperBound.y; return result;
     */
    // djm: faster putting all of them together, as if one is false we leave the logic
    // early
    return lowerBoundX <= aabb.lowerBoundX && lowerBoundY <= aabb.lowerBoundY
            && aabb.upperBoundX <= upperBoundX && aabb.upperBoundY <= upperBoundY;
  }

  /**
   * @deprecated please use {@link #raycast(RayCastOutput, RayCastInput, IWorldPool)} for better
   *             performance
   * @param output
   * @param input
   * @return
   */
  public final boolean raycast(final RayCastOutput output, final RayCastInput input) {
    return raycast(output, input, new DefaultWorldPool(4, 4));
  }

  /**
   * From Real-time Collision Detection, p179.
   *
   * @param output
   * @param input
   */
  public final boolean raycast(final RayCastOutput output, final RayCastInput input,
                               IWorldPool argPool) {
    float tmin = -Float.MAX_VALUE;
    float tmax = Float.MAX_VALUE;

    final Vec2 p = argPool.popVec2();
    final Vec2 d = argPool.popVec2();
    final Vec2 absD = argPool.popVec2();
    final Vec2 normal = argPool.popVec2();

    p.set(input.p1);
    d.set(input.p2).subLocal(input.p1);
    Vec2.absToOut(d, absD);

    // x then y
    if (absD.x < Settings.EPSILON) {
      // Parallel.
      if (p.x < lowerBoundX || upperBoundX < p.x) {
        argPool.pushVec2(4);
        return false;
      }
    } else {
      final float inv_d = 1.0f / d.x;
      float t1 = (lowerBoundX - p.x) * inv_d;
      float t2 = (upperBoundX - p.x) * inv_d;

      // Sign of the normal vector.
      float s = -1.0f;

      if (t1 > t2) {
        final float temp = t1;
        t1 = t2;
        t2 = temp;
        s = 1.0f;
      }

      // Push the min up
      if (t1 > tmin) {
        normal.setZero();
        normal.x = s;
        tmin = t1;
      }

      // Pull the max down
      tmax = MathUtils.min(tmax, t2);

      if (tmin > tmax) {
        argPool.pushVec2(4);
        return false;
      }
    }

    if (absD.y < Settings.EPSILON) {
      // Parallel.
      if (p.y < lowerBoundY || upperBoundY < p.y) {
        argPool.pushVec2(4);
        return false;
      }
    } else {
      final float inv_d = 1.0f / d.y;
      float t1 = (lowerBoundY - p.y) * inv_d;
      float t2 = (upperBoundY - p.y) * inv_d;

      // Sign of the normal vector.
      float s = -1.0f;

      if (t1 > t2) {
        final float temp = t1;
        t1 = t2;
        t2 = temp;
        s = 1.0f;
      }

      // Push the min up
      if (t1 > tmin) {
        normal.setZero();
        normal.y = s;
        tmin = t1;
      }

      // Pull the max down
      tmax = MathUtils.min(tmax, t2);

      if (tmin > tmax) {
        argPool.pushVec2(4);
        return false;
      }
    }

    // Does the ray start inside the box?
    // Does the ray intersect beyond the max fraction?
    if (tmin < 0.0f || input.maxFraction < tmin) {
      argPool.pushVec2(4);
      return false;
    }

    // Intersection.
    output.fraction = tmin;
    output.normal.x = normal.x;
    output.normal.y = normal.y;
    argPool.pushVec2(4);
    return true;
  }

  public static final boolean testOverlap(final AABB a, final AABB b) {
    if (b.lowerBoundX - a.upperBoundX > 0.0f || b.lowerBoundY - a.upperBoundY > 0.0f) {
      return false;
    }

    if (a.lowerBoundX - b.upperBoundX > 0.0f || a.lowerBoundY - b.upperBoundY > 0.0f) {
      return false;
    }

    return true;
  }

  @Override
  public final String toString() {
    final String s = "AABB[ (" + lowerBoundX + " : "+lowerBoundY+") , (" + upperBoundX + " : "+upperBoundY+")]";
    return s;
  }
}

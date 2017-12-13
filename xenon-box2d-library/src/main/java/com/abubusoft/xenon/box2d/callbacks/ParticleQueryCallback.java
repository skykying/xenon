package com.abubusoft.xenon.box2d.callbacks;

import com.abubusoft.xenon.box2d.dynamics.World;

/**
 * Callback class for AABB queries. See
 * {@link World#queryAABB(QueryCallback, com.abubusoft.xenon.box2d.collision.AABB)}.
 * 
 * @author dmurph
 * 
 */
public interface ParticleQueryCallback {
  /**
   * Called for each particle found in the query AABB.
   * 
   * @return false to terminate the query.
   */
  boolean reportParticle(int index);
}

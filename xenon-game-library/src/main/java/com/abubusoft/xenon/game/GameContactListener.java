package com.abubusoft.xenon.game;

import com.abubusoft.xenon.box2d.callbacks.ContactImpulse;
import com.abubusoft.xenon.box2d.callbacks.ContactListener;
import com.abubusoft.xenon.box2d.collision.Manifold;
import com.abubusoft.xenon.box2d.dynamics.BodyType;
import com.abubusoft.xenon.box2d.dynamics.Fixture;
import com.abubusoft.xenon.box2d.dynamics.contacts.Contact;
import com.abubusoft.xenon.mesh.tiledmaps.ObjBase;
import com.abubusoft.xenon.mesh.tiledmaps.TiledMap;
import org.abubu.elio.logger.ElioLogger;

public class GameContactListener implements ContactListener {
	public TiledMap tiledMap;

	public GamePortalOperations operations;

	@Override
	public void beginContact(Contact contact) {
		ObjBase userData1 = (ObjBase) contact.getFixtureA().getUserData();
		ObjBase userData2 = (ObjBase) contact.getFixtureB().getUserData();
		
		if (userData1!=null && userData2!=null)
		{
			ElioLogger.debug("Contact %s %s ", userData1.name, userData2.name);
		} else
		{
			ElioLogger.error("Contact ERROR %s %s ");
		}
		
		
		if (contact.getFixtureA().isSensor() || contact.getFixtureB().isSensor()) {
			Fixture sensor;
			Fixture object;

			if (!contact.getFixtureA().isSensor()) {
				object = contact.getFixtureA();
				sensor = contact.getFixtureB();
			} else {
				object = contact.getFixtureB();
				sensor = contact.getFixtureA();
			}

			if (sensor.getUserData() != null) {
				ObjBase portalData = (ObjBase) sensor.getUserData();

				if (portalData.name.startsWith("portal")) {
					ElioLogger.debug("-----------------------------");
					ElioLogger.debug("BEGIN PORTAL " + portalData.name);
					ElioLogger.debug("-----------------------------");
				} else {
					beginContactWithSensor(object, sensor);
				}
			}
		} else {
			Fixture object;
			Fixture staticObject;
						
			if (contact.getFixtureA().getBody().getType() != BodyType.STATIC) {
				object = contact.getFixtureA();
				staticObject = contact.getFixtureB();
			} else {
				object = contact.getFixtureB();
				staticObject = contact.getFixtureA();
			}

			ElioLogger.debug("-----------------------------");
			ElioLogger.debug("BEGIN CONTACT  ");
			ElioLogger.debug("-----------------------------");
			
			beginContactWithStatic(object, staticObject);
		}

	}

	public void beginContactWithStatic(Fixture object, Fixture staticObject) {
		// TODO Auto-generated method stub
		
	}

	public void beginContactWithSensor(Fixture object, Fixture sensor) {

	}

	@Override
	public void endContact(Contact contact) {

		// gestione dei portali
		if (contact.getFixtureA().isSensor() || contact.getFixtureB().isSensor()) {
			Fixture body;
			Fixture sensor;

			if (!contact.getFixtureA().isSensor()) {
				body = contact.getFixtureA();
				sensor = contact.getFixtureB();
			} else {
				body = contact.getFixtureB();
				sensor = contact.getFixtureA();
			}

			if (sensor.getUserData() != null) {
				ObjBase portalData = (ObjBase) sensor.getUserData();

				// siamo innanzi ad un portale
				if (portalData.name.startsWith("portal")) {

					if (portalData.name.endsWith("Right") || portalData.name.endsWith("Left")) {

						if (body.getBody().getPosition().x > Utils.pixelToBox2D(tiledMap.mapWidth / 2f)) {
							ElioLogger.debug("-----------------------------");
							ElioLogger.debug("MOVE TO THE LEFT");
							ElioLogger.debug("-----------------------------");
							operations.add(body.getBody(), body.getBody().getPosition().x - (Utils.pixelToBox2D(tiledMap.mapWidth)), body.getBody().getPosition().y);
						} else if (body.getBody().getPosition().x < -Utils.pixelToBox2D(tiledMap.mapWidth / 2f)) {
							ElioLogger.debug("-----------------------------");
							ElioLogger.debug("MOVE TO THE RIGHT");
							ElioLogger.debug("-----------------------------");
							operations.add(body.getBody(), body.getBody().getPosition().x + (Utils.pixelToBox2D(tiledMap.mapWidth)), body.getBody().getPosition().y);
						} else {
							ElioLogger.debug("-----------------------------");
							ElioLogger.debug("IGNORE");
							ElioLogger.debug("-----------------------------");
						}

					}
				} else {
					endContactWithSensor(body, sensor);
				}
			}
		} else {
			Fixture object;
			Fixture staticObject;

			if (contact.getFixtureA().getBody().getType() != BodyType.STATIC) {
				object = contact.getFixtureA();
				staticObject = contact.getFixtureB();
			} else {
				object = contact.getFixtureB();
				staticObject = contact.getFixtureA();
			}

			ElioLogger.debug("-----------------------------");
			ElioLogger.debug("END CONTACT  ");
			ElioLogger.debug("-----------------------------");
			
			endContactWithStatic(object, staticObject);
		}
	}

	public void endContactWithStatic(Fixture object, Fixture staticObject) {
		// TODO Auto-generated method stub
		
	}

	public void endContactWithSensor(Fixture body, Fixture sensor) {

	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {

	}
}

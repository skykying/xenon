package com.abubusoft.xenon.game;

import org.abubu.argon.box2d.collision.shapes.PolygonShape;
import org.abubu.argon.box2d.common.Vec2;
import org.abubu.argon.box2d.dynamics.Body;
import org.abubu.argon.box2d.dynamics.BodyDef;
import org.abubu.argon.box2d.dynamics.BodyType;
import org.abubu.argon.box2d.dynamics.FixtureDef;
import org.abubu.argon.box2d.dynamics.World;
import org.abubu.argon.entity.SpriteEntity;
import org.abubu.argon.math.Point2;
import org.abubu.argon.mesh.tiledmaps.ObjClass;
import org.abubu.argon.mesh.tiledmaps.ObjDefinition;
import org.abubu.argon.mesh.tiledmaps.ObjInstance;
import org.abubu.argon.mesh.tiledmaps.ObjSprite;
import org.abubu.argon.mesh.tiledmaps.TiledMap;
import org.abubu.argon.mesh.tiledmaps.orthogonal.OrthogonalHelper;
import org.abubu.argon.mesh.tiledmaps.tmx.loader.TMXPredefinedProperties;

public class Utils {
	// Create a JBox2D world.
	public static World world;

	public static Body addPlayer(TiledMap tiledMap, ObjDefinition objDefinition, SpriteEntity entity, JsonBody bodyDef) {
		// Create an JBox2D player1Body defination for ball.
		BodyDef bd = new BodyDef();
		bd.type = BodyType.DYNAMIC;
		//bd.setFixedRotation(false);
		bd.setFixedRotation(true);

		Point2 position = OrthogonalHelper.translateInPhysicCoords(tiledMap, entity.position.x, entity.position.y);
		bd.position.set(Utils.pixelToBox2D(position.x), Utils.pixelToBox2D(position.y));

		// poligono
		PolygonShape polygon = new PolygonShape();
		{
			Vec2 p;
			Vec2[] array = new Vec2[bodyDef.vertices.size()];
			for (int i = 0; i < array.length; i++) {
				p = new Vec2(bodyDef.vertices.get(i).x, bodyDef.vertices.get(i).y);
				array[i] = p;
			}

			polygon.set(array, array.length);
		}

		// Create a fixture for ball
		FixtureDef fd = new FixtureDef();
		fd.shape = polygon;
		fd.density = 1.0f;
		fd.friction = 1.0f;
		fd.restitution = 0.6f;
		

		/**
		 * Virtual invisible JBox2D player1Body of ball. Bodies have velocity and position. Forces, <span id="IL_AD10" class="IL_AD">torques</span>, and impulses can be applied to
		 * these bodies.
		 */
		Body body = Utils.world.createBody(bd);

		ObjSprite objectSprite = ObjSprite.build(objDefinition, entity);
		
		fd.setUserData(objectSprite);
		body.setUserData(objectSprite);
		body.createFixture(fd);

		return body;
	}

	/**
	 * <p>
	 * Inserisce un'oggetto di tipo definizione all'interno del world.
	 * </p>
	 * 
	 * @param tiledMap
	 * 		mappa
	 * @param obj
	 * 		definizione dell'oggetto
	 */
	public static void addObject(TiledMap tiledMap, ObjDefinition obj) {
		PolygonShape ps = new PolygonShape();

		ps.setAsBox(Utils.pixelToBox2D(obj.width * 0.5f), Utils.pixelToBox2D(obj.height * 0.5f));

		FixtureDef fd = new FixtureDef();
		fd.shape = ps;
		fd.setSensor(obj.getPropertyAsBool(TMXPredefinedProperties.SENSOR, false));
		fd.setUserData(obj);

		Point2 position = OrthogonalHelper.translateInPhysicCoords(tiledMap, obj.x + obj.width * 0.5f, obj.y + obj.height * 0.5f);

		BodyDef bd = new BodyDef();
		bd.type = BodyType.valueOf(obj.getProperty("type", BodyType.STATIC.toString()));
		bd.position.set(Utils.pixelToBox2D(position.x), Utils.pixelToBox2D(position.y));

		Body body = world.createBody(bd);
		body.createFixture(fd);

		// salviamo lo user object
		body.setUserData(obj);
	}
	
	public static void removeObjectBody(Body body)
	{
		world.destroyBody(body);
	}

	/**
	 * <p>.</p>
	 * 
	 * @param tiledMap
	 * @param className
	 * @param name
	 * @param mapPositionX
	 * @param mapPositionY
	 */
	public static Body addObjectFromClass(TiledMap tiledMap, String className, String name, int mapPositionX, int mapPositionY) {
		// deve subire ancora il fattore di conversione

		ObjClass objClazz = tiledMap.objectClasses.get(className);
		ObjInstance objInstance=ObjInstance.buildInstance(name, objClazz);
		ObjDefinition objPart;
		
		PolygonShape ps;
		FixtureDef fd;
		
		Point2 partCenter;

		// prima converte in physic coord, poi sottrae le dimensioni del box
		Point2 centerOrigin = OrthogonalHelper.translateInPhysicCoords(tiledMap, mapPositionX + (objClazz.width * 0.5f), mapPositionY + (objClazz.height * 0.5f)).copy();
		// origin.addCoords((clazz.width * 0.5f), -(clazz.height * 0.5f));
		// convertiamo in box2d
		// physicPos=Utils.pixelToBox2D(physicPos);

		BodyDef bd = new BodyDef();
		bd.type = BodyType.valueOf(objInstance.type);
		bd.position.set(Utils.pixelToBox2D(centerOrigin.x), Utils.pixelToBox2D(centerOrigin.y));

		Body body = world.createBody(bd);
		for (int i = 0; i < objClazz.parts.size(); i++) {
			objPart = objClazz.parts.get(i);

			ps = new PolygonShape();

			// prendiamo punto centrale dei vari body
			// partCenter = Point2D.set(obj.x, obj.y);
			// partCenter.addCoords(, );
			// partCenter.addCoords(obj.width * 0.5f, obj.height * 0.5f);
			partCenter = Point2.set(objPart.x - objClazz.width * 0.5f, -objPart.y + objClazz.height * 0.5f);
			// partCenter = TiledMapHelper.translateInPhysicCoords(tiledMap, partCenter.x, partCenter.y);

			ps.setAsBox(Utils.pixelToBox2D(objPart.width * 0.5f), Utils.pixelToBox2D(objPart.height * 0.5f), new Vec2(Utils.pixelToBox2D(partCenter.x), Utils.pixelToBox2D(partCenter.y)), 0f);

			fd = new FixtureDef();
			fd.shape = ps;
			fd.setSensor(objPart.getPropertyAsBool(TMXPredefinedProperties.SENSOR, false));
			fd.setUserData(objPart);

			body.createFixture(fd);
		}

		// salviamo lo user object
		body.setUserData(objInstance);
		
		return body;
	}

	// This method adds a ground to the screen.
	public static void addGround(TiledMap tiledMap) {
		PolygonShape ps = new PolygonShape();
		ps.setAsBox(Utils.pixelToBox2D(tiledMap.mapWidth / 2f), 0.5f);

		FixtureDef fd = new FixtureDef();
		fd.shape = ps;

		BodyDef bd = new BodyDef();
		bd.type = BodyType.STATIC;

		// bordo basso dello schermo
		bd.position = new Vec2(0.0f, -(Utils.pixelToBox2D(tiledMap.mapHeight / 2f) + 0.5f));

		world.createBody(bd).createFixture(fd);
	}

	// This method creates a walls.
	public static void addWall(float posX, float posY, float width, float height) {
		PolygonShape ps = new PolygonShape();
		ps.setAsBox(width, height);

		FixtureDef fd = new FixtureDef();
		fd.shape = ps;
		fd.density = 1.0f;
		fd.friction = 0.5f;

		BodyDef bd = new BodyDef();
		bd.type = BodyType.STATIC;
		bd.position.set(posX, posY);

		Utils.world.createBody(bd).createFixture(fd);
	}

	// Convert a JBox2D x coordinate to a JavaFX pixel x coordinate
	public static float pixelToBox2D(float pos) {
		pos = pos * PIXEL_2_METER;
		return pos;
	}

	// Convert a JBox2D x coordinate to a JavaFX pixel x coordinate
	public static Point2 pixelToBox2D(Point2 input) {
		input.x *= PIXEL_2_METER;
		input.y *= PIXEL_2_METER;
		return input;
	}

	public static float box2DToPixel(float pos) {
		pos = pos * METER_2_PIXEL;
		return pos;
	}

	/**
	 * converte
	 */
	public static float PIXEL_2_METER = 1f / 128f;

	public static float METER_2_PIXEL = 128f;

	public static void addLeftWall(TiledMap tiledMap) {
		PolygonShape ps = new PolygonShape();
		ps.setAsBox(0.5f, Utils.pixelToBox2D(tiledMap.mapHeight / 2f));

		FixtureDef fd = new FixtureDef();
		fd.shape = ps;

		BodyDef bd = new BodyDef();
		bd.type = BodyType.STATIC;

		// bordo basso dello schermo
		bd.position = new Vec2(-(Utils.pixelToBox2D(tiledMap.mapWidth / 2f) + 0.5f), 0.0f);

		world.createBody(bd).createFixture(fd);
	}

	public static void addRightWall(TiledMap tiledMap) {
		PolygonShape ps = new PolygonShape();
		ps.setAsBox(0.5f, Utils.pixelToBox2D(tiledMap.mapHeight / 2f));

		FixtureDef fd = new FixtureDef();
		fd.shape = ps;

		BodyDef bd = new BodyDef();
		bd.type = BodyType.STATIC;

		// bordo basso dello schermo
		bd.position = new Vec2((Utils.pixelToBox2D(tiledMap.mapWidth / 2f) + 0.5f), 0.0f);

		world.createBody(bd).createFixture(fd);

	}

	public static void addUpperWall(TiledMap tiledMap) {
		PolygonShape ps = new PolygonShape();
		ps.setAsBox(Utils.pixelToBox2D(tiledMap.mapWidth / 2f), 0.5f);

		FixtureDef fd = new FixtureDef();
		fd.shape = ps;

		BodyDef bd = new BodyDef();
		bd.type = BodyType.STATIC;

		// bordo basso dello schermo
		bd.position = new Vec2((Utils.pixelToBox2D(tiledMap.mapHeight / 2f) + 0.5f), 0.0f);

		world.createBody(bd).createFixture(fd);

	}

}

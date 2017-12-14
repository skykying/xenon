package com.abubusoft.xenon.game;

import java.util.Stack;

import org.abubu.argon.box2d.dynamics.Body;
import org.abubu.elio.collections.ObjectPool;
import org.abubu.elio.collections.PooledObject;
import org.abubu.elio.logger.ElioLogger;

public class GamePortalOperations {

	public GamePortalOperations() {
		stack = new Stack<>();
		pool = new OperationPool(10);

	}

	public enum OperationType {
		MOVE, REMOVE
	}

	public class Operation implements PooledObject {
		public OperationType type;
		public Body body;
		public float x;
		public float y;

		@Override
		public void initializePoolObject() {

		}

		@Override
		public void finalizePoolObject() {
			body = null;
			type = null;
		}
	}

	Stack<Operation> stack;

	OperationPool pool;

	public class OperationPool extends ObjectPool<Operation> {

		public OperationPool(int maxSize) {
			super(maxSize);
		}

		@Override
		protected Operation createPooledObject() {
			return new Operation();
		}

	}

	public void add(Body body, float posX, float posY) {
		Operation op = pool.newObject();

		op.type = OperationType.MOVE;
		op.body = body;
		op.x = posX;
		op.y = posY;

		stack.push(op);
	}

	public void execute() {
		Operation op;

		while (!stack.empty()) {
			op = stack.pop();

			switch (op.type) {
			case MOVE:
				ElioLogger.debug("++++++++ [%s, %s]", op.body.getPosition().x, op.body.getPosition().y);
				op.body.setTransform(op.x, op.y, op.body.getAngle());
				ElioLogger.debug("-------- [%s, %s]", op.body.getPosition().x, op.body.getPosition().y);
				break;
			case REMOVE:
				op.body.setActive(false);
				break;
			}

		}
	}

	public void removeObject(Body body) {
		Operation op = pool.newObject();

		op.type = OperationType.REMOVE;
		op.body = body;

		stack.push(op);

	}
}

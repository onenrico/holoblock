package me.onenrico.holoblock.utils;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import de.slikey.effectlib.effect.ImageEffect.Plane;

public class VectorUT {
	public static final Vector rotateAroundAxisX(Vector v, double angle) {
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		double y = v.getY() * cos - v.getZ() * sin;
		double z = v.getY() * sin + v.getZ() * cos;
		return v.setY(y).setZ(z);
	}

	public static final Vector rotateAroundAxisY(Vector v, double angle) {
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		double x = v.getX() * cos + v.getZ() * sin;
		double z = v.getX() * -sin + v.getZ() * cos;
		return v.setX(x).setZ(z);
	}

	public static final Vector rotateAroundAxisY(Vector v, float angle) {
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		double x = v.getX() * cos + v.getZ() * sin;
		double z = v.getX() * -sin + v.getZ() * cos;
		return v.setX(x).setZ(z);
	}

	public static final Vector rotateAroundAxisZ(Vector v, double angle) {
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		double x = v.getX() * cos - v.getY() * sin;
		double y = v.getX() * sin + v.getY() * cos;
		return v.setX(x).setY(y);
	}

	public static final Vector rotateVector(Vector v, double angleX, double angleY, double angleZ) {
		rotateAroundAxisX(v, angleX);
		rotateAroundAxisY(v, angleY);
		rotateAroundAxisZ(v, angleZ);
		return v;
	}

	public static final Vector rotateVector(Vector v, Location location) {
		return rotateVectorYZ(v, location.getYaw(), location.getPitch());
	}

	public static final Vector rotateVectorYZ(Vector v, float yawDegrees, float pitchDegrees) {
		double yaw = Math.toRadians(-1.0F * (yawDegrees + 90.0F));
		double pitch = Math.toRadians(-pitchDegrees);

		double cosYaw = Math.cos(yaw);
		double cosPitch = Math.cos(pitch);
		double sinYaw = Math.sin(yaw);
		double sinPitch = Math.sin(pitch);

		double initialX = v.getX();
		double initialY = v.getY();
		double x = initialX * cosPitch - initialY * sinPitch;
		double y = initialX * sinPitch + initialY * cosPitch;

		double initialZ = v.getZ();
		initialX = x;
		double z = initialZ * cosYaw - initialX * sinYaw;
		x = initialZ * sinYaw + initialX * cosYaw;

		return new Vector(x, y, z);
	}

	public static final Vector rotateVectorYX(Vector v, float yawDegrees, float pitchDegrees) {
		double yaw = Math.toRadians(-1.0F * yawDegrees);
		double pitch = pitchDegrees;

		double cosYaw = Math.cos(yaw);
		double cosPitch = Math.cos(pitch);
		double sinYaw = Math.sin(yaw);
		double sinPitch = Math.sin(pitch);

		double initialY = v.getY();
		double initialZ = v.getZ();
		double z = initialY * sinPitch - initialZ * cosPitch;
		double y = initialY * cosPitch + initialZ * sinPitch;

		initialZ = z;
		double initialX = v.getX();
		z = initialZ * cosYaw - initialX * sinYaw;
		double x = initialZ * sinYaw + initialX * cosYaw;

		return new Vector(x, y, z);
	}

	public static final double angleToXAxis(Vector vector) {
		return Math.atan2(vector.getX(), vector.getY());
	}

	public static Vector getBackVector(Location location) {
		float newZ = (float) (location.getZ() + 1.0D * Math.sin(Math.toRadians(location.getYaw() + 90.0F)));
		float newX = (float) (location.getX() + 1.0D * Math.cos(Math.toRadians(location.getYaw() + 90.0F)));
		return new Vector(newX - location.getX(), 0.0D, newZ - location.getZ());
	}

	public static double offset(Entity a, Entity b) {
		return offset(a.getLocation().toVector(), b.getLocation().toVector());
	}

	public static double offset(Location a, Location b) {
		return offset(a.toVector(), b.toVector());
	}

	public static double offset(Vector a, Vector b) {
		return a.subtract(b).length();
	}

	public static Vector planeRotation(Vector v, Plane plane, Vector angularVelocity, int rotationStep) {
		double rotX = 0.0D;
		double rotY = 0.0D;
		double rotZ = 0.0D;
		switch (plane) {
		case X:
			rotX = angularVelocity.getX() * rotationStep;
			break;
		case XY:
			rotY = angularVelocity.getY() * rotationStep;
			break;
		case XYZ:
			rotZ = angularVelocity.getZ() * rotationStep;
			break;
		case XZ:
			rotX = angularVelocity.getX() * rotationStep;
			rotY = angularVelocity.getY() * rotationStep;
			break;
		case Y:
			rotX = angularVelocity.getX() * rotationStep;
			rotZ = angularVelocity.getZ() * rotationStep;
			break;
		case YZ:
			rotX = angularVelocity.getX() * rotationStep;
			rotY = angularVelocity.getY() * rotationStep;
			rotZ = angularVelocity.getZ() * rotationStep;
			break;
		case Z:
			rotY = angularVelocity.getY() * rotationStep;
			rotZ = angularVelocity.getZ() * rotationStep;
		}
		return rotateVector(v, rotX, rotY, rotZ);
	}

	public static Vector calculateBezierPoint(float t, Vector p0, Vector p1, Vector p2, Vector p3) {
		float u = 1.0F - t;
		float tt = t * t;
		float uu = u * u;
		float uuu = uu * u;
		float ttt = tt * t;

		Vector p = p0.multiply(uuu);

		p.add(p1.multiply(3.0F * uu * t));

		p.add(p2.multiply(3.0F * u * tt));

		p.add(p3.multiply(ttt));
		return p;
	}

	public static Vector calculateBezierPoint(float t, Location p0, Location p1, Location p2, Location p3) {
		float u = 1.0F - t;
		float tt = t * t;
		float uu = u * u;
		float uuu = uu * u;
		float ttt = tt * t;

		Vector p = p0.toVector().multiply(uuu);

		p.add(p1.toVector().multiply(3.0F * uu * t));

		p.add(p2.toVector().multiply(3.0F * u * tt));

		p.add(p3.toVector().multiply(ttt));
		return p;
	}
}

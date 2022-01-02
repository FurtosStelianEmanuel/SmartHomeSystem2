/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package misc;

/**
 *
 * @author Manel
 */
public class Vector {

    private final double x;
    private final double y;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static Vector add(Vector a, Vector b) {
        return new Vector(a.getX() + b.x, a.getY() + b.y);
    }

    public static Vector subtract(Vector a, Vector b) {
        return new Vector(a.getX() - b.x, a.getY() - b.y);
    }

    public static Vector multiply(Vector a, Vector other) {
        return new Vector(a.getX() * other.x, a.getY() * other.y);
    }

    public static Vector multiply(Vector a, double scalar) {
        return new Vector(a.getX() * scalar, a.getY() * scalar);
    }

    public static Vector rotate(Vector vector, double angle) {
        double x2 = Math.cos(angle) * vector.getX() - Math.sin(angle) * vector.getY();
        double y2 = Math.sin(angle) * vector.getX() + Math.cos(angle) * vector.getY();

        return new Vector(x2, y2);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
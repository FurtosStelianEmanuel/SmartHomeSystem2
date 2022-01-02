/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem.animation.wallpapers.recursivetree;

import java.awt.Shape;
import java.awt.geom.Line2D;
import javafx.util.Pair;
import misc.Vector;
import smarthomesystem.animation.wallpapers.DrawnShape;

/**
 *
 * @author Manel
 */
public class Branch implements DrawnShape {

    private final Vector start;
    private final Vector end;
    private boolean grown;

    public Branch(double startX, double startY, double endX, double endY) {
        start = new Vector(startX, startY);
        end = new Vector(endX, endY);
    }

    public Branch(Vector start, Vector end) {
        this.start = start;
        this.end = end;
    }

    public Pair<Branch, Branch> grow(double angle, double growth) {
        Vector direction;
        grown = true;

        direction = Vector.subtract(end, start);
        direction = Vector.rotate(direction, angle);
        direction = Vector.multiply(direction, growth);
        Vector newEndA = Vector.add(end, direction);

        direction = Vector.subtract(end, start);
        direction = Vector.rotate(direction, -angle);
        direction = Vector.multiply(direction, growth);
        Vector newEndB = Vector.add(end, direction);

        return new Pair(new Branch(end, newEndA), new Branch(end, newEndB));
    }

    public Leaf growLeaf(double diameter) {
        return new Leaf(this, diameter);
    }

    @Override
    public Shape getShape() {
        return new Line2D.Double(getStart().getX(), getStart().getY(), getEnd().getX(), getEnd().getY());
    }

    public boolean isGrown() {
        return grown;
    }

    public Vector getStart() {
        return start;
    }

    public Vector getEnd() {
        return end;
    }
}
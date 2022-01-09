/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem.animation.wallpapers.recursivetree;

import java.awt.Color;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import misc.Vector;
import smarthomesystem.animation.wallpapers.DrawnShape;

/**
 *
 * @author Manel
 */
public class Leaf implements DrawnShape {

    private final Vector anchorPoint;
    private final double diameter;
    private Color color;
    private boolean hovered;
    private boolean touched;

    public Leaf(Branch rootBranch, double diameter) {
        this.diameter = diameter;
        anchorPoint = new Vector(rootBranch.getEnd().getX() - diameter / 2, rootBranch.getEnd().getY() - diameter / 2);
    }

    @Override
    public Shape getShape() {
        return new Ellipse2D.Double(anchorPoint.getX(), anchorPoint.getY(), diameter, diameter);
    }

    public Vector getAnchorPoint() {
        return anchorPoint;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public boolean isColored() {
        return color != null;
    }

    public void checkIfHovered(MouseEvent me) {
        hovered = isHovered(me);

        if (!hovered) {
            touched = false;
        }
    }

    public boolean isHovered() {
        return hovered;
    }

    public void touch() {
        touched = true;
    }

    public boolean isTouched() {
        return touched;
    }

    private boolean isHovered(MouseEvent me) {
        double x = anchorPoint.getX();
        double y = anchorPoint.getY();
        double distance = Math.sqrt((x - me.getX() + diameter / 2) * (x - me.getX() + diameter / 2) + (y - me.getY() + diameter / 2) * (y - me.getY() + diameter / 2));

        return distance <= diameter / 2;
    }
}

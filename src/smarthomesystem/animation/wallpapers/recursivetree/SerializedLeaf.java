/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem.animation.wallpapers.recursivetree;

/**
 *
 * @author Manel
 */
public class SerializedLeaf {

    public int index;
    public int red, green, blue, alpha;
    public boolean isColored;

    public SerializedLeaf() {

    }

    public SerializedLeaf(int index, Leaf leaf) {
        this.index = index;
        if (leaf.getColor() == null) {
            return;
        }

        this.red = leaf.getColor().getRed();
        this.green = leaf.getColor().getGreen();
        this.blue = leaf.getColor().getBlue();
        this.alpha = leaf.getColor().getAlpha();
        this.isColored = true;
    }
}
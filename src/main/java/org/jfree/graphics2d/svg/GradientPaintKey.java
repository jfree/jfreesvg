/**
 * (C)opyright 2013, by Object Refinery Limited.  All rights reserved.
 */
package org.jfree.graphics2d.svg;

import java.awt.GradientPaint;

/**
 * A wrapper for a GradientPaint that can be used as the key for a HashMap.
 * GradientPaint itself does not implement the equals() and hashCode() methods,
 * so it doesn't make an ideal key.
 */
public final class GradientPaintKey {

    private GradientPaint paint;
    
    /**
     * Creates a new instance.
     * 
     * @param paint  the paint (<code>null</code> not permitted). 
     */
    public GradientPaintKey(GradientPaint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.paint = paint;
    }
    
    /**
     * Returns the GradientPaint that was supplied to the constructor.
     * 
     * @return The GradientPaint (never <code>null</code>). 
     */
    public GradientPaint getPaint() {
        return this.paint;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof GradientPaintKey)) {
            return false;
        }
        GradientPaintKey that = (GradientPaintKey) obj;
        GradientPaint thisGP = this.paint;
        GradientPaint thatGP = that.getPaint();
        if (!thisGP.getColor1().equals(thatGP.getColor1())) {
            return false;
        }
        if (!thisGP.getColor2().equals(thatGP.getColor2())) {
            return false;
        }
        if (thisGP.getPoint1().equals(thatGP.getPoint1())) {
            return false;
        }
        if (thisGP.getPoint2().equals(thatGP.getPoint2())) {
            return false;
        }
        if (thisGP.getTransparency() != thatGP.getTransparency()) {
            return false;
        }
        if (thisGP.isCyclic() != thatGP.isCyclic()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + this.paint.getPoint1().hashCode();
        hash = 47 * hash + this.paint.getPoint2().hashCode();
        hash = 47 * hash + this.paint.getColor1().hashCode();
        hash = 47 * hash + this.paint.getColor2().hashCode();
        return hash;
    }

}

package ca.brocku.chinesecheckers.uiengine.visuals;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import ca.brocku.chinesecheckers.uiengine.Dimensions;
import ca.brocku.chinesecheckers.uiengine.PixelPosition;

/**
 * Author: Chris Kellendonk
 * Student #: 4810800
 * Date: 2/2/2014
 */
public class Visual {
    protected Visual parent;
    protected List<Visual> visuals = new CopyOnWriteArrayList<Visual>(); 		// Order matters
    protected TouchEventHandler handler = null;
    protected Dimensions dimensions;
    protected PixelPosition position;

    public Visual(float x, float y, float w, float h) {
        this(new PixelPosition(x, y), new Dimensions(w, h));
    }

    public Visual(PixelPosition position, Dimensions dimensions) {
        this.position = position;
        this.dimensions = dimensions;
    }

    /**
     * Add a child to this visual element
     * @param v The child to add to this.
     */
    public void addChild(Visual v) {
        if(v == null) { throw new RuntimeException("Can't add null child"); }
        if(v == this) { throw new RuntimeException("Can't add visual as child of its self."); }
        if(v.parent != null) { throw new RuntimeException("Child is already has a parent."); }

        // Add it
        v.setParent(this);
        visuals.add(v);
    }

    /**
     * Remove the child visual.
     * @param v The child to remove.
     */
    public void removeChild(Visual v) {
        visuals.remove(v);
    }

    /**
     * Remove a collection of children.
     * @param children  The children to remove.
     */
    public void removeChildren(Collection<Visual> children) {
        visuals.removeAll(children);
    }

    /**
     * Set the parent of this visual element in the hierarchy
     * @param v Set this instance parent to <code>v</code>.
     */
    protected void setParent(Visual v) {
        if(parent != null) { throw new RuntimeException("Parent can't be changed."); }
        if(v != null) {
            parent = v;

            this.position.addOffset(parent.position);
        }
    }

    /**
     * Did the (x,y) intersect with any part of this <code>Visual</code>.
     * @param x The x coordinate.
     * @param y The y coordinate
     * @return  True if it intersects, False otherwise.
     */
    protected boolean didIntersect(float x, float y) {
        return ( x >= this.position.getX() &&
                 x <= this.position.getX() + this.dimensions.getWidth() &&
                 y >= this.position.getY() &&
                 y <= this.position.getY() + this.dimensions.getHeight());
    }

    /**
     * Executed when drawing the onto the canvas.
     * @param c The canvas to draw to.
     */
    protected void onDraw(Canvas c) {}

    /**
     * Call when you want to draw to the canvas.
     * @param c The canvas to draw to.
     */
    @SuppressLint("WrongCall")
    final public void draw(Canvas c) {
        this.onDraw(c);

        for(Visual v : visuals) {
            v.draw(c);
        }
    }

    /**
     * Set the touch event handler for this visual element.
     * @param handler   The handler to fire.
     */
    public void setTouchEventHandler(TouchEventHandler handler) {
        this.handler = handler;
    }

    public void removeChildren() {
        this.visuals.clear();
    }

    /**
     * Send touch event to children.
     * It will not send the event to it's ancestors.
     *
     * @param e Event details.
     */
    public boolean sendTouchEvent(MotionEvent e) {
        boolean handled = false;

        ListIterator<Visual> iterator = visuals.listIterator(visuals.size());

        while(iterator.hasPrevious()) {
            Visual v = iterator.previous();

            if(v.didIntersect(e.getX(), e.getY())) {
                handled = v.sendTouchEvent(e);
                if(handled) {
                    break;
                }
            }
        }

        if(this.handler != null) {
            handled |= this.handler.onTouch(this, e); // Could be handled (depends on handler)
        }

        return handled; // Event not handled
    }

    /**
     * Handles touch events.
     */
    public interface TouchEventHandler {
        /**
         * Fired when a Visual element is touched within it's bounds.
         *
         * @param  v The Visual that was touched
         * @param  e The event.
         * @return   True to absorb the event and stop bubbling, or false to bubble.
         */
        public boolean onTouch(Visual v, MotionEvent e);
    }
}

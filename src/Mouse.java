import java.awt.event.*;
import javax.swing.event.MouseInputListener;

public class Mouse implements MouseInputListener {
    private int x, y, clicks, objectLoc;

    public int getClicks(){
        return clicks;
    }

    public void setClicks(int x){
        clicks = x;
    }

    public int getObjectLoc(){
        return objectLoc;
    }

    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public void mouseClicked(MouseEvent e) {
        x = (e.getX() / 50) * 50;
        y = (e.getY() / 50) * 50;
        objectLoc = (((y / 50) * 10) + (x / 50)) * 3;
        clicks++;
    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {

    }

    public void mouseReleased(MouseEvent e) {

    }

    public void mouseDragged(MouseEvent arg0) {

    }

    public void mouseMoved(MouseEvent arg0) {

    }

}

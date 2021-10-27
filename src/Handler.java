
import java.util.LinkedList;
import java.awt.Graphics;

public class Handler {
    LinkedList<GameObject> object = new LinkedList<GameObject>();

    public void tick() {
        int i = 0;
        GameObject tempObject = null;
        boolean error = false;
        while (i < object.size()) {
            do {
                try {
                    tempObject = object.get(i);
                    error = false;
                } catch (NullPointerException e) {
                    error = true;
                }
            } while(error);
            tempObject.tick();
            i++;
        }
    }

    public void render(Graphics g) {
        int i = 0;
        GameObject tempObject = null;
        boolean error = false;
        while (i < object.size()) {
            do {
                try {
                    tempObject = object.get(i);
                    error = false;
                } catch (NullPointerException e) {
                    error = true;
                }
            } while(error);
            tempObject.render(g);
            i++;
        }
    }

    public void addObject(GameObject object) {
        this.object.add(object);
    }

    public void replaceObject(int x, GameObject object) {
        this.object.remove(x);
        this.object.add(x, object);
    }

    public void reset() {
        for (int i = object.size(); i != 0; i--) {
            object.remove();
        }
    }

}

import java.awt.Color;
import java.awt.Graphics;

public class AiPath extends GameObject{
    Mouse mouse = new Mouse();
    public AiPath(int x, int y, ID id){
        super(x, y, id);
    }
    public void tick(){

    }

    public void render(Graphics g) {
        g.setColor(Color.magenta);
        g.fillRect(x, y, 50, 50);
    }
}
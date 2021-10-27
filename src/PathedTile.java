import java.awt.Color;
import java.awt.Graphics;

public class PathedTile extends GameObject{
    
    public PathedTile(int x, int y, ID id){
        super(x, y, id);
    }

    public void tick(){

    }

    public void render(Graphics g) {
        g.setColor(Color.blue);
        g.fillRect(x, y, 50, 50);
    }
}
import java.awt.Color;
import java.awt.Graphics;

public class Column extends GameObject{
    
    public Column(int x, int y, ID id){
        super(x, y, id);
    }

    public void tick(){

    }

    public void render(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(x, y, 5, 50);
    }
}
import static java.lang.System.out;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.image.BufferStrategy;
import java.lang.Math;

public class App extends Canvas implements Runnable {
    private int line = 0;
    private int WIDTH = 0;
    private int HEIGHT = 0;
    private int size = 100;
    private int bombCount = 30;
    private Thread thread;
    private boolean running = false;
    private Handler handler;
    private Mouse mouse = new Mouse();
    private Random rand = new Random();

    public App() {
        while (line != Math.sqrt(size)) {
            line++;
        }
        WIDTH = 50 * line;
        HEIGHT = 50 * line;
        handler = new Handler();
        new Window(WIDTH, HEIGHT, "MineMaze", this);
        this.addMouseListener(mouse);

        int f = 0;
        int d = 0;
        int g = 1;
        int h = 0;
        while (d != (size + 1)) {
            handler.addObject(new Tile(0 + (f * 50), 0 + (h * 50), ID.Tile));
            handler.addObject(new Column(0 + (f * 50), 0 + (h * 50), ID.Column));
            handler.addObject(new Row(0 + (f * 50), 0 + (h * 50), ID.Row));
            f++;
            d++;
            g++;
            if (g == line + 1) {
                g = 1;
                f = 0;
                h++;
            }
        }
        runMineMaze();
    }

    public synchronized void start() {
        thread = new Thread(this);
        thread.start();
        running = true;
    }

    public synchronized void stop() {
        try {
            thread.join();
            running = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= 1) {

                tick();
                delta--;
            }
            if (running)
                render();

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
            }
        }
        stop();
    }

    public void tick() {
        handler.tick();
    }

    public void render() {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();

        g.setColor(Color.green);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        handler.render(g);

        g.dispose();
        bs.show();
    }

    public static void main(String[] args) throws Exception {
        new App();
    }

    public void runMineMaze() {
        boolean gameRunning = true, isPlaying = true, validPath = false;
        int clicks = 0, x = 0, y = 0, playerMoves = 0, aiMoves = 0;
        int[] bombs = new int[100];
        int[] playerPath = new int[100];
        int[] aiPath = new int[100];
        mouse.setClicks(clicks);
        for (int i = 0; i != 100; i++) {
            bombs[i] = -1;
            playerPath[i] = -1;
            aiPath[i] = -1;
        }
        while (gameRunning) {
            out.println("Mode: bomb placement");
            while (clicks != bombCount) {
                while (clicks == mouse.getClicks()) {
                    out.print("");
                }
                clicks = mouse.getClicks();
                x = mouse.getX();
                y = mouse.getY();
                if (bombs[mouse.getObjectLoc() / 3] == -1) {
                    handler.replaceObject(mouse.getObjectLoc(), new ExposedBomb(x, y, ID.ExposedBomb));
                    bombs[mouse.getObjectLoc() / 3] = 3;
                } else {
                    out.println("Error: Bomb Placed On Bomb");
                    clicks--;
                    mouse.setClicks(clicks);
                }
            }
            for (int i = 0; i != 100; i++) {
                if (bombs[i] != -1)
                    handler.replaceObject(i * 3, new Bomb((i % 10) * 50, (i / 10) * 50, ID.Bomb));
            }
            out.println("Mode: player path");
            clicks = 0;
            mouse.setClicks(0);
            while (isPlaying) {
                validPath = false;
                while (clicks == mouse.getClicks()) {
                    out.print("");
                }
                clicks = mouse.getClicks();
                x = mouse.getX();
                y = mouse.getY();
                if (mouse.getObjectLoc() / 3 < 10) {
                    validPath = true;
                }
                if (!validPath && (mouse.getObjectLoc() / 3) - 10 > -1
                        && playerPath[(mouse.getObjectLoc() / 3) - 10] == 2) {
                    validPath = true;
                }
                if (!validPath && (mouse.getObjectLoc() / 3) + 10 < 100
                        && playerPath[(mouse.getObjectLoc() / 3) + 10] == 2) {
                    validPath = true;
                }
                if (!validPath && ((mouse.getObjectLoc() / 3) + 10) % 10 != 0
                        && playerPath[(mouse.getObjectLoc() / 3) - 1] == 2) {
                    validPath = true;
                }
                if (!validPath && ((mouse.getObjectLoc() / 3) + 1) % 10 != 0
                        && playerPath[(mouse.getObjectLoc() / 3) + 1] == 2) {
                    validPath = true;
                }
                if (validPath && bombs[mouse.getObjectLoc() / 3] != -1) {
                    validPath = false;
                    isPlaying = false;
                    out.println("Game End: Collision with Bomb");
                }
                if (validPath) {
                    handler.replaceObject(mouse.getObjectLoc(), new PathedTile(x, y, ID.PathedTile));
                    playerPath[mouse.getObjectLoc() / 3] = 2;
                    playerMoves++;
                }
                if (validPath && mouse.getObjectLoc() / 3 > 89) {
                    isPlaying = false;
                    out.println("Game End: Player Reached Safe Zone");
                }
                if (!validPath && isPlaying) {
                    out.println("Error: Path Not Connected");
                }
            }
            out.println("Mode: ai");
            int a = 0, b = 0, c = 0, d = 0, e = 100;
            int[] tempAiPath = new int[100];
            for (int i = 0; i != 100; i++) {
                tempAiPath[i] = -1;
            }
            boolean aiIsAlive = false;
            while (a != 100000) {
                aiMoves = 0;
                a++;
                aiIsAlive = true;
                b = rand.nextInt(10);
                aiMoves++;
                tempAiPath[b] = b;
                while (aiIsAlive) {
                    d++;
                    if (b < 90 && bombs[b + 10] == -1) {
                        b += 10;
                        tempAiPath[b] = 1;
                        d = 0;
                        aiMoves++;
                    }
                    if (b < 90 && bombs[b + 10] != -1) {
                        c = rand.nextInt(2 + 1);
                        switch (c) {
                        case 1:
                            if ((b + 10) % 10 != 0 && bombs[b - 1] == -1 && tempAiPath[b - 1] == -1) {
                                b--;
                                tempAiPath[b] = 1;
                                d = 0;
                                aiMoves++;
                            }
                            break;
                        case 2:
                            if ((b + 1) % 10 != 0 && bombs[b + 1] == -1 && tempAiPath[b + 1] == -1) {
                                b++;
                                tempAiPath[b] = 1;
                                d = 0;
                                aiMoves++;
                            }
                            break;
                        }
                    }
                    if (d == 10) {
                        for (int i = 0; i != 100; i++) {
                            tempAiPath[i] = -1;
                        }
                        d = 0;
                        b = 0;
                        c = 0;
                        aiIsAlive = false;
                    }
                    if (b > 89) {
                        aiIsAlive = false;
                        d = 0;
                        b = 0;
                        c = 0;
                        if (e > aiMoves) {
                            e = aiMoves;
                            for (int i = 0; i != 100; i++) {
                                aiPath[i] = tempAiPath[i];
                            }
                        }
                        for (int i = 0; i != 100; i++) {
                            tempAiPath[i] = -1;
                        }
                    }
                }
            }
            for (int i = 0; i != 100; i++) {
                tempAiPath[i] = -1;
            }
            a = 0;
            b = 0;
            c = 0;
            d = 0;
            aiMoves = 0;
            aiIsAlive = true;
            if (e == 100) {
                while (a != 100000) {
                    aiMoves = 0;
                    b = rand.nextInt(10);
                    tempAiPath[b] = 1;
                    aiMoves++;
                    aiIsAlive = true;
                    a++;
                    while (aiIsAlive) {
                        d++;
                        c = rand.nextInt(4 + 1);
                        switch (c) {
                        case 1:
                            if (b < 90 && bombs[b + 10] == -1 && tempAiPath[b + 10] == -1) {
                                b += 10;
                                tempAiPath[b] = 1;
                                d = 0;
                                aiMoves++;
                            }
                            break;
                        case 2:
                            if (b > 9 && bombs[b - 10] == -1 && tempAiPath[b - 10] == -1) {
                                b -= 10;
                                tempAiPath[b] = 1;
                                d = 0;
                                aiMoves++;
                            }
                            break;
                        case 3:
                            if ((b + 1) % 10 != 0 && bombs[b + 1] == -1 && tempAiPath[b + 1] == -1) {
                                b++;
                                tempAiPath[b] = 1;
                                d = 0;
                                aiMoves++;
                            }
                            break;
                        case 4:
                            if ((b + 10) % 10 != 0 && bombs[b - 1] == -1 && tempAiPath[b - 1] == -1) {
                                b--;
                                tempAiPath[b] = 1;
                                d = 0;
                                aiMoves++;
                            }
                            break;
                        }
                        if (d == 100) {
                            for (int i = 0; i != 100; i++) {
                                tempAiPath[i] = -1;
                            }
                            d = 0;
                            b = 0;
                            c = 0;
                            aiIsAlive = false;
                        }
                        if (b > 89) {
                            aiIsAlive = false;
                            d = 0;
                            b = 0;
                            c = 0;
                            if (e > aiMoves) {
                                e = aiMoves;
                                for (int i = 0; i != 100; i++) {
                                    aiPath[i] = tempAiPath[i];
                                }
                            }
                            for (int i = 0; i != 100; i++) {
                                tempAiPath[i] = -1;
                            }
                        }
                    }
                    
                }
            }
            out.println("Mode: Game Complete Rendering All");
            for (int i = 0; i != 100; i++) {
                if (bombs[i] != -1) {
                    handler.replaceObject(i * 3, new ExposedBomb((i % 10) * 50, (i / 10) * 50, ID.ExposedBomb));
                }
                if (aiPath[i] != -1) {
                    handler.replaceObject(i * 3, new AiPath((i % 10) * 50, (i / 10) * 50, ID.AiPath));
                }
            }
            aiMoves = e;
            gameRunning = false;
            out.println("ai Movements: " + aiMoves);
            out.println("player Movements: " + playerMoves);
            try {
                TimeUnit.SECONDS.sleep(15);
            } catch (InterruptedException e1) {

            }
            handler.reset();
            int f = 0;
            int j = 0;
            int g = 1;
            int h = 0;
            while (j != (size + 1)) {
                handler.addObject(new Tile(0 + (f * 50), 0 + (h * 50), ID.Tile));
                handler.addObject(new Column(0 + (f * 50), 0 + (h * 50), ID.Column));
                handler.addObject(new Row(0 + (f * 50), 0 + (h * 50), ID.Row));
                f++;
                j++;
                g++;
                if (g == line + 1) {
                    g = 1;
                    f = 0;
                    h++;
                }
            }
            runMineMaze();
        }
    }
}

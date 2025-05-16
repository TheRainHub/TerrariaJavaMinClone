package engine;

public class GameLoop {
    private boolean running;
    
    public GameLoop() {
        this.running = false;
    }
    
    public void start() {
        running = true;
        // Game loop implementation will go here
    }
    
    public void stop() {
        running = false;
    }
} 
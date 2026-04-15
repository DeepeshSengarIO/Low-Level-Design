package creational_patterns;

public class singleton {
    static class Logger{

        private static Logger instance;
        private Logger(){}

        public static Logger getInstance(){
            if (instance == null) {
                instance = new Logger();
            }
            return instance;
        }

        public void query(String operation) {
            // logger operations
        }
    }

    public static void main(String[] args) {
        Logger logger = Logger.getInstance();
        logger.query("This is a trace level log");
    }
}

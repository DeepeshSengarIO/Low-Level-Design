package creational_patterns;

public class factory {
    
    interface Notification{
        void send(String message);
    }

    static class EmailNotification implements Notification{
        @Override
        public void send(String message) {
            System.out.println("Email says: "+message);
        }
    }

    static class PhoneNotification implements Notification{
        @Override
        public void send(String message) {
            System.out.println("Phone says: "+message);
        }
    }

    class NotificationFactory{
        public static Notification create(String type){
            if (type == "email") {
                return new EmailNotification();
            }else if(type == "phone"){
                return new PhoneNotification();
            }
            throw new IllegalArgumentException("Unknown Type");
        }
    }

    // usage
    public static void main(String[] args) {
        Notification nf_email = NotificationFactory.create("email");
        nf_email.send("Hola!"); // Email says: Hola!
    }
}

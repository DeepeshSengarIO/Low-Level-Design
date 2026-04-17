package behavioral_patterns;

public class strategy {
    interface PaymentStrategy{
        boolean pay(double amount);
    }

    static class CreditcardPayment implements PaymentStrategy{

        private String cardNumber;

        public CreditcardPayment(String cardNumber){
            this.cardNumber = cardNumber;
        }

        @Override
        public boolean pay(double amount) {
            // payment logic
            System.out.println("Paid amout with Credit card amount: " + amount);
            return true;
        }
        
    }

    static class PayPalPayment implements PaymentStrategy {
        private String email;

        public PayPalPayment(String email) {
            this.email = email;
        }

        public boolean pay(double amount) {
            // PayPal processing logic
            System.out.println("Paid " + amount + " with PayPal");
            return true;
        }
    }

    static class ShoppingCart{
        private PaymentStrategy paymentStrategy;
        public void setPaymentStratgey(PaymentStrategy strategy){
            this.paymentStrategy = strategy;
        }
        public boolean makePayment(double amount){
            return paymentStrategy.pay(amount);
        }
    }

    public static void main(String[] args) {
        ShoppingCart cart = new ShoppingCart();
        cart.setPaymentStratgey(new CreditcardPayment("1234-4567"));
        cart.makePayment(23.0);

        cart.setPaymentStratgey(new PayPalPayment("asb@gmail.com"));
        cart.makePayment(12.7);
    }
}

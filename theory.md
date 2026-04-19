## 1. OOP Concepts
---

### 1.1 Encapsulation

Definition: Restricting direct access to an object's data (state) and exposing only necessary methods to interact with it.

Why use: Data security, integrity, and controlled access.

When to use: When you have sensitive data or logic that should not be manipulated from outside (e.g., Bank balance, User password).

When NOT to use: Simple DTOs (Data Transfer Objects) where you are just carrying data from point A to point B. Adding getters/setters here is just "boilerplate noise."

How to use: Declare fields `private`. Only expose what is absolutely necessary via public methods (getters/setters). Add input validation inside your "Setter" methods.

Example: A `Wallet` class where you cannot set the balance directly (wallet.balance = 100 is bad), but use `wallet.deposit(amount)` (good).

```java
class Wallet{
    private long balance;
    public synchronized long getBalance(){ return balance; }
    public synchronized boolean deposit(long amount){
        if(amount<0) return false;
        balance+=amount;
        return true;
    }
    public synchronized boolean withdraw(long amount){
        if(amount<0 || amount>balance) return false;
        balance-=amount;
        return true;
    }
}
```

### 1.2 Abstraction

Definition: Exposing only essential features while hiding the complex implementation logic.

Why use: Reduces cognitive load for the user of your class; decouples the "what" from the "how."

When to use: When you have a complex system that changes frequently (e.g., payment gateways, database drivers).

When NOT to use: If the system is simple and unlikely to change, introducing abstractions adds unnecessary files and complexity (YAGNI).

How to use: Use `interface` or `abstract` classes to define the "contract" (the what). Let child classes handle the implementation (the how).

Example: A `PaymentProcessor` interface with a `process()` method. The user doesn't care if it's Stripe or PayPal.

```java
interface PaymentProcessor{
    boolean process(long amount);
}
```


### 1.3 Inheritance

Definition: Creating a new class based on an existing class to inherit properties and behaviors.

Why use: Promotes code reusability and establishes a clear hierarchy.

When to use: When there is a clear "Is-A" relationship (e.g., A Car is a Vehicle).

When NOT to use: When you are using it just to share code between unrelated classes. Never use inheritance for "Has-A" relationships. (Use Composition instead).

How to use: Use the `extends` keyword. Put shared, generic logic in the parent class; use child classes only for specific, specialized logic.

Example: A `BaseWalletPayment` (using the **Template Method Pattern**) that handles core wallet interaction, extended by specific providers.

```java
abstract class BaseWalletPayment implements PaymentProcessor {
    protected final Wallet wallet;

    public BaseWalletPayment(Wallet wallet) { this.wallet = wallet; }

    @Override
    public final boolean process(long amount) {
        System.out.println("\n[System] Checking " + getProviderName() + "...");
        if (validate(amount) && wallet.withdraw(amount)) {
            onSuccess(amount);
            return true;
        }
        System.out.println("[System] " + getProviderName() + " failed.");
        return false;
    }

    protected abstract String getProviderName();
    protected abstract boolean validate(long amount);
    protected abstract void onSuccess(long amount);
}
```

### 1.4 Polymorphism
Definition: The ability of an object to take many forms; same method call acts differently based on the object.

Why use: Allows you to write code that is extensible; you can add new subclasses without changing the main client code.

When to use: When you have different objects that share a similar behavior but perform it differently (e.g., calculateArea() for Circle and Square).

When NOT to use: When the "different behaviors" are fundamentally different and shouldn't be grouped, or if you end up with too many `instanceof` checks (which defeats the purpose).

How to use: Use the parent reference (Interface/Abstract class) to hold a child instance. Override methods in the child class using `@Override`.

```java
// Implementation of various payment methods
class PaypalPayment extends BaseWalletPayment {
    public PaypalPayment(Wallet wallet) { super(wallet); }
    @Override protected String getProviderName() { return "PayPal"; }
    @Override protected boolean validate(long amount) { return amount > 0; }
    @Override protected void onSuccess(long amount) { System.out.println("[PayPal] Receipt emailed."); }
}

class VenmoPayment extends BaseWalletPayment {
    public VenmoPayment(Wallet wallet) { super(wallet); }
    @Override protected String getProviderName() { return "Venmo"; }
    @Override protected boolean validate(long amount) { return amount < 50000; } // Limit $500
    @Override protected void onSuccess(long amount) { System.out.println("[Venmo] Shared to feed."); }
}

class CreditCardPayment implements PaymentProcessor {
    @Override
    public boolean process(long amount) {
        System.out.println("\n[Credit Card] Processing directly via Bank Gateway...");
        System.out.println("[Credit Card] Transaction Approved.");
        return true;
    }
}

// THE CLIENT: Demonstrating Polymorphism
class PaymentApp {
    public static void main(String[] args) {
        // Setup state
        Wallet myWallet = new Wallet();
        myWallet.deposit(20000); // $200.00

        // POLYMORPHISM: Storing different implementations in a single list
        List<PaymentProcessor> userPaymentMethods = new ArrayList<>();
        userPaymentMethods.add(new PaypalPayment(myWallet));
        userPaymentMethods.add(new VenmoPayment(myWallet));
        userPaymentMethods.add(new CreditCardPayment());

        long purchaseAmount = 5000; // $50.00

        System.out.println("--- Starting Bulk Processing ---");
        
        // POLYMORPHISM IN ACTION: 
        // The code doesn't know (or care) which specific processor it is using.
        // It just calls .process() and the correct version runs.
        for (PaymentProcessor method : userPaymentMethods) {
            method.process(purchaseAmount);
        }

        System.out.println("\nFinal Wallet Balance: " + myWallet.getBalance());
    }
}
```

### 1.5 Composition vs. Inheritance

Definition: 
- **Inheritance (Is-A):** Subclassing to reuse code (e.g., `PaypalPayment` is a `BaseWalletPayment`).
- **Composition (Has-A):** Plugging components together (e.g., `PaypalPayment` has a `Wallet`).

Why: Composition is more flexible. It prevents "Fragile Base Class" issues where changing the parent breaks all children.

Example:
```java
// INHERITANCE: Rigid. Paypal IS a Wallet (Wait, that sounds wrong!)
class PaypalPaymentInherited extends Wallet { ... } 

// COMPOSITION: Flexible. Paypal HAS a Wallet to store funds.
class PaypalPaymentComposed {
    private Wallet wallet; 
}
```

### 1.6 Coupling and Cohesion

Definition:
- **Cohesion:** How focused a single module/class is. (High = Good).
- **Coupling:** How much one class knows about another. (Low = Good).

Example:
- **Low Cohesion (Bad):** A `Wallet` class that also sends emails and connects to a DB.
- **High Cohesion (Good):** A `Wallet` that only manages balance.
- **High Coupling (Bad):** `PaymentService` explicitly creating `new PaypalPayment()`.
- **Low Coupling (Good):** `PaymentService` receiving a `PaymentProcessor` interface via constructor.

### 1.7 Few Tips
1. **Code to Interface, not Implementation:** Always use `List<String> list = new ArrayList<>()` instead of `ArrayList<String> list = new ArrayList<>()`.
2. **YAGNI (You Ain't Gonna Need It):** Don't create an abstraction if you're only ever going to have one implementation.
3. **Composition Over Inheritance:** If you're unsure, choose composition. It's almost always easier to maintain.

## 2. SOLID Principles
---
The SOLID principles are the "Rules of Engagement" for writing maintainable software. They help you move from "Code that Works" to "Code that Lasts."

### 2.1 S - Single Responsibility Principle (SRP)
**Definition:** A class should have one, and only one, reason to change.

**The Scenario:** You are building a `NotificationService`.
- **Bad:** One class that fetches user preferences, formats the HTML, sends the email via SMTP, and logs errors to a DB. If the DB schema changes OR the SMTP provider changes, this class breaks.
- **Good:** Split into `TemplateEngine` (formatting), `NotificationSender` (delivery), and `NotificationRepo` (persistence).

```java
// High Cohesion: Each class does one thing.
class NotificationFormatter {
    public String format(String template, Map<String, String> data) { return "Formatted Msg"; }
}
class EmailSender {
    public void send(String to, String body) { /* SMTP Logic */ }
}
```

### 2.2 O - Open/Closed Principle (OCP)
**Definition:** Software entities should be open for extension, but closed for modification.

**The Scenario:** Adding "SMS" and "Slack" notifications.
- **Bad:** Using a `switch` statement inside `NotificationService`. Every time you add a new channel, you have to modify and re-test the core service.
- **Good:** Use an interface. To add Slack, you just create a new class `SlackNotification` that implements `MessageSender`.

```java
interface MessageSender {
    void sendMessage(String message);
}

// Open for extension: Just add a new class. 
// Core logic remains "Closed" for modification.
class SlackSender implements MessageSender {
    public void sendMessage(String msg) { /* Slack API logic */ }
}
```

### 2.3 L - Liskov Substitution Principle (LSP)
**Definition:** Objects of a superclass should be replaceable with objects of its subclasses without breaking the application.

**The Scenario:** Handling "In-App" notifications vs "Push" notifications.
- **Bad:** You have a `Notification` base class with a `setPhoneNumber()` method. But `InAppNotification` doesn't use phone numbers, so it throws a `NotImplementedException`. This breaks the calling code.
- **Good:** Only put methods in the base class that apply to *all* subclasses. Move phone-specific logic to a `Contactable` interface.

**Key Takeaway:** If a subclass throws an exception for a method the parent supports, you're likely violating LSP.

### 2.4 I - Interface Segregation Principle (ISP)
**Definition:** Clients should not be forced to depend on methods they do not use.

**The Scenario:** A massive `SmartNotification` interface.
- **Bad:** An interface with `sendEmail()`, `sendSMS()`, and `sendPush()`. A `LegacyEmailService` implementation is now forced to provide empty/dummy methods for SMS and Push.
- **Good:** Break the big interface into smaller, specific ones: `EmailService`, `SMSService`.

```java
// Better: Small, focused interfaces
interface EmailService { void sendEmail(); }
interface SmsService { void sendSms(); }

class EmailOnlyProvider implements EmailService {
    public void sendEmail() { /* Logic */ }
}
```

### 2.5 D - Dependency Inversion Principle (DIP)
**Definition:** High-level modules should not depend on low-level modules. Both should depend on abstractions.

**The Scenario:** Connecting your service to a provider like Twilio or SendGrid.
- **Bad:** `NotificationService` has a field `private TwilioSmsProvider provider = new TwilioSmsProvider();`. This is "Hard-coded" dependency. If Twilio goes down and you want to swap to MessageBird, you have to rewrite the service.
- **Good:** `NotificationService` depends on a `SmsProvider` interface. You "inject" the specific implementation at runtime.

```java
class NotificationService {
    private final MessageSender sender;

    // Dependency Injection: We depend on the abstraction (Interface)
    public NotificationService(MessageSender sender) {
        this.sender = sender;
    }

    public void notifyUser(String msg) {
        sender.sendMessage(msg);
    }
}
```

---

### Summary for Interviews
| Principle | Core Idea | Spot it by looking for... |
| :--- | :--- | :--- |
| **S**RP | Single Responsibility | Classes with 1000+ lines or many unrelated methods. |
| **O**CP | Open for Extension | Giant `switch` or `if-else` blocks for types. |
| **L**SP | Liskov Substitution | `if (obj instanceof X)` or `Throw NotImplementedException`. |
| **I**SP | Interface Segregation | Classes implementing methods with `// do nothing`. |
| **D**IP | Dependency Inversion | Using the `new` keyword for dependencies inside a constructor. |

## 3. Design Patterns
---
Design patterns are reusable solutions to common problems. They are divided into Creational, Structural, and Behavioral.

### 3.1 Creational Patterns (Object Creation)

#### **Factory Method**
- **Intent:** Define an interface for creating an object, but let subclasses decide which class to instantiate.
- **Senior Insight:** Use this to decouple the *user* of a class from the *concrete implementation*. If you are writing a `NotificationService`, the service shouldn't know how to "new up" an `SmsNotification`.
- **Code Smell:** A giant `switch` statement in the client code to decide which object to create.

```java
interface Notification {
  void send(String message);
}

class EmailNotification implements Notification {
  public void send(String message) {
    // Email sending logic
  }
}

class SMSNotification implements Notification {
  public void send(String message) {
    // SMS sending logic
  }
}

class NotificationFactory {
  public static Notification create(String type) {
    if (type.equals("email")) {
      return new EmailNotification();
    } else if (type.equals("sms")) {
      return new SMSNotification();
    }
    throw new IllegalArgumentException("Unknown type");
  }
}

// Usage
Notification notif = NotificationFactory.create("email");
notif.send("Hello");
```

#### **Builder**
- **Intent:** Separate the construction of a complex object from its representation.
- **Senior Insight:** Essential for **Immutability**. Instead of a constructor with 10 parameters (Telescoping Constructor), the Builder provides a fluent API.
- **When to use:** When an object has many optional fields or configurations (e.g., an HTTP Client or a Database Connection string).

```java
class HttpRequest {
  // Senior Tip: Use final fields for immutability
  private final String url;
  private final String method;
  private final Map<String, String> headers;
  private final String body;

  private HttpRequest(Builder builder) {
    this.url = builder.url;
    this.method = builder.method;
    this.headers = builder.headers;
    this.body = builder.body;
  }

  public static class Builder {
    private String url;
    private String method;
    private Map<String, String> headers = new HashMap<>();
    private String body;

    public Builder url(String url) {
      this.url = url;
      return this;
    }

    public Builder method(String method) {
      this.method = method;
      return this;
    }

    public Builder header(String key, String value) {
      this.headers.put(key, value);
      return this;
    }

    public Builder body(String body) {
      this.body = body;
      return this;
    }

    public HttpRequest build() {
      if (this.url == null) {
        throw new IllegalStateException("URL is required");
      }
      return new HttpRequest(this);
    }
  }
}

// Usage
HttpRequest request = new HttpRequest.Builder()
  .url("https://api.example.com")
  .method("POST")
  .header("Content-Type", "application/json")
  .body("{\"key\": \"value\"}")
  .build();
```

#### **Singleton (Thread-Safe)**
- **Intent:** Ensure a class has only one instance and provide a global point of access.
- **Senior Insight:** Be careful! It's often considered an **anti-pattern** because it introduces global state and makes unit testing difficult (hidden dependencies). If you use it, use the **Bill Pugh Singleton** (inner static class) or an **Enum** for thread-safety.

```java
class DatabaseConnection {
  private DatabaseConnection() {
    // Private constructor prevents external instantiation
  }

  // Bill Pugh Singleton: Thread-safe, lazy-loaded, no synchronization overhead.
  private static class Holder {
    private static final DatabaseConnection INSTANCE = new DatabaseConnection();
  }

  public static DatabaseConnection getInstance() {
    return Holder.INSTANCE;
  }

  public void query(String sql) {
    // Database operations
  }
}

// Usage
DatabaseConnection db = DatabaseConnection.getInstance();
db.query("SELECT * FROM users");
```

### 3.2 Structural Patterns (Object Composition)

#### **Decorator**
- **Intent:** Attach additional responsibilities to an object dynamically.
- **Senior Insight:** This is the primary alternative to Inheritance. Use it to "wrap" behavior (e.g., adding `Logging`, `Encryption`, or `Caching` to a `FileStream`).
- **SOLID link:** Perfectly illustrates **OCP** (Open for Extension, Closed for Modification).

```java
interface DataSource {
  void writeData(String data);
  String readData();
}

class FileDataSource implements DataSource {
  private String filename;

  public FileDataSource(String filename) {
    this.filename = filename;
  }

  public void writeData(String data) {
    // Write to file
  }

  public String readData() {
    // Read from file
    return "data from file";
  }
}

class EncryptionDecorator implements DataSource {
  private DataSource wrapped;

  public EncryptionDecorator(DataSource source) {
    this.wrapped = source;
  }

  public void writeData(String data) {
    String encrypted = encrypt(data);
    wrapped.writeData(encrypted);  // Delegate to wrapped object
  }

  public String readData() {
    String data = wrapped.readData();
    return decrypt(data);
  }

  private String encrypt(String data) {
    return "encrypted:" + data;
  }

  private String decrypt(String data) {
    return data.replace("encrypted:", "");
  }
}

class CompressionDecorator implements DataSource {
  private DataSource wrapped;

  public CompressionDecorator(DataSource source) {
    this.wrapped = source;
  }

  public void writeData(String data) {
    String compressed = compress(data);
    wrapped.writeData(compressed);  // Delegate to wrapped object
  }

  public String readData() {
    String data = wrapped.readData();
    return decompress(data);
  }

  private String compress(String data) {
    return "compressed:" + data;
  }

  private String decompress(String data) {
    return data.replace("compressed:", "");
  }
}

// Usage
DataSource source = new FileDataSource("data.txt");
source = new EncryptionDecorator(source);
source = new CompressionDecorator(source);
source.writeData("sensitive info");
// Data gets compressed, then encrypted, then written to file
```

#### **Facade**
- **Intent:** Provide a unified interface to a set of interfaces in a subsystem.
- **Senior Insight:** Use this to reduce **Coupling**. It acts as a gatekeeper to a complex internal system. Your `Game` class in Tic-Tac-Toe is a Facade because the UI only talks to `game.play()`, not the underlying `Board`, `WinningLogic`, and `Player` states.

```java
enum GameState {
  IN_PROGRESS,
  WON,
  DRAW
}

class Board {
  public boolean placeMark(int row, int col, String mark) {
    // Place mark logic
    return true;
  }

  public boolean checkWin(int row, int col) {
    // Check win logic
    return false;
  }

  public boolean isFull() {
    // Check if board is full
    return false;
  }
}

class Player {
  private String mark;

  public Player(String mark) {
    this.mark = mark;
  }

  public String getMark() {
    return mark;
  }
}

class Game {
  private Board board;
  private Player playerX;
  private Player playerO;
  private Player currentPlayer;
  private GameState state;

  public Game() {
    this.board = new Board();
    this.playerX = new Player("X");
    this.playerO = new Player("O");
    this.currentPlayer = playerX;
    this.state = GameState.IN_PROGRESS;
  }

  public boolean makeMove(int row, int col) {
    // Coordinates board, player, and state logic
    // Caller doesn't need to understand internal details
    if (state != GameState.IN_PROGRESS) return false;
    if (!board.placeMark(row, col, currentPlayer.getMark())) return false;

    if (board.checkWin(row, col)) {
      state = GameState.WON;
    } else if (board.isFull()) {
      state = GameState.DRAW;
    } else {
      currentPlayer = (currentPlayer == playerX) ? playerO : playerX;
    }
    return true;
  }
}

// Usage - simple interface hides all the coordination
Game game = new Game();
game.makeMove(0, 0);
game.makeMove(1, 1);
```

### 3.3 Behavioral Patterns (Object Interaction)

#### **Strategy**
- **Intent:** Define a family of algorithms, encapsulate each one, and make them interchangeable.
- **Senior Insight:** This is the "kill switch" for `if/else` logic. If you have different ways to calculate tax or process payments, encapsulate the logic in a Strategy.
- **Interviewer Tip:** Often used interchangeably with **State**, but Strategy is usually selected by the *client*, whereas State is managed by the *object itself*.

```java
interface PaymentStrategy {
  boolean pay(double amount);
}

class CreditCardPayment implements PaymentStrategy {
  private String cardNumber;

  public CreditCardPayment(String cardNumber) {
    this.cardNumber = cardNumber;
  }

  public boolean pay(double amount) {
    // Credit card processing logic
    System.out.println("Paid " + amount + " with credit card");
    return true;
  }
}

class PayPalPayment implements PaymentStrategy {
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

class ShoppingCart {
  private PaymentStrategy paymentStrategy;

  public void setPaymentStrategy(PaymentStrategy strategy) {
    this.paymentStrategy = strategy;
  }

  public void checkout(double amount) {
    paymentStrategy.pay(amount);
  }
}

// Usage
ShoppingCart cart = new ShoppingCart();

cart.setPaymentStrategy(new CreditCardPayment("1234-5678"));
cart.checkout(100.00);

cart.setPaymentStrategy(new PayPalPayment("user@example.com"));
cart.checkout(50.00);
```

#### **Observer**
- **Intent:** Define a one-to-many dependency so that when one object changes state, all its dependents are notified.
- **Senior Insight:** This is the foundation of **Event-Driven Architectures**. It promotes **Low Coupling** because the Subject doesn't need to know the concrete types of its observers.

```java
interface Observer {
  void update(String symbol, double price);
}

interface Subject {
  void attach(Observer observer);
  void detach(Observer observer);
  void notifyObservers();
}

class Stock implements Subject {
  private List<Observer> observers = new ArrayList<>();
  private String symbol;
  private double price;

  public Stock(String symbol) {
    this.symbol = symbol;
  }

  public void attach(Observer observer) {
    observers.add(observer);
  }

  public void detach(Observer observer) {
    observers.remove(observer);
  }

  public void setPrice(double price) {
    this.price = price;
    notifyObservers();  // Price changed, tell everyone
  }

  public void notifyObservers() {
    for (Observer observer : observers) {
      observer.update(symbol, price);
    }
  }
}

class PriceDisplay implements Observer {
  public void update(String symbol, double price) {
    System.out.println("Display updated: " + symbol + " = $" + price);
  }
}

class PriceAlert implements Observer {
  private double threshold;

  public PriceAlert(double threshold) {
    this.threshold = threshold;
  }

  public void update(String symbol, double price) {
    if (price > threshold) {
      System.out.println("Alert! " + symbol + " exceeded $" + threshold);
    }
  }
}

// Usage
Stock stock = new Stock("AAPL");

PriceDisplay display = new PriceDisplay();
PriceAlert alert = new PriceAlert(150.00);

stock.attach(display);
stock.attach(alert);

stock.setPrice(145.00);  // Both observers get notified
stock.setPrice(155.00);  // Both observers get notified
```

#### **State Pattern**
- **Intent:** Allow an object to alter its behavior when its internal state changes.
- **Senior Insight:** Vital for workflows like Order Processing (`PENDING` -> `PAID` -> `SHIPPED`). Instead of having a class full of `if (status == PENDING)`, you delegate behavior to a `State` object.

```java
class Order {
    private OrderState state;

    public Order() { this.state = new PendingState(); }
    public void setState(OrderState state) { this.state = state; }

    public void next() { state.next(this); }
    public void cancel() { state.cancel(this); }
}

interface OrderState {
    void next(Order order);
    void cancel(Order order);
}

class PendingState implements OrderState {
    public void next(Order order) {
        System.out.println("Payment received. Moving to PAID.");
        order.setState(new PaidState());
    }
    public void cancel(Order order) {
        System.out.println("Order cancelled from Pending.");
    }
}

class PaidState implements OrderState {
    public void next(Order order) { 
        System.out.println("Shipping order...");
        order.setState(new ShippedState()); 
    }
    public void cancel(Order order) { 
        System.out.println("Refunding payment and cancelling.");
    }
}
```

---

### Cheat Sheet: Choosing the Pattern
| If you see... | Use this Pattern |
| :--- | :--- |
| Complex object creation with optional fields | **Builder** |
| Need to swap algorithms at runtime | **Strategy** |
| One event needs to trigger multiple actions | **Observer** |
| Adding features (logging/caching) without changing code | **Decorator** |
| Object behavior changes drastically based on status | **State** |
| Hiding a messy, complex subsystem | **Facade** |
| Need to decouple code from concrete classes | **Factory** |

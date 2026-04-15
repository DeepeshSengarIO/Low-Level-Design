# Creational
---
Creational patterns control how objects get created. They hide construction details, let you swap implementations, and keep your code from being tightly coupled to specific classes.

### Factory

be careful while bringing this up as this might seemed as overengineering
"support different notification types" or "handle multiple payment methods."

### Builder

This shows up when designing things like HTTP requests, database queries, or configuration objects. Instead of a constructor with ten parameters where half are null, you build the object incrementally.
if only 2-4 paramenters then contructor works fine, use builder when bigger than these parameters.

### Singleton

Singleton ensures only one instance of this class exists.
Use it when you need exactly one shared resource like a configuration manager, connection pool, or logger.
only use when you want a single shared instance all over

# Stuctural
---
Structural patterns deal with how objects connect to each other. They help you build flexible relationships between classes without creating tight coupling or messy dependencies.

### Decorator

A decorator adds behavior to an object without changing its class. Use it when you need to layer on extra functionality at runtime.
You might need this when the requirements say things like "add logging to specific operations" or "encrypt certain messages."

If you see words like "optional features," "stack behaviors," or "combine multiple enhancements," think Decorator.

Each decorator adds one piece of functionality. You can stack them in any order and add or remove them without touching the base class or other decorators, though in real systems order often affects behavior.

If the behavior depends on runtime conditions, choose Decorator; if it’s a predefined type difference, choose Subclass.

### Facade

A facade is just a coordinator class that hides complexity. You're probably already building facades in every LLD interview without calling them that. Your Game class in Tic Tac Toe? That's a facade. 







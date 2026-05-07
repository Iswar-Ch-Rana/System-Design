# How to Approach a Low-Level Design (LLD) Interview

---

## 🎯 The 5-Step Framework (Use This Every Time)

```
Step 1: Clarify Requirements        (2-3 min)
Step 2: Identify Core Entities      (3-4 min)
Step 3: Define Relationships        (2-3 min)
Step 4: Apply Design Patterns       (5-7 min)
Step 5: Write Clean Code            (20-25 min)
```

---

## Step 1: Clarify Requirements

Always ask before coding:

| Question Type | Example                                            |
|---------------|----------------------------------------------------|
| Scope         | "Should I design the full system or just booking?" |
| Users/Actors  | "Who uses this? Admin, Customer, Driver?"          |
| Core Features | "What are the top 3 use cases to focus on?"        |
| Constraints   | "Multi-threaded? Real-time? Scale?"                |
| Edge Cases    | "What happens on failure? Concurrent access?"      |

> **Tip:** Don't jump to code. Spend 2-3 minutes here — interviewers love it.

---

## Step 2: Identify Core Entities (Classes)

Ask yourself: **"What are the nouns in the problem?"**

Example — Parking Lot:

```
Vehicle, ParkingSpot, ParkingLot, Ticket, Payment, Gate
```

Example — BookMyShow:

```
Movie, Theatre, Screen, Show, Seat, Booking, User, Payment
```

For each entity, define:

- **Attributes** (fields)
- **Behaviors** (methods)
- **State** (enum values)

---

## Step 3: Define Relationships

| Relationship | Meaning                   | Example                       |
|--------------|---------------------------|-------------------------------|
| Has-A        | Composition / Aggregation | ParkingLot HAS ParkingSpots   |
| Is-A         | Inheritance               | Car IS-A Vehicle              |
| Uses-A       | Dependency                | BookingService USES PaymentGW |
| Many-to-Many | Needs a join entity       | User ↔ Movie (via Booking)    |

---

## Step 4: Apply Design Patterns

### 🏗️ Most Frequently Used Patterns in LLD Interviews

---

### 1. Strategy Pattern ⭐⭐⭐⭐⭐ (Most Common)

**What:** Define a family of algorithms, encapsulate each, make them interchangeable.

**When to Use:**

- Multiple ways to do the same thing (payment, sorting, pricing, notification)
- You see `if-else` or `switch` on a "type" field

**Where (Interview Problems):**

- Payment processing (UPI / Card / Wallet)
- Pricing strategy (Surge / Flat / Distance-based)
- Notification (SMS / Email / Push)
- Ride matching (Nearest / Cheapest / Fastest)

```java
interface PaymentStrategy {
    void pay(double amount);
}

class UPIPayment implements PaymentStrategy { ...
}

class CardPayment implements PaymentStrategy { ...
}

// Usage
class PaymentService {
    private PaymentStrategy strategy;

    public void processPayment(double amt) {
        strategy.pay(amt);
    }
}
```

---

### 2. Factory Pattern ⭐⭐⭐⭐⭐

**What:** Create objects without exposing creation logic. Caller says "what" not "how".

**When to Use:**

- Object creation depends on input/type
- You want to centralize and hide `new` keywords

**Where (Interview Problems):**

- Vehicle creation (Car / Bike / Truck) in Parking Lot
- Notification factory (SMS / Email / Push)
- Document generator (PDF / Excel / CSV)

```java
class VehicleFactory {
    public static Vehicle create(VehicleType type) {
        return switch (type) {
            case CAR -> new Car();
            case BIKE -> new Bike();
            case TRUCK -> new Truck();
        };
    }
}
```

---

### 3. Observer Pattern ⭐⭐⭐⭐

**What:** When one object changes state, all dependents are notified automatically.

**When to Use:**

- One event triggers multiple actions
- Pub-Sub / Event-driven scenarios

**Where (Interview Problems):**

- Notify users when product is back in stock
- Order placed → SMS + Email + Invoice + Analytics
- Stock price change → notify all subscribers
- Auction bid → notify all watchers

```java
interface Observer {
    void update(String event, Object data);
}

class NotificationService implements Observer { ...
}

class AnalyticsService implements Observer { ...
}

class OrderService {
    List<Observer> observers;

    public void placeOrder(Order o) {
        // ... place order logic
        observers.forEach(obs -> obs.update("ORDER_PLACED", o));
    }
}
```

---

### 4. Singleton Pattern ⭐⭐⭐⭐

**What:** Ensure a class has only ONE instance globally.

**When to Use:**

- Shared resource (DB connection pool, Cache, Config)
- Exactly one coordinator needed

**Where (Interview Problems):**

- ParkingLot instance
- Logger
- Configuration manager
- Cache manager

```java
class ParkingLot {
    private static volatile ParkingLot instance;

    private ParkingLot() {
    }

    public static ParkingLot getInstance() {
        if (instance == null) {
            synchronized (ParkingLot.class) {
                if (instance == null)
                    instance = new ParkingLot();
            }
        }
        return instance;
    }
}
```

---

### 5. State Pattern ⭐⭐⭐⭐

**What:** Object changes its behavior when its internal state changes.

**When to Use:**

- Entity has distinct states with different behaviors
- You see a state machine (transitions between states)

**Where (Interview Problems):**

- Vending Machine (Idle → CoinInserted → Dispensing → Idle)
- Order status (Placed → Confirmed → Shipped → Delivered)
- ATM Machine (Idle → CardInserted → PinEntered → Transaction)
- Elevator (Moving → Stopped → DoorOpen)

```java
interface VendingMachineState {
    void insertCoin(VendingMachine vm);

    void selectProduct(VendingMachine vm);

    void dispense(VendingMachine vm);
}

class IdleState implements VendingMachineState { ...
}

class CoinInsertedState implements VendingMachineState { ...
}
```

---

### 6. Decorator Pattern ⭐⭐⭐

**What:** Add responsibilities to objects dynamically without modifying them.

**When to Use:**

- Optional add-ons / layers / toppings
- Features can be stacked

**Where (Interview Problems):**

- Pizza/Coffee with toppings (Cheese + Mushroom + Olives)
- Notification with encryption + compression
- Logger with timestamp + file + console

```java
interface Coffee {
    double cost();

    String description();
}

class BasicCoffee implements Coffee { ...
}

class MilkDecorator implements Coffee {
    private Coffee coffee;

    public double cost() {
        return coffee.cost() + 20;
    }
}
```

---

### 7. Chain of Responsibility ⭐⭐⭐

**What:** Pass a request along a chain of handlers; each decides to process or forward.

**When to Use:**

- Sequential validation / approval steps
- Logging levels
- Request filtering

**Where (Interview Problems):**

- ATM cash dispenser (₹500 → ₹200 → ₹100 notes)
- Expense approval (Manager → Director → VP)
- Request validation (Auth → RateLimit → Sanitize → Process)
- Log level filtering

```java
abstract class CashHandler {
    protected CashHandler next;

    public abstract void dispense(int amount);
}

class FiveHundredHandler extends CashHandler { ...
}

class TwoHundredHandler extends CashHandler { ...
}
```

---

### 8. Builder Pattern ⭐⭐⭐

**What:** Construct complex objects step-by-step.

**When to Use:**

- Object has many optional fields
- Constructor would have too many parameters

**Where (Interview Problems):**

- Query builder
- Notification builder (to, subject, body, attachments, priority)
- Pizza builder
- User profile creation

```java
Notification n = new Notification.Builder()
        .to("user@email.com")
        .subject("Order Confirmed")
        .body("Your order #123 is confirmed")
        .priority(Priority.HIGH)
        .build();
```

---

## 📊 Pattern Selection Cheat Sheet

| If You See This in the Problem...  | Use This Pattern            |
|------------------------------------|-----------------------------|
| Multiple algorithms / strategies   | **Strategy**                |
| Object creation based on type      | **Factory**                 |
| Event triggers multiple actions    | **Observer**                |
| Only one instance needed           | **Singleton**               |
| Object has lifecycle states        | **State**                   |
| Optional add-ons / layers          | **Decorator**               |
| Sequential processing / validation | **Chain of Responsibility** |
| Complex object construction        | **Builder**                 |
| Adapt incompatible interfaces      | **Adapter**                 |
| Simplify complex subsystem         | **Facade**                  |

---

## Step 5: Write Clean Code

### Code Quality Checklist:

```
✅ Use meaningful class/method names
✅ Follow Single Responsibility Principle (1 class = 1 job)
✅ Use interfaces for abstraction (program to interface, not implementation)
✅ Use enums for fixed states/types
✅ Make fields private, expose via methods
✅ Handle edge cases (null, empty, concurrent access)
✅ Use composition over inheritance
✅ Keep methods small (< 15 lines ideally)
```

### SOLID Principles Quick Reference:

| Principle                     | One-Liner                                     |
|-------------------------------|-----------------------------------------------|
| **S** - Single Responsibility | One class, one reason to change               |
| **O** - Open/Closed           | Open for extension, closed for modification   |
| **L** - Liskov Substitution   | Subtypes must be substitutable for base types |
| **I** - Interface Segregation | Many small interfaces > one fat interface     |
| **D** - Dependency Inversion  | Depend on abstractions, not concretions       |

---

## 🗺️ Common LLD Interview Problems → Patterns Map

| Problem                | Key Patterns Used                             |
|------------------------|-----------------------------------------------|
| Parking Lot            | Strategy, Factory, Singleton, Observer        |
| BookMyShow             | Strategy, Observer, Builder, State            |
| Vending Machine        | State, Chain of Responsibility, Singleton     |
| Elevator System        | State, Strategy, Observer, Singleton          |
| Snake & Ladder         | Factory, Strategy, Observer                   |
| Tic Tac Toe            | Strategy, Factory                             |
| ATM Machine            | State, Chain of Responsibility                |
| Ride Sharing (Uber)    | Strategy, Observer, Factory, Singleton        |
| Food Delivery (Zomato) | Strategy, Observer, State, Decorator          |
| Library Management     | Factory, Observer, Strategy                   |
| Hotel Booking          | Strategy, Observer, State, Builder            |
| Splitwise              | Strategy, Observer, Factory                   |
| Chess                  | Strategy, Factory, State                      |
| Logger Framework       | Singleton, Chain of Responsibility, Decorator |

---

## 🚫 Common Mistakes to Avoid

1. **Jumping to code without clarifying** — Always spend 2-3 min on requirements
2. **God class** — One class doing everything (violates SRP)
3. **Hardcoding logic** — Use Strategy/Factory instead of if-else chains
4. **Ignoring concurrency** — Use locks/synchronized where shared state exists
5. **No error handling** — At least mention it, even if you don't code it fully
6. **Over-engineering** — Don't use 10 patterns when 3 will do
7. **No enums** — Using strings for states/types is a red flag
8. **Tight coupling** — Always code to interfaces

---

## ⏱️ Time Management (45-min Interview)

```
[0-3 min]   Clarify requirements & scope
[3-7 min]   Identify entities, relationships, draw rough class diagram
[7-12 min]  Discuss patterns you'll use and WHY
[12-40 min] Write code (start with core classes, then expand)
[40-45 min] Walk through a use case end-to-end, discuss trade-offs
```

---

## 💡 Pro Tips

1. **Think out loud** — Interviewers want to see your thought process
2. **Start with interfaces** — Define contracts first, implement later
3. **Use enums early** — Shows you think about type safety
4. **Mention trade-offs** — "I chose Strategy here because... alternatively we could..."
5. **Draw before you code** — Even a 30-second sketch helps
6. **Name patterns explicitly** — "I'm using the Observer pattern here for..."
7. **Keep extensibility in mind** — "If tomorrow we need to add X, we just implement this interface"

---

## 📝 Template: How to Start Any LLD Problem

```
1. "Let me clarify the requirements..."
2. "The core entities I see are: A, B, C..."
3. "The relationships are: A has-many B, B belongs-to C..."
4. "For [this part], I'll use [Pattern] because [reason]..."
5. "Let me start coding with the interfaces and core classes..."
6. "Here's how a typical flow works end-to-end..."
```

---

*Good luck with your interviews! 🚀*


package lld.Best_Practices_In_LLD;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

// ╔════════════════════════════════════════════════════════════════════════════╗
// ║               DATABASE DESIGN AND INTEGRATION — COMPLETE GUIDE             ║
// ╠════════════════════════════════════════════════════════════════════════════╣
// ║  Section 1 │ ER Model — Entities, Attributes, Relationships                ║
// ║  Section 2 │ How to Design Tables from Requirements                        ║
// ║  Section 3 │ Mapping ER → Class Model (JPA / Hibernate style)              ║
// ║  Section 4 │ DAO Pattern — Data Access Object                              ║
// ║  Section 5 │ Repository Pattern — Domain-Centric Abstraction               ║
// ║  Section 6 │ DAO vs Repository — Side-by-Side Comparison                   ║
// ║  Section 7 │ Real-World Enhancements & Best Practices                      ║
// ╚════════════════════════════════════════════════════════════════════════════╝
//
// ─── Database Types at a Glance ─────────────────────────────────────────────
// ┌──────────────┬──────────────────┬────────────────────┬─────────────────────┐
// │ Type         │ Examples         │ Best For           │ Not Ideal For       │
// ├──────────────┼──────────────────┼────────────────────┼─────────────────────┤
// │ Relational   │ MySQL, Postgres  │ ACID, joins,       │ Massive horizontal  │
// │ (SQL)        │ Oracle, SQL Srv  │ structured data    │ scale               │
// ├──────────────┼──────────────────┼────────────────────┼─────────────────────┤
// │ Document     │ MongoDB,         │ Flexible schema,   │ Complex joins,      │
// │              │ CouchDB          │ nested JSON        │ strict consistency  │
// ├──────────────┼──────────────────┼────────────────────┼─────────────────────┤
// │ Key-Value    │ Redis, DynamoDB  │ Caching, sessions, │ Complex queries     │
// │              │ Memcached        │ fast lookups       │                     │
// ├──────────────┼──────────────────┼────────────────────┼─────────────────────┤
// │ Column       │ Cassandra, HBase │ Time-series, logs, │ Ad-hoc queries,     │
// │              │ ScyllaDB         │ write-heavy        │ joins               │
// ├──────────────┼──────────────────┼────────────────────┼─────────────────────┤
// │ Graph        │ Neo4j, ArangoDB  │ Relationships,     │ Simple CRUD,        │
// │              │ Amazon Neptune   │ social networks    │ tabular data        │
// └──────────────┴──────────────────┴────────────────────┴─────────────────────┘


// ═══════════════════════════════════════════════════════════════════════════════
// SECTION 1 — ER MODEL (Entity-Relationship Model)
// ═══════════════════════════════════════════════════════════════════════════════
//
// ─── What is an ER Model? ───────────────────────────────────────────────────
//   A blueprint that describes ENTITIES (real-world objects), their ATTRIBUTES
//   (properties), and RELATIONSHIPS (how entities connect to each other).
//   It's the first step before writing any SQL or Java code.
//
// ─── Core Concepts ──────────────────────────────────────────────────────────
//   Entity      → A "thing" you store data about (User, Order, Product)
//   Attribute   → A property of an entity (name, email, price)
//   Primary Key → Unique identifier for each entity instance (userId)
//   Foreign Key → Links one entity to another (order.userId → user.id)
//   Relationship→ Association between entities (User PLACES Order)
//
// ─── Relationship Types ─────────────────────────────────────────────────────
// ┌──────────────────┬───────────────────────────────────┬─────────────────────┐
// │ Type             │ Example                           │ Implementation      │
// ├──────────────────┼───────────────────────────────────┼─────────────────────┤
// │ One-to-One (1:1) │ User ↔ UserProfile                │ FK in either table  │
// │ One-to-Many(1:N) │ User → Orders                     │ FK in "many" table  │
// │ Many-to-Many(M:N)│ Student ↔ Course                  │ Join/bridge table   │
// └──────────────────┴───────────────────────────────────┴─────────────────────┘
//
// ─── Example ER Diagram (E-Commerce) ────────────────────────────────────────
//
//   ┌──────────┐       1:N       ┌───────────┐       N:1       ┌──────────────┐
//   │   User   │───────────────▶ │  Order    │◀─────────────── │   Product    │
//   │──────────│                 │───────────│                 │──────────────│
//   │ id (PK)  │                 │ id (PK)   │   M:N via       │ id (PK)      │
//   │ name     │                 │ userId(FK)│   OrderItem     │ name         │
//   │ email    │                 │ total     │                 │ price        │
//   │ phone    │                 │ status    │                 │ categoryId(FK│
//   └──────────┘                 │ createdAt │                 └──────────────┘
//                                └───────────┘
//                                     │ 1:N
//                                     ▼
//                               ┌────────────┐
//                               │ OrderItem  │  (Bridge table for M:N)
//                               │────────────│
//                               │ id (PK)    │
//                               │ orderId(FK)│
//                               │ productId  │
//                               │ quantity   │
//                               │ unitPrice  │
//                               └────────────┘


// ═══════════════════════════════════════════════════════════════════════════════
// SECTION 2 — HOW TO DESIGN TABLES FROM REQUIREMENTS
// ═══════════════════════════════════════════════════════════════════════════════
//
// ─── Step-by-Step Process ───────────────────────────────────────────────────
//   1. Identify nouns in requirements → these become ENTITIES (tables)
//   2. Identify properties            → these become COLUMNS (attributes)
//   3. Identify verbs/associations    → these become RELATIONSHIPS (FKs)
//   4. Determine cardinality          → 1:1, 1:N, M:N
//   5. Normalize to 3NF              → eliminate redundancy
//   6. Add indexes for query patterns → optimize reads
//
// ─── Example Requirement ────────────────────────────────────────────────────
//   "Users can place orders. Each order has multiple products.
//    Each product belongs to a category. Track order status."
//
// ─── Resulting SQL (MySQL style) ────────────────────────────────────────────
/*
    CREATE TABLE users (
        id          BIGINT PRIMARY KEY AUTO_INCREMENT,
        name        VARCHAR(100) NOT NULL,
        email       VARCHAR(255) NOT NULL UNIQUE,
        phone       VARCHAR(20),
        created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

        INDEX idx_email (email)
    );

    CREATE TABLE categories (
        id          BIGINT PRIMARY KEY AUTO_INCREMENT,
        name        VARCHAR(100) NOT NULL UNIQUE
    );

    CREATE TABLE products (
        id          BIGINT PRIMARY KEY AUTO_INCREMENT,
        name        VARCHAR(200) NOT NULL,
        price       DECIMAL(10,2) NOT NULL,
        category_id BIGINT NOT NULL,
        stock       INT DEFAULT 0,

        FOREIGN KEY (category_id) REFERENCES categories(id),
        INDEX idx_category (category_id)
    );

    CREATE TABLE orders (
        id          BIGINT PRIMARY KEY AUTO_INCREMENT,
        user_id     BIGINT NOT NULL,
        total       DECIMAL(12,2) NOT NULL,
        status      ENUM('PLACED','CONFIRMED','SHIPPED','DELIVERED','CANCELLED')
                        DEFAULT 'PLACED',
        created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

        FOREIGN KEY (user_id) REFERENCES users(id),
        INDEX idx_user_status (user_id, status)
    );

    CREATE TABLE order_items (
        id          BIGINT PRIMARY KEY AUTO_INCREMENT,
        order_id    BIGINT NOT NULL,
        product_id  BIGINT NOT NULL,
        quantity    INT NOT NULL,
        unit_price  DECIMAL(10,2) NOT NULL,

        FOREIGN KEY (order_id)   REFERENCES orders(id),
        FOREIGN KEY (product_id) REFERENCES products(id),
        INDEX idx_order (order_id)
    );
*/
//
// ─── Normalization Quick Reference ──────────────────────────────────────────
// ┌──────┬──────────────────────────────────┬─────────────────────────────────┐
// │ Form │ Rule                             │ Example Fix                     │
// ├──────┼──────────────────────────────────┼─────────────────────────────────┤
// │ 1NF  │ No repeating groups / arrays     │ Split "tags" into separate rows │
// │ 2NF  │ No partial dependency on         │ Move product_name out of        │
// │      │ composite key                    │ order_items into products table │
// │ 3NF  │ No transitive dependency         │ Move category_name into         │
// │      │ (non-key → non-key)              │ separate categories table       │
// └──────┴──────────────────────────────────┴─────────────────────────────────┘


// ═══════════════════════════════════════════════════════════════════════════════
// SECTION 3 — MAPPING ER → CLASS MODEL (JPA / Spring Boot Style)
// ═══════════════════════════════════════════════════════════════════════════════
//
// ─── Mapping Rules ──────────────────────────────────────────────────────────
// ┌───────────────────────┬───────────────────────────────────────────────────┐
// │ ER Concept            │ Java / JPA Equivalent                             │
// ├───────────────────────┼───────────────────────────────────────────────────┤
// │ Entity                │ @Entity class                                     │
// │ Attribute             │ Field + @Column                                   │
// │ Primary Key           │ @Id + @GeneratedValue                             │
// │ Foreign Key (1:N)     │ @ManyToOne + @JoinColumn                          │
// │ Reverse side (N:1)    │ @OneToMany(mappedBy = "...")                      │
// │ Many-to-Many          │ @ManyToMany + @JoinTable                          │
// │ One-to-One            │ @OneToOne + @JoinColumn                           │
// │ Enum column           │ @Enumerated(EnumType.STRING)                      │
// │ Timestamp             │ @CreationTimestamp / LocalDateTime                │
// └───────────────────────┴───────────────────────────────────────────────────┘

/// ─── When to Use JPA/Hibernate ─────────────────────────────────────────────
///   ✔ Rapid development with auto table creation
///   ✔ Object-oriented domain model needed
///   ✔ Standard CRUD with relationships
///   ✘ Complex reporting queries (prefer native SQL / jOOQ)
///   ✘ Extreme performance-critical bulk operations

// Enum for Order Status
enum OrderStatus {
    PLACED, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
}

// ─── Entity: User ───────────────────────────────────────────────────────────
// Spring Boot style: @Entity @Table(name = "users")
class User {
    // @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Column(nullable = false)
    private String name;

    // @Column(nullable = false, unique = true)
    private String email;

    private String phone;

    // @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private final List<Order> orders = new ArrayList<>();

    // @CreationTimestamp
    private LocalDateTime createdAt;

    public User() {
    }

    public User(Long id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.createdAt = LocalDateTime.now();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void addOrder(Order order) {
        orders.add(order);
        order.setUser(this);
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', email='" + email + "'}";
    }
}

// ─── Entity: Category ───────────────────────────────────────────────────────
// @Entity @Table(name = "categories")
class Category {
    // @Id @GeneratedValue
    private Long id;

    // @Column(nullable = false, unique = true)
    private String name;

    // @OneToMany(mappedBy = "category")
    private final List<DbProduct> products = new ArrayList<>();

    public Category() {
    }

    public Category(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<DbProduct> getProducts() {
        return products;
    }

    @Override
    public String toString() {
        return "Category{id=" + id + ", name='" + name + "'}";
    }
}

// ─── Entity: Product ────────────────────────────────────────────────────────
// @Entity @Table(name = "products")
class DbProduct {
    // @Id @GeneratedValue
    private Long id;

    // @Column(nullable = false)
    private String name;

    private double price;
    private int stock;

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    public DbProduct() {
    }

    public DbProduct(Long id, String name, double price, int stock, Category category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public Category getCategory() {
        return category;
    }

    public void decrementStock(int qty) {
        if (stock < qty) throw new IllegalStateException("Insufficient stock for " + name);
        stock -= qty;
    }

    @Override
    public String toString() {
        return "DbProduct{id=" + id + ", name='" + name + "', price=" + price + ", stock=" + stock + "}";
    }
}

// ─── Entity: Order ──────────────────────────────────────────────────────────
// @Entity @Table(name = "orders")
class Order {
    // @Id @GeneratedValue
    private Long id;

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private double total;

    // @Enumerated(EnumType.STRING)
    private OrderStatus status;

    // @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<OrderItem> items = new ArrayList<>();

    private LocalDateTime createdAt;

    public Order() {
    }

    public Order(Long id) {
        this.id = id;
        this.status = OrderStatus.PLACED;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public double getTotal() {
        return total;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
        recalculateTotal();
    }

    private void recalculateTotal() {
        this.total = items.stream()
                .mapToDouble(i -> i.getUnitPrice() * i.getQuantity())
                .sum();
    }

    @Override
    public String toString() {
        return "Order{id=" + id + ", status=" + status + ", total=" + total +
                ", items=" + items.size() + "}";
    }
}

// ─── Entity: OrderItem (Bridge table for M:N Order ↔ Product) ───────────────
// @Entity @Table(name = "order_items")
class OrderItem {
    // @Id @GeneratedValue
    private Long id;

    // @ManyToOne @JoinColumn(name = "order_id")
    private Order order;

    // @ManyToOne @JoinColumn(name = "product_id")
    private DbProduct product;

    private int quantity;
    private double unitPrice;

    public OrderItem() {
    }

    public OrderItem(Long id, DbProduct product, int quantity) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = product.getPrice();
    }

    public Long getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public DbProduct getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "OrderItem{product='" + product.getName() + "', qty=" + quantity +
                ", unitPrice=" + unitPrice + "}";
    }
}


// ═══════════════════════════════════════════════════════════════════════════════
// SECTION 4 — DAO PATTERN (Data Access Object)
// ═══════════════════════════════════════════════════════════════════════════════
//
// ─── What is DAO? ────────────────────────────────────────────────────────────
//   A DAO encapsulates ALL database access logic (SQL, JDBC, connection
//   handling) into a single class, isolating it from business logic.
//   It works directly with database concepts (connections, queries, result sets).
//
// ─── When to Use ─────────────────────────────────────────────────────────────
//   ✔ Legacy systems using raw JDBC
//   ✔ Multiple data sources (SQL, NoSQL, file) behind a unified interface
//   ✔ Complex SQL queries that ORMs don't handle well
//   ✔ Need full control over connection management
//
// ─── Pros ────────────────────────────────────────────────────────────────────
//   ✔ Clean separation of persistence from business logic
//   ✔ Easy to swap database implementations (MySQL → Postgres)
//   ✔ Fine-grained control over SQL and performance tuning
//   ✔ Testable — mock the DAO interface in unit tests
//
// ─── Cons ────────────────────────────────────────────────────────────────────
//   ✘ Boilerplate-heavy (open/close connections, map ResultSet manually)
//   ✘ Error-prone resource management (leaks if not careful)
//   ✘ Doesn't enforce domain rules — purely technical layer

// ─── DAO Interface ──────────────────────────────────────────────────────────
interface UserDao {
    void save(User user);

    User findById(Long id);

    List<User> findAll();

    User findByEmail(String email);

    void update(User user);

    void deleteById(Long id);
}

// ─── DAO Implementation (JDBC style) ────────────────────────────────────────
// In Spring Boot, you'd use JdbcTemplate instead of raw JDBC:
//   @Repository
//   class UserDaoJdbcImpl implements UserDao {
//       @Autowired private JdbcTemplate jdbc;
//       public User findById(Long id) {
//           return jdbc.queryForObject("SELECT * FROM users WHERE id = ?",
//               new UserRowMapper(), id);
//       }
//   }

class UserDaoImpl implements UserDao {
    // Simulating an in-memory database using a HashMap
    private final Map<Long, User> database = new HashMap<>();

    @Override
    public void save(User user) {
        // In real JDBC:
        // String sql = "INSERT INTO users (name, email, phone) VALUES (?, ?, ?)";
        // PreparedStatement ps = conn.prepareStatement(sql);
        // ps.setString(1, user.getName()); ...
        database.put(user.getId(), user);
        System.out.println("[DAO] Saved: " + user);
    }

    @Override
    public User findById(Long id) {
        // In real JDBC:
        // String sql = "SELECT * FROM users WHERE id = ?";
        // ResultSet rs = ps.executeQuery();
        // return mapRow(rs);
        return database.get(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(database.values());
    }

    @Override
    public User findByEmail(String email) {
        return database.values().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void update(User user) {
        if (!database.containsKey(user.getId())) {
            throw new IllegalArgumentException("User not found: " + user.getId());
        }
        database.put(user.getId(), user);
        System.out.println("[DAO] Updated: " + user);
    }

    @Override
    public void deleteById(Long id) {
        User removed = database.remove(id);
        if (removed != null) {
            System.out.println("[DAO] Deleted: " + removed);
        }
    }
}


// ═══════════════════════════════════════════════════════════════════════════════
// SECTION 5 — REPOSITORY PATTERN (Domain-Centric Abstraction)
// ═══════════════════════════════════════════════════════════════════════════════
//
// ─── What is a Repository? ──────────────────────────────────────────────────
//   A Repository acts like an in-memory COLLECTION of domain objects.
//   It speaks the language of the DOMAIN, not the database.
//   The caller doesn't know or care about SQL — just "find me orders by status."
//
// ─── When to Use ─────────────────────────────────────────────────────────────
//   ✔ Domain-Driven Design (DDD) projects
//   ✔ Spring Data JPA — auto-generates implementations from method names
//   ✔ Business logic needs clean, expressive data access
//   ✔ You want framework-managed transactions and caching
//
// ─── Pros ────────────────────────────────────────────────────────────────────
//   ✔ Extremely clean and expressive — reads like English
//   ✔ Spring Data auto-implements queries from method names
//   ✔ Built-in pagination, sorting, auditing support
//   ✔ Encourages aggregate root thinking (DDD)
//
// ─── Cons ────────────────────────────────────────────────────────────────────
//   ✘ Less control over generated SQL (may produce N+1 queries)
//   ✘ Complex queries may need @Query or native SQL anyway
//   ✘ Tied to Spring ecosystem (not portable to non-Spring projects)
//   ✘ Hides database details — harder to optimize without understanding internals

// ─── Repository Interface ───────────────────────────────────────────────────
// In Spring Boot, this would be:
//   @Repository
//   public interface OrderRepository extends JpaRepository<Order, Long> {
//       List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);
//       List<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status);
//       @Query("SELECT o FROM Order o WHERE o.total > :minTotal")
//       List<Order> findExpensiveOrders(@Param("minTotal") double minTotal);
//   }

interface OrderRepository {
    void save(Order order);

    Optional<Order> findById(Long id);

    List<Order> findAll();

    List<Order> findByUserId(Long userId);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);
}

// ─── Repository Implementation (simulated) ──────────────────────────────────
class OrderRepositoryImpl implements OrderRepository {
    private final Map<Long, Order> store = new HashMap<>();

    @Override
    public void save(Order order) {
        store.put(order.getId(), order);
        System.out.println("[REPO] Saved: " + order);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        return store.values().stream()
                .filter(o -> o.getUser() != null && o.getUser().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        return store.values().stream()
                .filter(o -> o.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findByUserIdAndStatus(Long userId, OrderStatus status) {
        return store.values().stream()
                .filter(o -> o.getUser() != null
                        && o.getUser().getId().equals(userId)
                        && o.getStatus() == status)
                .collect(Collectors.toList());
    }
}


// ═══════════════════════════════════════════════════════════════════════════════
// SECTION 6 — DAO vs REPOSITORY — SIDE-BY-SIDE COMPARISON
// ═══════════════════════════════════════════════════════════════════════════════
//
// ┌──────────────────────┬──────────────────────────┬──────────────────────────┐
// │ Aspect               │ DAO                      │ Repository               │
// ├──────────────────────┼──────────────────────────┼──────────────────────────┤
// │ Focus                │ Persistence / DB ops     │ Domain object collection │
// │ Abstraction Level    │ Low (close to DB)        │ High (close to domain)   │
// │ Language             │ SQL / JDBC concepts      │ Business / domain terms  │
// │ SQL Control          │ Full control             │ Framework-generated      │
// │ Typical Methods      │ save, findById, delete   │ findByStatus, findActive │
// │ Framework            │ Raw JDBC / JdbcTemplate  │ Spring Data JPA          │
// │ Boilerplate          │ High                     │ Very Low (auto-impl)     │
// │ Use Case Example     │ Legacy migration, perf   │ DDD, rapid development   │
// │ Testability          │ Mock the DAO interface   │ @DataJpaTest + H2        │
// │ N+1 Problem          │ You control joins        │ Need @EntityGraph / JOIN │
// │ Portability          │ Portable (plain Java)    │ Spring-specific          │
// └──────────────────────┴──────────────────────────┴──────────────────────────┘
//
// ─── When to Choose ─────────────────────────────────────────────────────────
//   • Use DAO: complex raw SQL, multiple data sources, legacy JDBC code
//   • Use Repository: Spring Boot project, standard CRUD, DDD
//   • You CAN use both: Repository for simple queries, DAO for complex reports


// ═══════════════════════════════════════════════════════════════════════════════
// SECTION 7 — REAL-WORLD ENHANCEMENTS & BEST PRACTICES
// ═══════════════════════════════════════════════════════════════════════════════
//
// ─── 1. Service Layer — Business Logic Between Controller & Repository ──────
//   Controller → Service → Repository → Database
//   The Service layer orchestrates business rules, transactions, and validation.
//
// ─── Spring Boot Style ──────────────────────────────────────────────────────
//   @Service
//   @Transactional
//   class OrderService {
//       @Autowired private OrderRepository orderRepo;
//       @Autowired private ProductRepository productRepo;
//
//       public Order placeOrder(Long userId, Map<Long, Integer> cart) {
//           Order order = new Order();
//           cart.forEach((productId, qty) -> {
//               Product p = productRepo.findById(productId).orElseThrow();
//               p.decrementStock(qty);
//               order.addItem(new OrderItem(null, p, qty));
//           });
//           return orderRepo.save(order);
//       }
//   }

// ─── Service Layer (plain Java simulation) ──────────────────────────────────

class OrderService {
    private final UserDao userDao;
    private final OrderRepository orderRepo;

    public OrderService(UserDao userDao, OrderRepository orderRepo) {
        this.userDao = userDao;
        this.orderRepo = orderRepo;
    }

    /// Place an order for a user with given product-quantity map
    public Order placeOrder(Long userId, List<OrderItem> items) {
        // 1. Validate user exists
        User user = userDao.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + userId);
        }

        // 2. Create order and add items
        Order order = new Order((long) (Math.random() * 10000));
        for (OrderItem item : items) {
            // Decrement stock (would be transactional in real app)
            item.getProduct().decrementStock(item.getQuantity());
            order.addItem(item);
        }

        // 3. Link user and save
        user.addOrder(order);
        orderRepo.save(order);

        System.out.println("[SERVICE] Order placed for " + user.getName() + ": " + order);
        return order;
    }

    /// Get all orders for a user filtered by status
    public List<Order> getOrdersByStatus(Long userId, OrderStatus status) {
        return orderRepo.findByUserIdAndStatus(userId, status);
    }
}

//
// ─── 2. Transaction Management ──────────────────────────────────────────────
//   • @Transactional on service methods ensures atomicity
//   • If any step fails, ALL database changes roll back
//   • Spring manages begin/commit/rollback automatically
//
//   @Transactional(
//       isolation = Isolation.READ_COMMITTED,   // prevent dirty reads
//       propagation = Propagation.REQUIRED,      // join existing or start new
//       rollbackFor = Exception.class            // rollback on any exception
//   )
//
// ─── 3. Connection Pooling ──────────────────────────────────────────────────
//   • HikariCP (default in Spring Boot) — fast, lightweight
//   • Config in application.yml:
//     spring.datasource.hikari.maximum-pool-size: 20
//     spring.datasource.hikari.minimum-idle: 5
//     spring.datasource.hikari.connection-timeout: 30000
//
// ─── 4. Indexing Strategy ───────────────────────────────────────────────────
// ┌─────────────────────┬─────────────────────────────────────────────────────┐
// │ Index Type          │ When to Use                                         │
// ├─────────────────────┼─────────────────────────────────────────────────────┤
// │ Single Column       │ WHERE clause on one column (email, userId)          │
// │ Composite           │ WHERE on multiple columns (userId + status)         │
// │ Unique              │ Enforce uniqueness (email, username)                │
// │ Full-Text           │ Text search (product descriptions)                  │
// │ Partial / Filtered  │ Index only active rows (WHERE deleted = false)      │
// └─────────────────────┴─────────────────────────────────────────────────────┘
//   Rule: Index columns that appear in WHERE, JOIN, ORDER BY, GROUP BY
//   Caution: Too many indexes slow down INSERT/UPDATE operations
//
// ─── 5. Soft Delete vs Hard Delete ──────────────────────────────────────────
//   Soft Delete: SET deleted = true (data preserved, can be recovered)
//     @SQLDelete(sql = "UPDATE users SET deleted = true WHERE id = ?")
//     @Where(clause = "deleted = false")
//
//   Hard Delete: DELETE FROM users WHERE id = ?  (permanent removal)
//
// ─── 6. Auditing ────────────────────────────────────────────────────────────
//   Track who changed what and when:
//   @CreatedBy, @CreatedDate, @LastModifiedBy, @LastModifiedDate
//   Enable with: @EnableJpaAuditing on config class
//
// ─── 7. Pagination in Spring Data ───────────────────────────────────────────
//   Page<Order> findByStatus(OrderStatus status, Pageable pageable);
//   // Usage:
//   Pageable page = PageRequest.of(0, 10, Sort.by("createdAt").descending());
//   Page<Order> result = orderRepo.findByStatus(OrderStatus.PLACED, page);
//
// ─── 8. N+1 Problem & Solutions ─────────────────────────────────────────────
//   Problem: Loading 100 orders → 1 query for orders + 100 queries for users
//   Solutions:
//   • @EntityGraph(attributePaths = {"user", "items"})
//   • JOIN FETCH in JPQL: "SELECT o FROM Order o JOIN FETCH o.user"
//   • Batch fetching: @BatchSize(size = 25)
//
// ─── Best Practices Summary ─────────────────────────────────────────────────
//   1. Always use a Service layer between Controller and Repository
//   2. Keep Entities clean — no business logic (use Services for that)
//   3. Use DTOs for API responses — never expose entities directly
//   4. Enable connection pooling (HikariCP)
//   5. Index columns used in WHERE, JOIN, ORDER BY
//   6. Use @Transactional on service methods
//   7. Handle N+1 with JOIN FETCH or @EntityGraph
//   8. Use soft delete for recoverable data
//   9. Add auditing fields (createdAt, updatedAt, createdBy)
//  10. Write integration tests with @DataJpaTest + H2/TestContainers


// ═══════════════════════════════════════════════════════════════════════════════
// DEMO — Putting It All Together
// ═══════════════════════════════════════════════════════════════════════════════

public class DatabaseDesignAndIntegration {
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("  DATABASE DESIGN & INTEGRATION — DEMO");
        System.out.println("═══════════════════════════════════════════════════\n");

        // ── 1. Setup: Create entities ───────────────────────────────────────
        System.out.println("── Section 3: Entity Creation (ER → Class) ──");
        Category electronics = new Category(1L, "Electronics");
        Category books = new Category(2L, "Books");

        DbProduct laptop = new DbProduct(1L, "Laptop", 999.99, 10, electronics);
        DbProduct phone = new DbProduct(2L, "Phone", 499.99, 25, electronics);
        DbProduct javaBook = new DbProduct(3L, "Effective Java", 39.99, 100, books);

        User alice = new User(1L, "Alice", "alice@example.com", "1234567890");
        User bob = new User(2L, "Bob", "bob@example.com", "0987654321");

        System.out.println("Created: " + alice);
        System.out.println("Created: " + bob);
        System.out.println("Created: " + laptop);
        System.out.println("Created: " + phone);
        System.out.println("Created: " + javaBook);

        // ── 2. DAO Demo ────────────────────────────────────────────────────
        System.out.println("\n── Section 4: DAO Pattern Demo ──");
        UserDao userDao = new UserDaoImpl();
        userDao.save(alice);
        userDao.save(bob);

        User found = userDao.findById(1L);
        System.out.println("[DAO] findById(1): " + found);

        User byEmail = userDao.findByEmail("bob@example.com");
        System.out.println("[DAO] findByEmail('bob@example.com'): " + byEmail);

        System.out.println("[DAO] findAll(): " + userDao.findAll());

        // ── 3. Repository Demo ─────────────────────────────────────────────
        System.out.println("\n── Section 5: Repository Pattern Demo ──");
        OrderRepository orderRepo = new OrderRepositoryImpl();

        // ── 4. Service Layer Demo ──────────────────────────────────────────
        System.out.println("\n── Section 7: Service Layer Demo ──");
        OrderService orderService = new OrderService(userDao, orderRepo);

        // Alice places an order for a laptop and a book
        List<OrderItem> aliceCart = List.of(
                new OrderItem(1L, laptop, 1),
                new OrderItem(2L, javaBook, 2)
        );
        Order aliceOrder = orderService.placeOrder(alice.getId(), aliceCart);

        // Bob places an order for a phone
        List<OrderItem> bobCart = List.of(
                new OrderItem(3L, phone, 1)
        );
        Order bobOrder = orderService.placeOrder(bob.getId(), bobCart);

        // ── 5. Query Demo ──────────────────────────────────────────────────
        System.out.println("\n── Repository Queries ──");
        System.out.println("All orders: " + orderRepo.findAll());
        System.out.println("Alice's orders: " + orderRepo.findByUserId(1L));
        System.out.println("PLACED orders: " + orderRepo.findByStatus(OrderStatus.PLACED));

        // ── 6. Stock Check ─────────────────────────────────────────────────
        System.out.println("\n── Stock After Orders ──");
        System.out.println(laptop.getName() + " stock: " + laptop.getStock());   // 10 - 1 = 9
        System.out.println(phone.getName() + " stock: " + phone.getStock());     // 25 - 1 = 24
        System.out.println(javaBook.getName() + " stock: " + javaBook.getStock()); // 100 - 2 = 98

        // ── 7. Error Handling Demo ─────────────────────────────────────────
        System.out.println("\n── Error Handling ──");
        try {
            // Try to place order for non-existent user
            orderService.placeOrder(999L, List.of());
        } catch (IllegalArgumentException e) {
            System.out.println("Expected error: " + e.getMessage());
        }

        try {
            // Try to order more than available stock
            DbProduct limitedItem = new DbProduct(4L, "Limited Edition", 199.99, 1, books);
            List<OrderItem> tooMany = List.of(new OrderItem(4L, limitedItem, 5));
            orderService.placeOrder(alice.getId(), tooMany);
        } catch (IllegalStateException e) {
            System.out.println("Expected error: " + e.getMessage());
        }

        System.out.println("\n════════════════════════════════════════  ═══════════");
        System.out.println("  All demos completed successfully!");
        System.out.println("═══════════════════════════════════════════════════");
    }
}

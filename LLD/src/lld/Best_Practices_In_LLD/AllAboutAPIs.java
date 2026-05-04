package lld.Best_Practices_In_LLD;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/// ╔════════════════════════════════════════════════════════════════════════════╗
/// ║          ALL ABOUT APIs – DESIGN, VERSIONING & SECURITY                    ║
/// ╠════════════════════════════════════════════════════════════════════════════╣
/// ║                                                                            ║
/// ║  Topics Covered:                                                           ║
/// ║  ┌───────────────────────────────────────────────────────────────────┐     ║
/// ║  │ 1. What Are APIs? Design Principles                               │     ║
/// ║  │ 2. API Lifecycle Across Teams                                     │     ║
/// ║  │ 3. Advanced REST Principles & Sensitive Operations                │     ║
/// ║  │ 4. DTO Patterns – Designing Contracts                             │     ║
/// ║  │ 5. Error Handling                                                 │     ║
/// ║  │ 6. Versioning                                                     │     ║
/// ║  │ 7. Filtering, Sorting, and Pagination                             │     ║
/// ║  │ 8. API Security, Throttling & Rate Limiting                       │     ║
/// ║  └───────────────────────────────────────────────────────────────────┘     ║
/// ║                                                                            ║
/// ║  API Styles Comparison:                                                    ║
/// ║  ┌────────────┬──────────────────┬──────────────────┬─────────────────┐    ║
/// ║  │ Style      │ Best For         │ Protocol         │ Data Format     │    ║
/// ║  ├────────────┼──────────────────┼──────────────────┼─────────────────┤    ║
/// ║  │ REST       │ CRUD, Web apps   │ HTTP             │ JSON/XML        │    ║
/// ║  │ GraphQL    │ Flexible queries │ HTTP             │ JSON            │    ║
/// ║  │ gRPC       │ Microservices    │ HTTP/2           │ Protobuf        │    ║
/// ║  │ WebSocket  │ Real-time        │ WS               │ Any             │    ║
/// ║  │ SOAP       │ Enterprise/Bank  │ HTTP/SMTP        │ XML             │    ║
/// ║  └────────────┴──────────────────┴──────────────────┴─────────────────┘    ║
/// ╚════════════════════════════════════════════════════════════════════════════╝


// ═══════════════════════════════════════════════════════════════════════════════
// 1. WHAT ARE APIs? – DESIGN PRINCIPLES
// ═══════════════════════════════════════════════════════════════════════════════

/// API = Application Programming Interface
/// A contract between systems that defines how they communicate.
///
/// REST API Design Rules:
///   ✅ Use nouns for resources:    GET /users, POST /orders
///   ❌ Avoid verbs in URLs:        GET /getUsers, POST /createOrder
///   ✅ Use plural names:           /products, /users
///   ✅ Use HTTP methods correctly:
///
///   ┌─────────┬──────────────────┬────────────────────┬─────────────┐
///   │ Method  │ Purpose          │ Example            │ Idempotent? │
///   ├─────────┼──────────────────┼────────────────────┼─────────────┤
///   │ GET     │ Read resource    │ GET /users/123     │ ✅ Yes       │
///   │ POST    │ Create resource  │ POST /users        │ ❌ No        │
///   │ PUT     │ Full update      │ PUT /users/123     │ ✅ Yes       │
///   │ PATCH   │ Partial update   │ PATCH /users/123   │ ✅ Yes       │
///   │ DELETE  │ Remove resource  │ DELETE /users/123  │ ✅ Yes       │
///   └─────────┴──────────────────┴────────────────────┴─────────────┘
///
/// HTTP Status Codes:
///   ┌──────────┬──────────────────────────────────────┐
///   │ Code     │ Meaning                              │
///   ├──────────┼──────────────────────────────────────┤
///   │ 200      │ OK – Success                         │
///   │ 201      │ Created – Resource created           │
///   │ 204      │ No Content – Deleted successfully    │
///   │ 400      │ Bad Request – Invalid input          │
///   │ 401      │ Unauthorized – Not authenticated     │
///   │ 403      │ Forbidden – Not authorized           │
///   │ 404      │ Not Found – Resource doesn't exist   │
///   │ 409      │ Conflict – Duplicate/state conflict  │
///   │ 429      │ Too Many Requests – Rate limited     │
///   │ 500      │ Internal Server Error                │
///   │ 503      │ Service Unavailable                  │
///   └──────────┴──────────────────────────────────────┘
///
/// Key Design Principles:
///   1. Consistency     – Same naming, structure across all endpoints
///   2. Statelessness   – Each request is self-contained (no server-side session)
///   3. Idempotency     – Same request = same result (GET, PUT, DELETE)
///   4. HATEOAS         – Response includes links to related actions
///   5. Versioning      – Never break existing clients


// ═══════════════════════════════════════════════════════════════════════════════
// 2. API LIFECYCLE ACROSS TEAMS
// ═══════════════════════════════════════════════════════════════════════════════

/// Lifecycle Stages:
///   ┌──────────────┐    ┌──────────────┐    ┌──────────────┐
///   │   DESIGN     │ →  │   DEVELOP    │ →  │   TEST       │
///   │ (Contract)   │    │ (Implement)  │    │ (Validate)   │
///   └──────────────┘    └──────────────┘    └──────────────┘
///          ↓                                        ↓
///   ┌──────────────┐    ┌──────────────┐    ┌──────────────┐
///   │  DEPRECATE   │ ←  │   MONITOR    │ ←  │   DEPLOY     │
///   │ (Sunset)     │    │ (Observe)    │    │ (Release)    │
///   └──────────────┘    └──────────────┘    └──────────────┘
///
/// Contract-First (API-First) Approach:
///   - Define OpenAPI/Swagger spec BEFORE writing code
///   - Frontend & backend teams can work in parallel
///   - Auto-generate client SDKs from spec
///
/// When to Use Contract-First:
///   ✅ Multiple teams consuming the same API
///   ✅ Public/partner-facing APIs
///   ✅ Microservice boundaries
///
/// When Code-First is OK:
///   ✅ Internal-only APIs with single consumer
///   ✅ Rapid prototyping / hackathons


// ═══════════════════════════════════════════════════════════════════════════════
// 3. ADVANCED REST PRINCIPLES & SENSITIVE OPERATIONS
// ═══════════════════════════════════════════════════════════════════════════════

/// Sensitive Operations – Use POST even for "reads":
///   - POST /users/search        (complex filters in body)
///   - POST /reports/generate    (triggers a job)
///   - POST /auth/login          (credentials in body, not URL)
///
/// Why? GET parameters appear in:
///   ❌ Browser history, server logs, proxy logs, referrer headers
///
/// Sub-Resources (Nested Routes):
///   GET  /users/123/orders          → orders of user 123
///   GET  /orders/456/items          → items in order 456
///   POST /users/123/addresses       → add address for user 123
///
/// Bulk Operations:
///   POST /users/bulk         { "users": [...] }    → create many
///   DELETE /orders/bulk      { "ids": [1,2,3] }    → delete many
///
/// Async Long-Running Operations:
///   POST /reports/generate   → 202 Accepted + { "jobId": "abc" }
///   GET  /jobs/abc/status    → { "status": "IN_PROGRESS", "progress": 45 }


// ═══════════════════════════════════════════════════════════════════════════════
// 4. DTO PATTERNS – DESIGNING CONTRACTS
// ═══════════════════════════════════════════════════════════════════════════════

/// DTO = Data Transfer Object
/// Purpose: Decouple internal domain model from external API contract
///
/// When to Use:
///   - Never expose your entity/DB model directly
///   - Different views for create vs read vs list
///   - Hide sensitive fields (password, internal IDs)
///
/// Pros:
///   ✅ API stays stable even if DB schema changes
///   ✅ Can tailor response per use case (list vs detail)
///   ✅ Security: control exactly what's exposed
///
/// Cons:
///   ❌ More classes to maintain
///   ❌ Need mapping logic (entity ↔ DTO)
///
/// @param password accepted but NEVER returned

// Request DTO – what the client sends to CREATE a user
record CreateUserRequest(String name, String email, String password) {
}

// Response DTO – what the client receives (no password!)
record UserResponse(String id, String name, String email, String createdAt) {

    @Override
    public String toString() {
        return "UserResponse{id='" + id + "', name='" + name
                + "', email='" + email + "', createdAt='" + createdAt + "'}";
    }
}

// List DTO – minimal fields for listing (no detailed info)
record UserListItem(String id, String name) {

    @Override
    public String toString() {
        return "UserListItem{id='" + id + "', name='" + name + "'}";
    }
}


// ═══════════════════════════════════════════════════════════════════════════════
// 5. ERROR HANDLING – STANDARDIZED ERROR RESPONSES
// ═══════════════════════════════════════════════════════════════════════════════

/// Principles:
///   ✅ Always return a consistent error structure
///   ✅ Include: status, error code, message, timestamp, path
///   ✅ Never expose stack traces in production
///   ✅ Use problem+json (RFC 7807) for standard format
///
/// Standard Error Response Format:
///   {
///     "status": 400,
///     "error": "BAD_REQUEST",
///     "message": "Email format is invalid",
///     "timestamp": "2026-05-03T10:15:30Z",
///     "path": "/api/v1/users"
///   }

class ApiError {
    private final int status;
    private final String error;
    private final String message;
    private final String timestamp;
    private final String path;

    public ApiError(int status, String error, String message, String path) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.timestamp = Instant.now().toString();
        this.path = path;
    }

    @Override
    public String toString() {
        return "{\n"
                + "  \"status\": " + status + ",\n"
                + "  \"error\": \"" + error + "\",\n"
                + "  \"message\": \"" + message + "\",\n"
                + "  \"timestamp\": \"" + timestamp + "\",\n"
                + "  \"path\": \"" + path + "\"\n"
                + "}";
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }
}

// Custom API exception with status code
class ApiException extends RuntimeException {
    private final int statusCode;
    private final String errorCode;

    public ApiException(int statusCode, String errorCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}


// ═══════════════════════════════════════════════════════════════════════════════
// 6. VERSIONING STRATEGIES
// ═══════════════════════════════════════════════════════════════════════════════

/// Why Version?
///   - You WILL need to introduce breaking changes
///   - Old clients must continue to work
///   - Gradual migration, not big-bang
///
/// Versioning Approaches:
///   ┌─────────────────────┬─────────────────────────────┬──────────────────────┐
///   │ Strategy            │ Example                     │ Used By              │
///   ├─────────────────────┼─────────────────────────────┼──────────────────────┤
///   │ URI Path            │ /api/v1/users               │ Twitter, GitHub      │
///   │ Query Parameter     │ /api/users?version=2        │ Google               │
///   │ Header              │ X-API-Version: 2            │ Azure                │
///   │ Content Negotiation │ Accept: application/vnd.    │ GitHub (alt)         │
///   │                     │   myapp.v2+json             │                      │
///   └─────────────────────┴─────────────────────────────┴──────────────────────┘
///
/// URI Path Versioning (Most Common):
///   When to Use:
///     ✅ Public APIs with many consumers
///     ✅ Clear, visible, easy to document
///   Pros:
///     ✅ Simple to implement and understand
///     ✅ Easy to route in API gateway / load balancer
///   Cons:
///     ❌ URL pollution (many paths)
///     ❌ Clients must update URLs on version change
///
/// Header Versioning:
///   When to Use:
///     ✅ Internal APIs, cleaner URLs
///   Pros:
///     ✅ URLs stay clean
///     ✅ Can default to latest version
///   Cons:
///     ❌ Less discoverable (not visible in URL)
///     ❌ Harder to test in browser
///
/// Deprecation Strategy:
///   1. Announce deprecation (Sunset header, docs)
///   2. Return warning in response headers
///   3. Support old version for 6-12 months
///   4. Remove with final notice

// Simulates versioned API controllers
class UserControllerV1 {
    // GET /api/v1/users/{id} → returns basic info
    public Map<String, String> getUser(String id) {
        Map<String, String> response = new HashMap<>();
        response.put("id", id);
        response.put("name", "John Doe");
        response.put("email", "john@example.com");
        return response;
    }
}

class UserControllerV2 {
    // GET /api/v2/users/{id} → returns enriched info + HATEOAS links
    public Map<String, Object> getUser(String id) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", id);
        response.put("name", "John Doe");
        response.put("email", "john@example.com");
        response.put("phone", "+1-555-0123"); // new field in v2

        // HATEOAS links
        Map<String, String> links = new HashMap<>();
        links.put("self", "/api/v2/users/" + id);
        links.put("orders", "/api/v2/users/" + id + "/orders");
        response.put("_links", links);

        return response;
    }
}


// ═══════════════════════════════════════════════════════════════════════════════
// 7. FILTERING, SORTING & PAGINATION
// ═══════════════════════════════════════════════════════════════════════════════

/// Why?
///   - Never return ALL records (performance, bandwidth, UX)
///   - Let clients request exactly what they need
///
/// URL Patterns:
///   GET /products?category=electronics&minPrice=100    → Filtering
///   GET /products?sort=price&order=asc                 → Sorting
///   GET /products?page=2&size=20                       → Pagination
///   GET /products?category=electronics&sort=price&page=1&size=10  → Combined
///
/// Pagination Styles:
///   ┌────────────────────┬──────────────────────────────┬───────────────────────┐
///   │ Style              │ How It Works                 │ Best For              │
///   ├────────────────────┼──────────────────────────────┼───────────────────────┤
///   │ Offset-based       │ page=2&size=20 (skip 20)     │ Simple UIs, SQL       │
///   │ Cursor-based       │ after=abc123&size=20         │ Infinite scroll, feeds│
///   │ Keyset             │ createdAfter=2026-01-01      │ Large datasets        │
///   └────────────────────┴──────────────────────────────┴───────────────────────┘
///
/// Offset-Based:
///   Pros: ✅ Simple, ✅ Random page access
///   Cons: ❌ Slow on large datasets (OFFSET N), ❌ Inconsistent if data changes
///
/// Cursor-Based:
///   Pros: ✅ Fast, ✅ Consistent pagination
///   Cons: ❌ No random page access, ❌ Complex implementation

// Paginated response wrapper
class PagedResponse<T> {
    private final List<T> data;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    private final boolean hasNext;

    public PagedResponse(List<T> data, int page, int size, long totalElements) {
        this.data = data;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / size);
        this.hasNext = page < totalPages;
    }

    public List<T> getData() {
        return data;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    @Override
    public String toString() {
        return "PagedResponse{page=" + page + "/" + totalPages
                + ", size=" + size + ", total=" + totalElements
                + ", hasNext=" + hasNext + ", data=" + data + "}";
    }
}

// Product model for filtering/sorting demo
record Product(String id, String name, String category, double price) {

    @Override
    public String toString() {
        return name + " ($" + price + ", " + category + ")";
    }
}

// Service demonstrating filter + sort + paginate
class ProductService {
    private final List<Product> products;

    public ProductService(List<Product> products) {
        this.products = products;
    }

    // Filter + Sort + Paginate in one call
    public PagedResponse<Product> getProducts(String category, String sortBy,
                                              String order, int page, int size) {
        // 1. FILTER
        List<Product> filtered = products.stream()
                .filter(p -> category == null || p.category().equalsIgnoreCase(category))
                .collect(Collectors.toList());

        // 2. SORT
        if (sortBy != null) {
            Comparator<Product> comparator = switch (sortBy.toLowerCase()) {
                case "price" -> Comparator.comparingDouble(Product::price);
                case "name" -> Comparator.comparing(Product::name);
                default -> Comparator.comparing(Product::id);
            };
            if ("desc".equalsIgnoreCase(order)) {
                comparator = comparator.reversed();
            }
            filtered.sort(comparator);
        }

        // 3. PAGINATE (offset-based)
        long total = filtered.size();
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, filtered.size());

        List<Product> pageData = (fromIndex >= filtered.size())
                ? Collections.emptyList()
                : filtered.subList(fromIndex, toIndex);

        return new PagedResponse<>(pageData, page, size, total);
    }
}


// ═══════════════════════════════════════════════════════════════════════════════
// 8. API SECURITY, THROTTLING & RATE LIMITING
// ═══════════════════════════════════════════════════════════════════════════════

/// Security Layers:
///   ┌───────────────────────────────────────────────────────────────────────┐
///   │ Layer           │ What It Does                                        │
///   ├─────────────────┼─────────────────────────────────────────────────────┤
///   │ Authentication  │ WHO are you? (JWT, OAuth, API Key)                  │
///   │ Authorization   │ WHAT can you do? (RBAC, ABAC)                       │
///   │ Transport       │ HTTPS only (TLS encryption)                         │
///   │ Input Validation│ Reject malformed/malicious input                    │
///   │ Rate Limiting   │ Prevent abuse (throttle requests)                   │
///   │ CORS            │ Control which origins can call API                  │
///   └─────────────────┴─────────────────────────────────────────────────────┘
///
/// Authentication Methods:
///   ┌───────────────┬────────────────────────────┬────────────────────────────┐
///   │ Method        │ When to Use                │ Pros / Cons                │
///   ├───────────────┼────────────────────────────┼────────────────────────────┤
///   │ API Key       │ Server-to-server, simple   │ ✅ Simple ❌ No user ctx     │
///   │ JWT (Bearer)  │ Stateless auth, SPAs       │ ✅ Scalable ❌ Can't revoke  │
///   │ OAuth 2.0     │ Third-party access         │ ✅ Granular ❌ Complex       │
///   │ Session/Cookie│ Traditional web apps       │ ✅ Revocable ❌ Stateful     │
///   └───────────────┴────────────────────────────┴────────────────────────────┘
///
/// Rate Limiting Algorithms:
///   ┌─────────────────────┬─────────────────────────────────────────────────────┐
///   │ Algorithm           │ How It Works                                        │
///   ├─────────────────────┼─────────────────────────────────────────────────────┤
///   │ Fixed Window        │ Count requests per time window (e.g., 100/min)      │
///   │ Sliding Window      │ Rolling window, smoother than fixed                 │
///   │ Token Bucket        │ Tokens added at fixed rate, request consumes token  │
///   │ Leaky Bucket        │ Requests processed at fixed rate, excess queued     │
///   └─────────────────────┴─────────────────────────────────────────────────────┘
///
/// Rate Limiting – When to Use:
///   ✅ Public APIs (prevent abuse)
///   ✅ Paid tiers (enforce quotas)
///   ✅ Protect against DDoS
///
/// Pros:
///   ✅ Protects backend from overload
///   ✅ Fair usage across clients
///   ✅ Can monetize (free tier: 100/hr, pro: 10000/hr)
///
/// Cons:
///   ❌ Legitimate traffic may be rejected during spikes
///   ❌ Distributed rate limiting is complex (Redis needed)
///   ❌ Clock synchronization issues in distributed systems

// Simple Fixed-Window Rate Limiter
class FixedWindowRateLimiter {
    private final int maxRequests;        // max allowed per window
    private final long windowSizeMs;      // window duration in ms
    private final ConcurrentHashMap<String, WindowData> clientWindows = new ConcurrentHashMap<>();

    public FixedWindowRateLimiter(int maxRequests, long windowSizeMs) {
        this.maxRequests = maxRequests;
        this.windowSizeMs = windowSizeMs;
    }

    public boolean allowRequest(String clientId) {
        long now = System.currentTimeMillis();
        clientWindows.putIfAbsent(clientId, new WindowData(now));

        WindowData window = clientWindows.get(clientId);
        synchronized (window) {
            // Reset window if expired
            if (now - window.windowStart >= windowSizeMs) {
                window.windowStart = now;
                window.count.set(0);
            }

            if (window.count.get() < maxRequests) {
                window.count.incrementAndGet();
                return true; // allowed
            }
            return false; // rate limited (429)
        }
    }

    private static class WindowData {
        long windowStart;
        AtomicInteger count = new AtomicInteger(0);

        WindowData(long start) {
            this.windowStart = start;
        }
    }
}

// Token Bucket Rate Limiter (smoother, allows bursts)
class TokenBucketRateLimiter {
    private final int maxTokens;          // bucket capacity
    private final double refillRate;      // tokens per millisecond
    private double currentTokens;
    private long lastRefillTime;

    public TokenBucketRateLimiter(int maxTokens, int tokensPerSecond) {
        this.maxTokens = maxTokens;
        this.refillRate = tokensPerSecond / 1000.0;
        this.currentTokens = maxTokens; // start full
        this.lastRefillTime = System.currentTimeMillis();
    }

    public synchronized boolean allowRequest() {
        refill();
        if (currentTokens >= 1) {
            currentTokens--;
            return true;
        }
        return false; // no tokens available
    }

    private void refill() {
        long now = System.currentTimeMillis();
        double tokensToAdd = (now - lastRefillTime) * refillRate;
        currentTokens = Math.min(maxTokens, currentTokens + tokensToAdd);
        lastRefillTime = now;
    }

    public double getCurrentTokens() {
        refill();
        return currentTokens;
    }
}


// ═══════════════════════════════════════════════════════════════════════════════
// MAIN – DEMO ALL API CONCEPTS
// ═══════════════════════════════════════════════════════════════════════════════

public class AllAboutAPIs {
    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║       ALL ABOUT APIs – DEMO                      ║");
        System.out.println("╚══════════════════════════════════════════════════╝\n");

        // ─── 4. DTO Pattern Demo ──────────────────────────────────────────
        System.out.println("═══ 4. DTO PATTERN ═══");
        CreateUserRequest request = new CreateUserRequest("Alice", "alice@example.com", "secret123");
        // Simulate: controller receives request → service creates user → returns response DTO
        UserResponse response = new UserResponse("usr-001", request.name(),
                request.email(), Instant.now().toString());
        System.out.println("  Request received: name=" + request.name() + ", email=" + request.email());
        System.out.println("  Response (no password!): " + response);

        // List DTO
        UserListItem listItem = new UserListItem("usr-001", "Alice");
        System.out.println("  List view: " + listItem);
        System.out.println();

        // ─── 5. Error Handling Demo ───────────────────────────────────────
        System.out.println("═══ 5. ERROR HANDLING ═══");
        try {
            // Simulate a validation failure
            throw new ApiException(400, "BAD_REQUEST", "Email format is invalid");
        } catch (ApiException e) {
            ApiError error = new ApiError(e.getStatusCode(), e.getErrorCode(),
                    e.getMessage(), "/api/v1/users");
            System.out.println("  Standardized Error Response:");
            System.out.println("  " + error.toString().replace("\n", "\n  "));
        }
        System.out.println();

        // ─── 6. Versioning Demo ──────────────────────────────────────────
        System.out.println("═══ 6. VERSIONING ═══");
        UserControllerV1 v1 = new UserControllerV1();
        UserControllerV2 v2 = new UserControllerV2();
        System.out.println("  V1 GET /api/v1/users/123: " + v1.getUser("123"));
        System.out.println("  V2 GET /api/v2/users/123: " + v2.getUser("123"));
        System.out.println();

        // ─── 7. Filtering, Sorting & Pagination Demo ─────────────────────
        System.out.println("═══ 7. FILTERING, SORTING & PAGINATION ═══");
        List<Product> productData = Arrays.asList(
                new Product("p1", "Laptop", "electronics", 999.99),
                new Product("p2", "Mouse", "electronics", 29.99),
                new Product("p3", "Desk", "furniture", 249.99),
                new Product("p4", "Keyboard", "electronics", 79.99),
                new Product("p5", "Chair", "furniture", 399.99),
                new Product("p6", "Monitor", "electronics", 549.99),
                new Product("p7", "Lamp", "furniture", 59.99)
        );

        ProductService productService = new ProductService(productData);

        // Filter: electronics only, sorted by price asc, page 1, size 2
        System.out.println("  GET /products?category=electronics&sort=price&order=asc&page=1&size=2");
        PagedResponse<Product> page1 = productService.getProducts("electronics", "price", "asc", 1, 2);
        System.out.println("  " + page1);

        System.out.println("  GET /products?category=electronics&sort=price&order=asc&page=2&size=2");
        PagedResponse<Product> page2 = productService.getProducts("electronics", "price", "asc", 2, 2);
        System.out.println("  " + page2);

        // No filter, sorted by price desc
        System.out.println("  GET /products?sort=price&order=desc&page=1&size=3");
        PagedResponse<Product> allByPrice = productService.getProducts(null, "price", "desc", 1, 3);
        System.out.println("  " + allByPrice);
        System.out.println();

        // ─── 8. Rate Limiting Demo ────────────────────────────────────────
        System.out.println("═══ 8. RATE LIMITING ═══");

        // Fixed Window: 5 requests per 1000ms
        System.out.println("  [Fixed Window] Max 5 requests per second:");
        FixedWindowRateLimiter fixedLimiter = new FixedWindowRateLimiter(5, 1000);
        for (int i = 1; i <= 8; i++) {
            boolean allowed = fixedLimiter.allowRequest("client-A");
            System.out.println("    Request #" + i + ": " + (allowed ? "✅ ALLOWED" : "❌ 429 RATE LIMITED"));
        }
        System.out.println();

        // Token Bucket: capacity=3, refill=2/sec
        System.out.println("  [Token Bucket] Capacity=3, Refill=2/sec:");
        TokenBucketRateLimiter tokenLimiter = new TokenBucketRateLimiter(3, 2);
        for (int i = 1; i <= 5; i++) {
            boolean allowed = tokenLimiter.allowRequest();
            System.out.printf("    Request #%d: %s (tokens remaining: %.1f)%n",
                    i, allowed ? "✅ ALLOWED" : "❌ REJECTED", tokenLimiter.getCurrentTokens());
        }

        // Wait for refill
        System.out.println("    ... waiting 1.5s for token refill ...");
        try {
            Thread.sleep(1500);
        } catch (InterruptedException ignored) {
        }
        boolean afterWait = tokenLimiter.allowRequest();
        System.out.printf("    Request #6 (after wait): %s (tokens: %.1f)%n",
                afterWait ? "✅ ALLOWED" : "❌ REJECTED", tokenLimiter.getCurrentTokens());
        System.out.println();

        System.out.println("═══ ALL DEMOS COMPLETE ═══");
    }
}

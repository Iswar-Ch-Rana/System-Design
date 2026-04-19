package lld.Structural_Design_Patterns;

/// ## Facade Pattern
/// Provides a simplified interface to a library, a framework, or any other complex set of classes.
///
/// **Examples:** Starting a car (turn key -> battery, fuel, starter), One-click checkout, Home theater system.
///
/// ### The Problem: Complex Subsystems
/// A client needs to interact with multiple services to complete a single task (e.g., booking a ticket).
/// This leads to tight coupling and complex client code.
///
/// ### The Solution: Facade Pattern
/// A wrapper class that delegates client requests to the appropriate subsystem classes.
///
/// #### Understanding
/// - **Facade:** The entry point that knows which subsystem classes are responsible for a request.
/// - **Subsystem Classes:** Handle specific tasks. They have no knowledge of the facade.
/// - **Client:** Uses the facade instead of calling subsystem objects directly.
///
/// #### Pros
/// - **Simplicity:** Client code becomes much cleaner.
/// - **Loose Coupling:** Subsystem changes don't necessarily break client code.
/// - **Layering:** Can be used to define entry points to each level of a layered system.
///
/// #### Cons
/// - **God Object Risk:** Facade can become too complex if it tries to do everything.

// ========== Subsystem Services ==========

// Service class responsible for handling payments.
class PaymentService {
    public void makePayment(String accountId, double amount) {
        System.out.println("Payment of ₹" + amount + " successful for account " + accountId);
    }
}

/// Service class responsible for reserving seats.
class SeatReservationService {
    public void reserveSeat(String movieId, String seatNumber) {
        System.out.println("Seat " + seatNumber + " reserved for movie " + movieId);
    }
}

/// Service class responsible for sending notifications.
class NotificationService {
    public void sendBookingConfirmation(String userEmail) {
        System.out.println("Booking confirmation sent to " + userEmail);
    }
}

/// Service class for managing loyalty/reward points.
class LoyaltyPointsService {
    public void addPoints(String accountId, int points) {
        System.out.println(points + " loyalty points added to account " + accountId);
    }
}

/// Service class for generating movie tickets.
class TicketService {
    public void generateTicket(String movieId, String seatNumber) {
        System.out.println("Ticket generated for movie " + movieId + ", Seat: " + seatNumber);
    }
}

// ========== Facade ==========

/// Unified interface for movie ticket booking.
class MovieTicketFacade {
    private final PaymentService paymentService;
    private final SeatReservationService seatReservationService;
    private final NotificationService notificationService;
    private final LoyaltyPointsService loyaltyPointsService;
    private final TicketService ticketService;

    public MovieTicketFacade() {
        this.paymentService = new PaymentService();
        this.seatReservationService = new SeatReservationService();
        this.notificationService = new NotificationService();
        this.loyaltyPointsService = new LoyaltyPointsService();
        this.ticketService = new TicketService();
    }

    /// Orchestrates the entire booking process.
    public void bookMovieTicket(String accountId, String userEmail, String movieId, String seatNumber, double amount) {
        paymentService.makePayment(accountId, amount);
        seatReservationService.reserveSeat(movieId, seatNumber);
        notificationService.sendBookingConfirmation(userEmail);
        loyaltyPointsService.addPoints(accountId, 50);
        ticketService.generateTicket(movieId, seatNumber);
    }
}

/// ## Summary of Facade Pattern
///
/// ### Pros
/// - **Isolation:** Protects clients from subsystem components.
///
/// ### Cons
/// - **Limited Access:** If a client needs a specific subsystem feature not in facade, they must bypass it.
public class FacadePattern {
    public static void main(String[] args) {
        // Without Facade (Complex for client)
        // Step 1: Make payment
        PaymentService paymentService = new PaymentService();
        paymentService.makePayment("user123", 500);

        // Step 2: Reserve seat
        SeatReservationService seatReservationService = new SeatReservationService();
        seatReservationService.reserveSeat("movie456", "A10");

        // Step 3: Send booking confirmation via email
        NotificationService notificationService = new NotificationService();
        notificationService.sendBookingConfirmation("user@example.com");

        // Step 4: Add loyalty points to user's account
        LoyaltyPointsService loyaltyPointsService = new LoyaltyPointsService();
        loyaltyPointsService.addPoints("user123", 50);

        // Step 5: Generate the ticket
        TicketService ticketService = new TicketService();
        ticketService.generateTicket("movie456", "A10");


        // With Facade (Simplified)
        System.out.println("\n--- Booking with Facade ---\n");
        MovieTicketFacade facade = new MovieTicketFacade();
        facade.bookMovieTicket("user123", "example@email.com", "movie456", "A10", 500);
    }
}

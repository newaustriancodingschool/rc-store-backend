package at.refugeescode.rcstore.controller.logic;

import at.refugeescode.rcstore.models.Item;
import at.refugeescode.rcstore.models.LogEntry;
import at.refugeescode.rcstore.models.User;
import at.refugeescode.rcstore.persistence.ItemRepository;
import at.refugeescode.rcstore.persistence.LogEntryRepository;
import at.refugeescode.rcstore.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class BookingServiceImp implements BookingService {

    private final ItemRepository itemRepository;
    private final LogEntryRepository logEntryRepository;

    @Override
    public String book(Item item) {
        if (isWithinBorrowingLimit(item)) {
            setBorrowingInfo(item);
            createLogEntry(item);
            itemRepository.save(item);
        }
        return "redirect:/";
    }

    boolean isWithinBorrowingLimit(Item item) {
        return Duration.between(item.getBorrowingDate(), item.getDueDate()).abs().toDays() <= item.getBorrowingLimit();
    }

    void setBorrowingInfo(Item item) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        item.setBookedBy(authentication.getName());
        item.setBorrowed(true);
    }

    private void createLogEntry(Item item) {
        User user = getCurrentUser();
        LogEntry logEntry = LogEntry.builder()
                .borrowerName(user.getFirstName() + " " + user.getLastName())
                .borrowerId(user.getId())
                .nameOfBorrowedItem(item.getName())
                .descriptionOfBorrowedItem(item.getDescription())
                .idOfBorrowedItem(item.getId())
                .dateOfBorrowing(item.getBorrowingDate())
                .dateOfReturn(item.getDueDate())
                .build();
        logEntryRepository.save(logEntry);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return userPrincipal.getUser();
    }

}

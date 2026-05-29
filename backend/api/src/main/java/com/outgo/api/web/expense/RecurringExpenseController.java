package com.outgo.api.web.expense;

import com.outgo.api.application.expense.command.CreateRecurringExpenseCommand;
import com.outgo.api.application.expense.command.CreateRecurringExpenseUseCase;
import com.outgo.api.application.expense.command.DeactivateRecurringExpenseUseCase;
import com.outgo.api.application.expense.query.GetRecurringExpensesQuery;
import com.outgo.api.domain.expense.RecurringExpenseId;
import com.outgo.api.domain.shared.Money;
import com.outgo.api.web.expense.dto.CreateRecurringExpenseRequest;
import com.outgo.api.web.expense.dto.RecurringExpenseResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/recurring-expenses")
public class RecurringExpenseController {

    private final CreateRecurringExpenseUseCase createRecurringExpenseUseCase;
    private final DeactivateRecurringExpenseUseCase deactivateRecurringExpenseUseCase;
    private final GetRecurringExpensesQuery getRecurringExpensesQuery;

    public RecurringExpenseController(
            CreateRecurringExpenseUseCase createRecurringExpenseUseCase,
            DeactivateRecurringExpenseUseCase deactivateRecurringExpenseUseCase,
            GetRecurringExpensesQuery getRecurringExpensesQuery) {
        this.createRecurringExpenseUseCase = createRecurringExpenseUseCase;
        this.deactivateRecurringExpenseUseCase = deactivateRecurringExpenseUseCase;
        this.getRecurringExpensesQuery = getRecurringExpensesQuery;
    }

    @PostMapping
    public ResponseEntity<Void> create(
            @Valid @RequestBody CreateRecurringExpenseRequest request,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        CreateRecurringExpenseCommand command = new CreateRecurringExpenseCommand(
                userId,
                Money.of(request.amount(), request.currency()),
                request.category(),
                request.description(),
                request.frequency(),
                request.startDate());

        RecurringExpenseId id = createRecurringExpenseUseCase.execute(command);
        return ResponseEntity.created(URI.create("/api/recurring-expenses/" + id.value())).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable UUID id, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        deactivateRecurringExpenseUseCase.execute(RecurringExpenseId.of(id), userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<RecurringExpenseResponse>> list(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        List<RecurringExpenseResponse> response = getRecurringExpensesQuery.execute(userId)
                .stream()
                .map(RecurringExpenseResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }
}

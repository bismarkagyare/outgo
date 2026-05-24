package com.outgo.api.web.expense;

import com.outgo.api.application.expense.command.CreateExpenseCommand;
import com.outgo.api.application.expense.command.CreateExpenseUseCase;
import com.outgo.api.application.expense.command.DeleteExpenseUseCase;
import com.outgo.api.application.expense.command.UpdateExpenseCommand;
import com.outgo.api.application.expense.command.UpdateExpenseUseCase;
import com.outgo.api.application.expense.query.GetExpenseByIdQuery;
import com.outgo.api.application.expense.query.GetMonthlyExpensesQuery;
import com.outgo.api.domain.expense.ExpenseId;
import com.outgo.api.domain.shared.Money;
import com.outgo.api.web.expense.dto.CreateExpenseRequest;
import com.outgo.api.web.expense.dto.ExpenseResponse;
import com.outgo.api.web.expense.dto.UpdateExpenseRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final CreateExpenseUseCase createExpenseUseCase;
    private final UpdateExpenseUseCase updateExpenseUseCase;
    private final DeleteExpenseUseCase deleteExpenseUseCase;
    private final GetMonthlyExpensesQuery getMonthlyExpensesQuery;
    private final GetExpenseByIdQuery getExpenseByIdQuery;

    public ExpenseController(CreateExpenseUseCase createExpenseUseCase,
            UpdateExpenseUseCase updateExpenseUseCase,
            DeleteExpenseUseCase deleteExpenseUseCase,
            GetMonthlyExpensesQuery getMonthlyExpensesQuery,
            GetExpenseByIdQuery getExpenseByIdQuery) {
        this.createExpenseUseCase = createExpenseUseCase;
        this.updateExpenseUseCase = updateExpenseUseCase;
        this.deleteExpenseUseCase = deleteExpenseUseCase;
        this.getMonthlyExpensesQuery = getMonthlyExpensesQuery;
        this.getExpenseByIdQuery = getExpenseByIdQuery;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Map<String, UUID>> create(@Valid @RequestBody CreateExpenseRequest request,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        Money amount = Money.of(request.amount(), request.currency());
        CreateExpenseCommand command = new CreateExpenseCommand(
                userId, amount, request.category(), request.description(), request.expenseDate());
        ExpenseId id = createExpenseUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", id.getValue()));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable UUID id,
            @Valid @RequestBody UpdateExpenseRequest request,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        Money amount = Money.of(request.amount(), request.currency());
        UpdateExpenseCommand command = new UpdateExpenseCommand(
                ExpenseId.of(id), userId, amount, request.category(), request.description(), request.expenseDate());
        updateExpenseUseCase.execute(command);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        deleteExpenseUseCase.execute(ExpenseId.of(id), userId);
    }

    @GetMapping("/{id}")
    public ExpenseResponse getById(@PathVariable UUID id, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        return ExpenseResponse.from(getExpenseByIdQuery.execute(ExpenseId.of(id), userId));
    }

    @GetMapping
    public List<ExpenseResponse> getMonthly(@RequestParam int year,
            @RequestParam int month,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        return getMonthlyExpensesQuery.execute(userId, year, month).stream()
                .map(ExpenseResponse::from)
                .toList();
    }
}

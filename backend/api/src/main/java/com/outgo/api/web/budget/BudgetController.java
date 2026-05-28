package com.outgo.api.web.budget;

import com.outgo.api.application.budget.CreateBudgetCommand;
import com.outgo.api.application.budget.CreateBudgetUseCase;
import com.outgo.api.application.budget.DeleteBudgetUseCase;
import com.outgo.api.application.budget.GetBudgetsByMonthQuery;
import com.outgo.api.application.budget.UpdateBudgetCommand;
import com.outgo.api.application.budget.UpdateBudgetUseCase;
import com.outgo.api.domain.budget.BudgetId;
import com.outgo.api.domain.shared.Money;
import com.outgo.api.web.budget.dto.BudgetResponse;
import com.outgo.api.web.budget.dto.CreateBudgetRequest;
import com.outgo.api.web.budget.dto.UpdateBudgetRequest;
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
@RequestMapping("/api/budgets")
public class BudgetController {

    private final CreateBudgetUseCase createBudgetUseCase;
    private final UpdateBudgetUseCase updateBudgetUseCase;
    private final DeleteBudgetUseCase deleteBudgetUseCase;
    private final GetBudgetsByMonthQuery getBudgetsByMonthQuery;

    public BudgetController(CreateBudgetUseCase createBudgetUseCase,
            UpdateBudgetUseCase updateBudgetUseCase,
            DeleteBudgetUseCase deleteBudgetUseCase,
            GetBudgetsByMonthQuery getBudgetsByMonthQuery) {
        this.createBudgetUseCase = createBudgetUseCase;
        this.updateBudgetUseCase = updateBudgetUseCase;
        this.deleteBudgetUseCase = deleteBudgetUseCase;
        this.getBudgetsByMonthQuery = getBudgetsByMonthQuery;
    }

    @PostMapping
    public ResponseEntity<Map<String, UUID>> create(@Valid @RequestBody CreateBudgetRequest request,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        Money limit = Money.of(request.amount(), request.currency());
        CreateBudgetCommand command = new CreateBudgetCommand(
                userId, limit, request.category(), request.year(), request.month());
        BudgetId id = createBudgetUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", id.getValue()));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable UUID id,
            @Valid @RequestBody UpdateBudgetRequest request,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        Money newLimit = Money.of(request.amount(), request.currency());
        updateBudgetUseCase.execute(new UpdateBudgetCommand(BudgetId.of(id), userId, newLimit));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        deleteBudgetUseCase.execute(BudgetId.of(id), userId);
    }

    @GetMapping
    public List<BudgetResponse> getByMonth(@RequestParam int year,
            @RequestParam int month,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        return getBudgetsByMonthQuery.execute(userId, year, month).stream()
                .map(BudgetResponse::from)
                .toList();
    }
}

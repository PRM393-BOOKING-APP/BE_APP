package com.sang.sourcepattern.controller;

import com.sang.sourcepattern.dto.response.ApiResponse;
import com.sang.sourcepattern.dto.response.MembershipTierResponse;
import com.sang.sourcepattern.repository.MembershipTierRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Public endpoint to list all membership tiers (sorted by requiredSpending ascending).
 * Used by the app to render the tier progress screen.
 */
@RestController
@RequestMapping("/public/membership-tiers")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MembershipTierController {

    MembershipTierRepository membershipTierRepository;

    @GetMapping
    public ApiResponse<List<MembershipTierResponse>> getAllTiers() {
        List<MembershipTierResponse> tiers = membershipTierRepository.findAll()
                .stream()
                .sorted(java.util.Comparator.comparingDouble(
                        com.sang.sourcepattern.entity.MembershipTier::getRequiredSpending))
                .map(t -> MembershipTierResponse.builder()
                        .id((long) t.getId())
                        .name(t.getName())
                        .requiredSpending(t.getRequiredSpending())
                        .benefits(t.getBenefits())
                        .build())
                .collect(Collectors.toList());

        return ApiResponse.<List<MembershipTierResponse>>builder()
                .result(tiers)
                .build();
    }
}

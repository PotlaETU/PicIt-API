package com.picit.iam.exceptions;

import lombok.Builder;

@Builder
public record ErrorResponse(
        String message
) {
}

package com.workshop.recipe.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorType {
    private String message;
    private String code;
    private String error;
    private String classType;
}

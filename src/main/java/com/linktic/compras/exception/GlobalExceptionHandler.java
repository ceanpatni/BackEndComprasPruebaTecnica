package com.linktic.compras.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.linktic.compras.dto.request.ErrorResponseDTO;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;


@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ChangeSetPersister.NotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(ChangeSetPersister.NotFoundException ex) {
        logError(ex);

        String type = "recurso";
        String message = ex.getMessage() != null ? ex.getMessage() : "Recurso no encontrado";

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(type, message);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }


    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponseDTO> handleBusiness(BusinessException ex) {
        logError(ex);

        // Si tu BusinessException ya trae tipo, úsalo.
        String type = "negocio";

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(type, ex.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneral(Exception ex) {
        logError(ex);

        String type = "interno";
        String message = "Error interno del servidor";

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(type, message);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationErrors(MethodArgumentNotValidException ex) {
        logError(ex);

        String errorMsg = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fe -> fe.getDefaultMessage())
                .findFirst()
                .orElse("Error de validación");

        Object target = ex.getBindingResult().getTarget();
        String type = null;
        if (target != null) {
            try {
                type = (String) target.getClass().getMethod("getResourceType").invoke(target);
            } catch (Exception ignored) {
            }
        }

        if (type == null) {
            type = "desconocido";
        }

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(type, errorMsg);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidDataFormat(InvalidFormatException ex) {
        logError(ex);

        String fieldName = ex.getPath().stream()
                .map(ref -> ref.getFieldName())
                .reduce((f1, f2) -> f1 + "." + f2)
                .orElse("campo");

        String errorMsg = String.format("El valor '%s' no es válido para el campo '%s'", ex.getValue(), fieldName);

        // Por default usamos desconocido
        String type = "desconocido";

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(type, errorMsg);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidFormat(HttpMessageNotReadableException ex) {
        logError(ex);

        String errorMsg = "JSON inválido";
        Throwable cause = ex.getCause();

        if (cause instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) cause;
            String fieldName = ife.getPath().stream()
                    .map(ref -> ref.getFieldName())
                    .reduce((f1, f2) -> f1 + "." + f2)
                    .orElse("campo");

            errorMsg = String.format("El valor '%s' no es válido para el campo '%s'", ife.getValue(), fieldName);
        }

        String type = "desconocido";
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(type, errorMsg);

        return ResponseEntity.badRequest().body(errorResponse);
    }
    private void logError(Exception ex) {
        // Logea mensaje y stack trace
        logger.error("Excepción capturada: {} - {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
    }
}

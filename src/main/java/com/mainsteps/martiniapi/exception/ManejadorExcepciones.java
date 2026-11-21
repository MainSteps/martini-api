package com.mainsteps.martiniapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Convierte errores de negocio y de validación en respuestas JSON claras.
 */
@RestControllerAdvice
public class ManejadorExcepciones {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> noEncontrado(RecursoNoEncontradoException ex) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("estado", 404);
        cuerpo.put("error", "No encontrado");
        cuerpo.put("mensaje", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(cuerpo);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errores.put(error.getField(), error.getDefaultMessage());
        }
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("estado", 400);
        cuerpo.put("error", "Validación fallida");
        cuerpo.put("errores", errores);
        return ResponseEntity.badRequest().body(cuerpo);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> jsonInvalido(HttpMessageNotReadableException ex) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("estado", 400);
        cuerpo.put("error", "Petición inválida");
        cuerpo.put("mensaje", "El cuerpo JSON es inválido o un valor de enum no es reconocido. "
                + "Divisiones: PANADERIA_PASTELERIA, GELATERIA, FOOD_SERVICE, RETAIL. "
                + "Productos: MANTECA_UNIGRAL, MARGARINA_GRAN_MASTER_HOJA, BRAVO_CREM, "
                + "MONNALISA, CHOCOLATE_CARAVELLA, CHOCOLATE_ARIBA, LINEA_VEGANA_ORASI. "
                + "Estados: PENDIENTE, CONTACTADO.");
        return ResponseEntity.badRequest().body(cuerpo);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> parametroInvalido(MethodArgumentTypeMismatchException ex) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("estado", 400);
        cuerpo.put("error", "Petición inválida");
        cuerpo.put("mensaje", "El parámetro '" + ex.getName() + "' no es válido. "
                + "Divisiones válidas: PANADERIA_PASTELERIA, GELATERIA, FOOD_SERVICE, RETAIL.");
        return ResponseEntity.badRequest().body(cuerpo);
    }
}

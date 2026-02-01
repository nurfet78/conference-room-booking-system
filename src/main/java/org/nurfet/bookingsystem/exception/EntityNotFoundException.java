package org.nurfet.bookingsystem.exception;

public class EntityNotFoundException extends BusinessException {

    public EntityNotFoundException(String entityName, Long id) {
        super("ОБЪЕКТ НЕ НАЙДЕН", entityName + " с id " + id + " не найден");
    }

    public EntityNotFoundException(String entityName, String identifier) {
        super("ОБЪЕКТ НЕ НАЙДЕН", entityName + " '" + identifier + " 'not found");
    }
}

package co.com.bancolombia.model.ex;

public class EntityNotFoundException extends DomainException {
    public EntityNotFoundException(String entity, Object id) {
        super("ENTITY_NOT_FOUND", String.format("%s with id '%s' not found", entity, id));
    }
}
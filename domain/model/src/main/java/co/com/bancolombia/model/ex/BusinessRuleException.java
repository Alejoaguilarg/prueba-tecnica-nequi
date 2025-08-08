package co.com.bancolombia.model.ex;

public class BusinessRuleException extends DomainException {
    public BusinessRuleException(String rule, String detail) {
        super("BUSINESS_RULE_" + rule.toUpperCase(), detail);
    }
}

package <%=packageNameGenerated%>.security;

import <%=packageNameGenerated%>.config.Constants;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

/**
 * Implementation of AuditorAware based on Spring Security.
 */
@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    public String getCurrentAuditor() {
        return (Constants.SYSTEM_ACCOUNT);
    }
}

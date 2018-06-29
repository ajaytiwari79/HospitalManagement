package com.kairos.config.security;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.expression.OAuth2ExpressionParser;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;
import org.springframework.security.oauth2.provider.expression.OAuth2SecurityExpressionMethods;
public class CustomOAuth2WebSecurityExpressionHandler extends OAuth2MethodSecurityExpressionHandler {
    private AuthenticationTrustResolver trustResolver =
            new AuthenticationTrustResolverImpl();

    public CustomOAuth2WebSecurityExpressionHandler() {
        setExpressionParser(new OAuth2ExpressionParser(getExpressionParser()));
    }
    @Override
    public MethodSecurityExpressionOperations createSecurityExpressionRoot(
            Authentication authentication, MethodInvocation invocation) {
        CustomMethodSecurityExpressionRoot root =
                new CustomMethodSecurityExpressionRoot(authentication);
        root.setTrustResolver(this.trustResolver);
        root.setRoleHierarchy(getRoleHierarchy());
        root.setPermissionEvaluator(getPermissionEvaluator());
        return root;
    }
    @Override
    public StandardEvaluationContext createEvaluationContextInternal(Authentication authentication, MethodInvocation mi) {
        StandardEvaluationContext ec = super.createEvaluationContextInternal(authentication, mi);
        ec.setVariable("oauth2", new OAuth2SecurityExpressionMethods(authentication));
        return ec;
    }
}

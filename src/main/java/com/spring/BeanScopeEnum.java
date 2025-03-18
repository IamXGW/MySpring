package com.spring;

/**
 * @author xuguangwei
 */

public enum BeanScopeEnum {
    SINGLETON("singleton"),
    PROTOTYPE("prototype");

    private String value;

    BeanScopeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

package com.budget.core.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Description:
 * <p>
 * -----------------------------------------------------
 * Created by kremezniy on 9/7/2016.
 */
@Getter @AllArgsConstructor
public enum Statuses {
    SUCCESS("SUCCESS"),
    ERROR("ERROR"),
    WARNING("WARNING");

    private final String text;
}

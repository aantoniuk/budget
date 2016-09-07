package com.budget.core.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Description:
 * <p>
 * -----------------------------------------------------
 * Created by kremezniy on 9/7/2016.
 */
@Getter @AllArgsConstructor
public enum RestStatuses {
    SUCCESS("SUCCESS"),
    ERROR("ERROR"),
    WARNING("WARNING");

    private final String text;
}

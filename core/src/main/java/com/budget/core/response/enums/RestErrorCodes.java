package com.budget.core.response.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Description:
 * <p>
 * -----------------------------------------------------
 * Created by kremezniy on 9/7/2016.
 */
@Getter @AllArgsConstructor
public enum RestErrorCodes {
    RECAT01("Categories data is empty. There is no categories"),
    RECAT02("Category with ID %1 has not been found.");

    private final String text;
}

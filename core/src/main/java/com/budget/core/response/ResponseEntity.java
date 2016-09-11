package com.budget.core.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Description:
 * <p>
 * -----------------------------------------------------
 * Created by kremezniy on 9/7/2016.
 */
@Getter @Setter @AllArgsConstructor
public class ResponseEntity {
    Status statusObject;
    Object restObject;
}

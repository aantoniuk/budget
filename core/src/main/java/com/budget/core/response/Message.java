package com.budget.core.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Description:
 * <p>
 * -----------------------------------------------------
 * Created by kremezniy on 9/7/2016.
 */
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class Message {
    private String msgCode;
    private String[] msgValues;
    private String msgText;
}

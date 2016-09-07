package com.budget.core.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Description:
 * <p>
 * -----------------------------------------------------
 * Created by kremezniy on 9/7/2016.
 */
@Getter @Setter @AllArgsConstructor
public class RestStatus {
    String restStatus;
    List<RestMessage> restMessageList;
}

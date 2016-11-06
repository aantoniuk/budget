package com.budget.core.entity;

import com.budget.core.Utils.OperationType;
import lombok.*;

import javax.persistence.*;

@Getter @Setter
@EqualsAndHashCode
@NoArgsConstructor
@MappedSuperclass
public abstract class BaseCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    private String name;
    @Enumerated(EnumType.STRING)
    private OperationType type;
    private Boolean enable;
    private Long parentId;

    public BaseCategory(@NonNull String name, OperationType type, Boolean enable, Long parentId) {
        this.name = name;
        this.type = type;
        if(enable == null) {
            this.enable = Boolean.TRUE;
        } else {
            this.enable = enable;
        }
        this.parentId = parentId;
    }
}

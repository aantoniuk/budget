package com.budget.core.entity;

import com.budget.core.Utils.OperationType;
import lombok.*;

import javax.persistence.*;

@Getter @Setter
@EqualsAndHashCode
@NoArgsConstructor @RequiredArgsConstructor
@MappedSuperclass
abstract class BaseCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NonNull
    private String name;
    @NonNull
    @Enumerated(EnumType.STRING)
    private OperationType type;
    private boolean enable = true;
    private Long parentId;
}

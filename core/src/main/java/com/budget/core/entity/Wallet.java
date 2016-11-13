package com.budget.core.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@EqualsAndHashCode
@Getter @Setter @NoArgsConstructor @RequiredArgsConstructor
public class Wallet {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NonNull
    private String name;
    @NonNull
    private Boolean enable = true;
    @NonNull
    private Long userCurrencyId;
}

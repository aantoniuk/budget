package com.budget.core.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter @NoArgsConstructor @RequiredArgsConstructor
public class Wallet {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NonNull
    private String name;
    @NonNull // FIXME doesn't work with primitive. I guess we need to define true value here
    private boolean enable;
    @NonNull
    @ManyToOne
    // FIXME I guess we need to use relations to User and Carency instead UserCurrency
    private UserCurrency userCurrency;
}

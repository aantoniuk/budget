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
    @NonNull
    private Boolean enable = true;
    @NonNull
    @ManyToOne
    @JoinColumn(name="USER_ID")
    private User user;
    @NonNull
    @ManyToOne
    @JoinColumn(name="CURRENCY_ID")
    private Currency currency;
}

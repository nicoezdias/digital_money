package com.digital.money.msvc.api.account.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("transaction_id")
    @Column(name = "transaction_id")
    private Long transactionId;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "realization_date", nullable = false)
    private LocalDateTime realizationDate;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "from_cvu", nullable = false, length = 22)
    private String fromCvu;

    @Column(name = "to_cvu", nullable = false, length = 22)
    private String toCvu;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TransactionType type;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "account_id")
    @JsonIgnore
    private Account account;
}






package com.digital.money.msvc.api.account.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Entity
@Table(name = "cards")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("card_id")
    @Column(unique = true, name = "card_id")
    private Long cardId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "account_id", nullable = false)
    @JsonProperty("account_id")
    private Account account;

    @Column(name = "alias", length = 50)
    private String alias;

    @Column(name = "cardNumber", unique = true, nullable = false, length = 16)
    private Long cardNumber;

    @Column(name = "cardHolder", nullable = false, length = 30)
    private String cardHolder;

    @Column(name = "expirationDate", nullable = false, length = 7)
    private String expirationDate;

    @Column(name = "cvv", nullable = false, length = 4)
    private Integer cvv;

    @Column(name = "bank", nullable = false, length = 50)
    private String bank;

    @Column(name = "cardType", nullable = false, length = 16)
    private String cardType;

    @Column(name = "cardNetwork", nullable = false)
    private String cardNetwork;

    @Column(name = "isEnabled", nullable = false)
    private boolean isEnabled = true;


    @Override
    public String toString() {
        return "Card{" +
                "cardId=" + cardId +
                ", alias='" + alias + '\'' +
                ", cardNumber=" + cardNumber +
                ", cardHolder='" + cardHolder + '\'' +
                ", expirationDate='" + expirationDate + '\'' +
                ", cvv=" + cvv +
                ", bank='" + bank + '\'' +
                ", cardType='" + cardType + '\'' +
                ", cardNetwork='" + cardNetwork + '\'' +
                ", enabled=" + isEnabled +
                '}';
    }
}

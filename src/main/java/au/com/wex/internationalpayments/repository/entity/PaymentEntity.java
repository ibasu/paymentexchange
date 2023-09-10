package au.com.wex.internationalpayments.repository.entity;

import au.com.wex.internationalpayments.dto.CurrencyEnum;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "WEX_TRANSACTION_MASTER")
@Data
@EqualsAndHashCode(callSuper = false)
@EntityListeners(AuditingEntityListener.class)
public class PaymentEntity extends AbstractEntity<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "transaction_id")
    private String id;

    @Column(nullable = false)
    private String transactionDescription;

    @Column(updatable = false)
    private LocalDateTime transactionDate = LocalDateTime.now();

    @Column(updatable = false)
    private BigDecimal transactionAmount;

    @Column(updatable = false)
    @Enumerated(EnumType.STRING)
    private CurrencyEnum transactionOriginalCurrencyCode;

    @Column(updatable = false)
    private String createdUser;

    @Column(updatable = true, insertable = false)
    private String lastUpdatedUser;

}

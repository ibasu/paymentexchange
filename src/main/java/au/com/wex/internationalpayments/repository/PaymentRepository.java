package au.com.wex.internationalpayments.repository;

import au.com.wex.internationalpayments.repository.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentEntity, String> {

}

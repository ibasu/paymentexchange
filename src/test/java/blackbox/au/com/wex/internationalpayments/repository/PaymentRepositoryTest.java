package au.com.wex.internationalpayments.repository;

import au.com.wex.internationalpayments.dto.PaymentDTO;
import au.com.wex.internationalpayments.mapper.PaymentMapper;
import au.com.wex.internationalpayments.repository.entity.PaymentEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static common.internationalpayments.helper.PaymentDataHelper.defaultPaymentDTO;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    private PaymentMapper paymentMapper = new PaymentMapper();

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(paymentRepository).isNotNull();
    }

    @Test
    void testNewPaymentTransaction() {
        PaymentDTO paymentDTO = defaultPaymentDTO();
        PaymentEntity paymentEntity = paymentRepository.save(paymentMapper.mapToModel(paymentDTO));

        assertThat(paymentRepository.findById(paymentEntity.getId())).isNotNull();
    }
}

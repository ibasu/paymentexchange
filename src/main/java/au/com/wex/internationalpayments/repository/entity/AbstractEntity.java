package au.com.wex.internationalpayments.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public abstract class AbstractEntity<ID> {

    @Column(nullable = false)
    private String createdUser;

    private String lastUpdatedUser;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime lastUpdatedDate;

    @Version
    private Integer version;

    public abstract ID getId();

    public abstract void setId(ID id);

}

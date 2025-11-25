package uz.uzinfocom.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersRepository extends JpaRepository<OrdersEntity, Long> {

  Page<OrdersEntity> findByUserId(Long userId, Pageable pageable);

}

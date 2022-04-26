package ch.gisel.bpmn.create_address.repository;

import ch.gisel.bpmn.create_address.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
}

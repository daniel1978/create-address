package ch.gisel.bpmn.create_address.repository;

import ch.gisel.bpmn.create_address.entity.Activity;
import ch.gisel.bpmn.create_address.entity.ActivityProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityPropertyRepository extends JpaRepository<ActivityProperty, Long> {
    List<ActivityProperty> findByActivity(Activity activity);
}

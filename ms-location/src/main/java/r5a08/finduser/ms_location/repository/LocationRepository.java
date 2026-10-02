package r5a08.finduser.ms_location.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import r5a08.finduser.ms_location.model.Location;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {

    List<Location> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}
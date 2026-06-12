package com.json.AutoAlquiler.repositories;

import com.json.AutoAlquiler.models.Location;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    @Query("SELECT DISTINCT l.department FROM Location l WHERE l.country.id = :countryId ORDER BY l.department ASC")
    List<String> findDepartmentsByCountryId(@Param("countryId") Long countryId);

    @Query(
        "SELECT l.municipality FROM Location l WHERE l.country.id = :countryId AND l.department = :department ORDER BY l.municipality ASC"
    )
    List<String> findMunicipalitiesByCountryAndDepartment(@Param("countryId") Long countryId, @Param("department") String department);

    Optional<Location> findByCountryIdAndDepartmentAndMunicipality(Long countryId, String department, String municipality);
    List<Location> findAllByOrderByDepartmentAscMunicipalityAsc();
}

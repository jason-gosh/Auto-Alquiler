package com.json.AutoAlquiler.repositories;

import com.json.AutoAlquiler.models.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserDetailRepository extends JpaRepository<UserDetail, Long> {
    
    @Query("SELECT u FROM UserDetail u WHERE u.typeIdentification.id = :idType AND u.identification = :identification")
    Optional<UserDetail> findByDocAndType(
        @Param("idType") Long idType, 
        @Param("identification") String identification
    );

    Optional<UserDetail> findByUserId(Long userId);
}
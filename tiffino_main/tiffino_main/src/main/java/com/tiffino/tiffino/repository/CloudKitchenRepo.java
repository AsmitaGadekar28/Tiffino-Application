/**package com.tiffino.tiffino.repository;

import com.tiffino.tiffino.entity.CloudKitchen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CloudKitchenRepo extends JpaRepository<CloudKitchen, String> {

    long countByCityAndDivision(String city, String division);
    Optional<CloudKitchen> findByManager_ManagerId(String managerId);

    List<CloudKitchen> findByCloudKitchenIdContainingIgnoreCase(String searchTerm);
    List<CloudKitchen> findByManager_ManagerNameContainingIgnoreCase(String searchTerm);

    List<CloudKitchen> findByCloudKitchenIdInIgnoreCase(List<String> cloudKitchenIds);
    List<CloudKitchen> findByNameInIgnoreCase(List<String> names);

    @Query("SELECT ck FROM CloudKitchen ck WHERE ck.isDeleted = false AND ck.isActive = true")
    List<CloudKitchen> findAllActiveCloudKitchens();


}
**/
package com.tiffino.tiffino.repository;

import com.tiffino.tiffino.entity.CloudKitchen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CloudKitchenRepo extends JpaRepository<CloudKitchen, String> {

    // Count kitchens by city & division
    long countByCityAndDivision(String city, String division);

    // Find kitchen by manager
    Optional<CloudKitchen> findByManager_ManagerId(String managerId);

    // Get all active and non-deleted kitchens
    @Query("SELECT ck FROM CloudKitchen ck WHERE ck.isDeleted = false AND ck.isActive = true")
    List<CloudKitchen> findAllActiveCloudKitchens();

    // Search kitchens by ID or manager name (active & not deleted)
    @Query("SELECT ck FROM CloudKitchen ck " +
            "WHERE ck.isDeleted = false AND ck.isActive = true AND " +
            "(LOWER(ck.cloudKitchenId) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(ck.manager.managerName) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<CloudKitchen> searchActiveCloudKitchens(@Param("searchTerm") String searchTerm);

    // Find kitchens by a list of IDs or names (active & not deleted)
    List<CloudKitchen> findByCloudKitchenIdInIgnoreCase(List<String> cloudKitchenIds);
    List<CloudKitchen> findByNameInIgnoreCase(List<String> names);

    Optional<CloudKitchen> findByCloudKitchenIdAndIsDeletedFalse(String cloudKitchenId);

    List<CloudKitchen> findAllByIsDeletedFalse();

    @Query("SELECT ck FROM CloudKitchen ck " +
            "WHERE (:states IS NULL OR ck.state IN :states) " +
            "AND (:cities IS NULL OR ck.city IN :cities) " +
            "AND (:divisions IS NULL OR ck.division IN :divisions)")
    List<CloudKitchen> findByFilters(
            @Param("states") List<String> states,
            @Param("cities") List<String> cities,
            @Param("divisions") List<String> divisions
    );



}

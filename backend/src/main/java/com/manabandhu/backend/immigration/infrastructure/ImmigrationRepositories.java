package com.manabandhu.backend.immigration.infrastructure;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ImmigrationCategoryRepository extends JpaRepository<ImmigrationCategoryEntity, UUID> {
}

@Repository
public interface ImmigrationResourceRepository extends JpaRepository<ImmigrationResourceEntity, UUID> {

    @Query("""
           select r from ImmigrationResourceEntity r
           where r.status = 'published'
             and (:categoryId is null or r.categoryId = :categoryId)
             and (:type is null or r.type = :type)
             and (:query is null or lower(r.title || ' ' || r.body) like lower('%' || :query || '%'))
           """)
    Page<ImmigrationResourceEntity> search(@Param("categoryId") UUID categoryId, @Param("type") String type,
                                           @Param("query") String query, Pageable pageable);
}

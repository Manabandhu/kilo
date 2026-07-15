package com.manabandhu.backend.community.infrastructure;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityRepository extends JpaRepository<CommunityEntity, UUID> {

    @Query("""
           select c from CommunityEntity c
           where (:kind is null or c.kind = :kind)
             and (:category is null or c.category = :category)
             and (:city is null or c.city = :city)
             and (:state is null or c.state = :state)
             and (:query is null or lower(c.name || ' ' || c.description) like lower('%' || :query || '%'))
           """)
    Page<CommunityEntity> search(@Param("kind") String kind, @Param("category") String category,
                                 @Param("city") String city, @Param("state") String state,
                                 @Param("query") String query, Pageable pageable);
}

package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    @Query(
        value = "SELECT * FROM NEWS n LEFT JOIN user_news u on n.id = u.news_id"
            + " where not exists (SELECT * from news n2 left join user_news u2 on n2.id = u2.news_id"
            + " where u2.user_id = :userId and n.id = n2.id) order by date desc", nativeQuery = true
    )
    List<News> loadUnreadNews(@Param("userId") Long userId);
}

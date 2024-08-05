package pl.kaczmarek.Recruitment.task.url;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.kaczmarek.Recruitment.task.common.UniqueIdChecker;

@Repository
interface UrlRepository extends JpaRepository<UrlEntity, String>, UniqueIdChecker {
}
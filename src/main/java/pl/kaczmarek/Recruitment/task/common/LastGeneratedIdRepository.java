package pl.kaczmarek.Recruitment.task.common;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LastGeneratedIdRepository extends JpaRepository<LastGeneratedId, Long> {
}
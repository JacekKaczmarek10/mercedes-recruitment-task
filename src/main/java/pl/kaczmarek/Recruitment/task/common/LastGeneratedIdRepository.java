package pl.kaczmarek.Recruitment.task.common;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface LastGeneratedIdRepository extends JpaRepository<LastGeneratedId, Long> {

}
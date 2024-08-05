package pl.kaczmarek.Recruitment.task.url;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
class UrlEntity {

    @Id
    @Column(unique = true)
    private String id;

    private String longUrl;

    private LocalDateTime expirationDate;

    private Long ttl;

}
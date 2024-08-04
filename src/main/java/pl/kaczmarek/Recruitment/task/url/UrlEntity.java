package pl.kaczmarek.Recruitment.task.url;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class UrlEntity {

    @Id
    private String id;

    private String longUrl;

    private LocalDateTime expirationDate;

    private Long ttl;

}
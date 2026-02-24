package cz.kyndryl.camunda.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "job_application")
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String candidateName;

    @Column(nullable = false)
    private String candidateEmail;

    private String resumeUrl;

    @Column(length = 2000)
    private String coverLetter;

    private Long jobPositionId;

    private String processInstanceKey;

    @Builder.Default
    private LocalDateTime appliedAt = LocalDateTime.now();
}

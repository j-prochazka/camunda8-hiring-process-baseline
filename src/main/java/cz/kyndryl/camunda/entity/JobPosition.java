package cz.kyndryl.camunda.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "job_position")
public class JobPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    private String department;

    private String location;

    private String requester;

    private String requesterEmail;

    private LocalDate applicationDeadline;

    private Integer expectedSalary;

    @Builder.Default
    private Boolean managerialPosition = false;

    private String processInstanceKey;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}

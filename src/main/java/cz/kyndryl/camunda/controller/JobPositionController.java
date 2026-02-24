package cz.kyndryl.camunda.controller;

import cz.kyndryl.camunda.entity.JobPosition;
import cz.kyndryl.camunda.repository.JobPositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/positions")
@RequiredArgsConstructor
public class JobPositionController {

    private final JobPositionRepository jobPositionRepository;

    @GetMapping
    public List<JobPosition> listAll() {
        return jobPositionRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobPosition> getById(@PathVariable Long id) {
        return jobPositionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

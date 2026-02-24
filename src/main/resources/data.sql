-- Test data for UI development
-- Activate by setting spring.sql.init.mode=always in application.properties

INSERT INTO job_position (title, description, department, location, requester, requester_email, application_deadline, expected_salary, managerial_position, process_instance_key, created_at)
VALUES ('Senior Java Developer', 'We are looking for an experienced Java developer with Spring Boot and Camunda expertise.', 'Engineering', 'Prague', 'Jan Novak', 'jan.novak@company.com', '2026-03-15', 5400, false, '100001', NOW());

INSERT INTO job_position (title, description, department, location, requester, requester_email, application_deadline, expected_salary, managerial_position, process_instance_key, created_at)
VALUES ('DevOps Engineer', 'Seeking a DevOps engineer to manage CI/CD pipelines and cloud infrastructure.', 'Infrastructure', 'Brno', 'Petra Svobodova', 'petra.svobodova@company.com', '2026-03-20', 6000, false, '100002', NOW());

INSERT INTO job_position (title, description, department, location, requester, requester_email, application_deadline, expected_salary, managerial_position, process_instance_key, created_at)
VALUES ('Business Analyst', 'Business analyst role focusing on process automation and digital transformation.', 'Consulting', 'Prague', 'Martin Dvorak', 'martin.dvorak@company.com', '2026-04-01', 7900, true, '100003', NOW());

INSERT INTO job_application (candidate_name, candidate_email, resume_url, cover_letter, job_position_id, process_instance_key, applied_at)
VALUES ('Alice Johnson', 'alice.johnson@example.com', 'https://example.com/cv/alice.pdf', 'I have 8 years of Java experience and have worked extensively with Camunda 7 and 8.', 1, '200001', NOW());

INSERT INTO job_application (candidate_name, candidate_email, resume_url, cover_letter, job_position_id, process_instance_key, applied_at)
VALUES ('Bob Williams', 'bob.williams@example.com', 'https://example.com/cv/bob.pdf', 'Passionate about clean code and microservices architecture.', 1, '200002', NOW());

INSERT INTO job_application (candidate_name, candidate_email, resume_url, cover_letter, job_position_id, process_instance_key, applied_at)
VALUES ('Carol Martinez', 'carol.martinez@example.com', NULL, 'Experienced in Kubernetes, Terraform, and GitHub Actions.', 2, '200003', NOW());

INSERT INTO users (name, email, password, createdat, updatedat) VALUES
    ('bob', 'bob@mail.com', '$2a$10$gMLaAFAugWNRu7YBVJmGtewa1Mfr8kd40QBWwYUZgOjAvWhAiWN2i', NOW(), NOW()),
    ('john', 'john@mail.com', '$2a$10$GLlDEPmTesWDD.fqYdtus.rGsEHsLzlXnOuXaXLUgDgSkwUMQOcZu', NOW(), NOW()),
    ('luke', 'luke@mail.com', '$2a$10$mc7U1kMd/kefEZ0azhq0pObGLip.KaXS//zwL1Mn74aTgD2OYfT6K', NOW(), NOW());

INSERT INTO posts (title, createdat, updatedat) VALUES
    ('post 1', NOW(), NOW()),
    ('post 2', NOW(), NOW());
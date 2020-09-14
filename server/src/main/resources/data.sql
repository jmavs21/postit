INSERT INTO users (name, email, password, createdat, updatedat) VALUES
    ('bob', 'bob@mail.com', 'bob', NOW(), NOW()),
    ('john', 'john@mail.com', 'john', NOW(), NOW()),
    ('luke', 'luke@mail.com', 'luke', NOW(), NOW());

INSERT INTO posts (title, createdat, updatedat) VALUES
    ('post 1', NOW(), NOW()),
    ('post 2', NOW(), NOW());
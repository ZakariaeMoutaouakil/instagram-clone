-- Inserting data for Person table
INSERT INTO Person (username, email, password, firstname, lastname, bio)
VALUES
    ('john_doe', 'john.doe@example.com', 'password123', 'John', 'Doe', 'Hello, I am John.'),
    ('jane_smith', 'jane.smith@example.com', 'pass456', 'Jane', 'Smith', 'Nice to meet you!'),
    ('bob_jones', 'bob.jones@example.com', 'securepass', 'Bob', 'Jones', 'I love coding!');

-- Inserting data for Post table
INSERT INTO Post (description, image, uploader_id)
VALUES
    ('First post!',  'first_post.jpg', 1),
    ('Excited about Spring Boot!',  'spring_boot.jpg', 2),
    ('Beautiful sunset!',  'sunset.jpg', 3);

-- Inserting data for Comment table
INSERT INTO Comment (comment, author_id, post_id)
VALUES
    ('Great post!', 2, 1),
    ('I agree, Spring Boot is awesome!', 1, 2),
    ('Amazing photo!', 3, 3),
    ('Nice coding skills!', 1, 2);

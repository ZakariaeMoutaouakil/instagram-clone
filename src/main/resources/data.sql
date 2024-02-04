-- -- Inserting data for Person table
-- INSERT INTO Person (username, email, password, firstname, lastname, bio)
-- VALUES
--     ('john_doe', 'john.doe@example.com', 'password123', 'John', 'Doe', 'Hello, I am John.'),
--     ('jane_smith', 'jane.smith@example.com', 'pass456', 'Jane', 'Smith', 'Nice to meet you!'),
--     ('bob_jones', 'bob.jones@example.com', 'securepass', 'Bob', 'Jones', 'I love coding!');
--
-- -- Inserting data for Post table
-- INSERT INTO Post (description, image, uploader_id)
-- VALUES
--     ('First post!',  'first_post.jpg', 1),
--     ('Excited about Spring Boot!',  'spring_boot.jpg', 2),
--     ('Beautiful sunset!',  'sunset.jpg', 3);
--
-- -- Inserting data for Comment table
-- INSERT INTO Comment (comment, author_id, post_id)
-- VALUES
--     ('Great post!', 2, 1),
--     ('I agree, Spring Boot is awesome!', 1, 2),
--     ('Amazing photo!', 3, 3),
--     ('Nice coding skills!', 1, 2);


-- Inserting Persons
INSERT INTO Person (username, email, password, firstname, lastname, bio) VALUES
                                                                             ('john_doe', 'john.doe@example.com', 'password123', 'John', 'Doe', 'This is John Doe.'),
                                                                             ('jane_smith', 'jane.smith@example.com', 'password456', 'Jane', 'Smith', 'Hello, I am Jane.'),
                                                                             ('bob_jackson', 'bob.jackson@example.com', 'password789', 'Bob', 'Jackson', 'Bob here!');

-- Inserting Posts
INSERT INTO Post (description,  image, uploader_id) VALUES
                                                                 ('First post by John',  'image1.jpg', 1),
                                                                 ('Janes travel adventure',  'image2.jpg', 2),
    ('Bobs coding journey', 'image3.jpg', 3);

-- Inserting Comments
INSERT INTO Comment (author_id, comment, post_id) VALUES
                                                      (2, 'Great post, John!', 1),
                                                      (3, 'I wish I could travel like this!', 2),
                                                      (1, 'Awesome coding journey, Bob!', 3);

-- Creating Follower-Followee Relationships
INSERT INTO Person_followers (followers_id, followees_id) VALUES
                                                              (1, 2), -- John follows Jane
                                                              (1, 3), -- John follows Bob
                                                              (2, 1); -- Jane follows John

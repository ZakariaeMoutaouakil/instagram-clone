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
--     ('First post!',  'https://img.freepik.com/photos-gratuite/peinture-lac-montagne-montagne-arriere-plan_188544-9126.jpg?size=626&ext=jpg&ga=GA1.1.1788068356.1706832000&semt=sph', 1),
--     ('Excited about Spring Boot!',  'https://images.ctfassets.net/hrltx12pl8hq/28ECAQiPJZ78hxatLTa7Ts/2f695d869736ae3b0de3e56ceaca3958/free-nature-images.jpg?fit=fill&w=1200&h=630', 2),
--     ('Beautiful sunset!',  'https://img.freepik.com/photos-gratuite/peinture-numerique-montagne-arbre-colore-au-premier-plan_1340-25699.jpg', 3);
--
-- -- Inserting data for Comment table
-- INSERT INTO Comment (comment, author_id, post_id)
-- VALUES
--     ('Great post!', 2, 1),
--     ('I agree, Spring Boot is awesome!', 1, 2),
--     ('Amazing photo!', 3, 3),
--     ('Nice coding skills!', 1, 2);


-- Inserting Persons
INSERT INTO Person (username, email, password, firstname, lastname, bio, validated, photo)
VALUES ('john_doe', 'john.doe@example.com', 'password123', 'John', 'Doe', 'This is John Doe.', TRUE,
        'https://static.vecteezy.com/system/resources/thumbnails/009/292/244/small/default-avatar-icon-of-social-media-user-vector.jpg'),
       ('jane_smith', 'jane.smith@example.com', 'password456', 'Jane', 'Smith', 'Hello, I am Jane.', TRUE,
        'https://static.vecteezy.com/system/resources/thumbnails/009/292/244/small/default-avatar-icon-of-social-media-user-vector.jpg'),
       ('bob_jackson', 'bob.jackson@example.com', 'password789', 'Bob', 'Jackson', 'Bob here!', FALSE,
        'https://static.vecteezy.com/system/resources/thumbnails/009/292/244/small/default-avatar-icon-of-social-media-user-vector.jpg');

-- Inserting Posts
INSERT INTO Post (description, image, uploader_id,date)
VALUES ('First post by John',
        'https://img.freepik.com/photos-gratuite/peinture-lac-montagne-montagne-arriere-plan_188544-9126.jpg?size=626&ext=jpg&ga=GA1.1.1788068356.1706832000&semt=sph',
        1,CURRENT_TIMESTAMP),
       ('Janes travel adventure',
        'https://images.ctfassets.net/hrltx12pl8hq/28ECAQiPJZ78hxatLTa7Ts/2f695d869736ae3b0de3e56ceaca3958/free-nature-images.jpg?fit=fill&w=1200&h=630',
        2,CURRENT_TIMESTAMP),
       ('Bobs coding journey',
        'https://img.freepik.com/photos-gratuite/peinture-numerique-montagne-arbre-colore-au-premier-plan_1340-25699.jpg',
        3,CURRENT_TIMESTAMP),
       ('Bobs coding journey',
        'https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Image_created_with_a_mobile_phone.png/1200px-Image_created_with_a_mobile_phone.png',
        3,CURRENT_TIMESTAMP),
       ('Bobs coding journey',
        'https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Image_created_with_a_mobile_phone.png/1200px-Image_created_with_a_mobile_phone.png',
        3,CURRENT_TIMESTAMP),
       ('Bobs coding journey',
        'https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Image_created_with_a_mobile_phone.png/1200px-Image_created_with_a_mobile_phone.png',
        3,CURRENT_TIMESTAMP),
       ('Bobs coding journey',
        'https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Image_created_with_a_mobile_phone.png/1200px-Image_created_with_a_mobile_phone.png',
        3,CURRENT_TIMESTAMP),
       ('Bobs coding journey',
        'https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Image_created_with_a_mobile_phone.png/1200px-Image_created_with_a_mobile_phone.png',
        3,CURRENT_TIMESTAMP),
       ('Bobs coding journey',
        'https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Image_created_with_a_mobile_phone.png/1200px-Image_created_with_a_mobile_phone.png',
        3,CURRENT_TIMESTAMP),
       ('Bobs coding journey',
        'https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Image_created_with_a_mobile_phone.png/1200px-Image_created_with_a_mobile_phone.png',
        3,CURRENT_TIMESTAMP),
       ('Bobs coding journey',
        'https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Image_created_with_a_mobile_phone.png/1200px-Image_created_with_a_mobile_phone.png',
        3,CURRENT_TIMESTAMP),
       ('Bobs coding journey',
        'https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Image_created_with_a_mobile_phone.png/1200px-Image_created_with_a_mobile_phone.png',
        3,CURRENT_TIMESTAMP),
       ('Bobs coding journey',
        'https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Image_created_with_a_mobile_phone.png/1200px-Image_created_with_a_mobile_phone.png',
        3,CURRENT_TIMESTAMP),
       ('Bobs coding journey',
        'https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Image_created_with_a_mobile_phone.png/1200px-Image_created_with_a_mobile_phone.png',
        3,CURRENT_TIMESTAMP);

-- Inserting Comments
INSERT INTO Comment (author_id, comment, post_id,date)
VALUES (2, 'Great post, John!', 1,CURRENT_TIMESTAMP),
       (3, 'I wish I could travel like this!', 2,CURRENT_TIMESTAMP),
       (1, 'Awesome coding journey, Bob!', 3,CURRENT_TIMESTAMP);

-- Creating Follower-Followee Relationships
INSERT INTO Person_followers (followers_id, followees_id)
VALUES (1, 2), -- John follows Jane
       (1, 3), -- John follows Bob
       (2, 1); -- Jane follows John

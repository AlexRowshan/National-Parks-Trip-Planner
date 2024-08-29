CREATE TABLE IF NOT EXISTS user_entity (
   username VARCHAR(255) PRIMARY KEY,
   favorites VARCHAR(511),
   is_private BIT,
   password VARCHAR(255)
);
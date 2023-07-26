-- Insert Roles
INSERT INTO roles (role_id, name) VALUES (1, "ROLE_ADMIN");
INSERT INTO roles (role_id, name) VALUES (2, "ROLE_USER");

-- Insert Usuario
INSERT INTO users (user_id, name, last_name, dni, account_id, email, password, phone, enabled, attempts, verified, role_id) VALUES (null, 'Admin', 'Admin', 123456789, 1, 'admin@mail.com', '$2a$12$x1tssYTJ/CYjl1JmGeek/.OSxBkm4NOQHltYsYUISETBqqUvGWfhO', 1234567891, true, 0, true, 1);
INSERT INTO users (user_id, name, last_name, dni, account_id, email, password, phone, enabled, attempts, verified, role_id) VALUES (null, 'user1', 'user1', 987654321, 2, 'user1@mail.com', '$2a$12$x1tssYTJ/CYjl1JmGeek/.OSxBkm4NOQHltYsYUISETBqqUvGWfhO', 1226489722, true, 0, true, 2);
INSERT INTO users (user_id, name, last_name, dni, account_id, email, password, phone, enabled, attempts, verified, role_id) VALUES (null, 'user2', 'user2', 987654322, 3, 'user3@mail.com', '$2a$12$x1tssYTJ/CYjl1JmGeek/.OSxBkm4NOQHltYsYUISETBqqUvGWfhO', 1338765433, true, 0, true, 2);
INSERT INTO users (user_id, name, last_name, dni, account_id, email, password, phone, enabled, attempts, verified, role_id) VALUES (null, 'Ana Maria', 'Lopez', 13222916, 4, 'amaria@mail.com', '$2a$12$x1tssYTJ/CYjl1JmGeek/.OSxBkm4NOQHltYsYUISETBqqUvGWfhO', 1338765433, true, 0, true, 2);
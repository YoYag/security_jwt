CREATE USER 'cos'@'%' IDENTIFIED BY 'cos1234';
GRANT ALL PRIVILEGES ON *.* TO 'cos'@'%';

DROP DATABASE IF EXISTS `security`;
CREATE DATABASE `security`;
USE `security`;

DESC `user`;

SELECT * FROM `user`;

UPDATE `user`
SET `role` = 'ROLE_ADMIN'
WHERE id = 1;

UPDATE `user`
SET `role` = 'ROLE_MANAGER'
WHERE id = 2;
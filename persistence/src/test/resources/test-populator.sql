INSERT INTO institutions (instid, instname, domainname, adminemail) VALUES
(1, 'ITBA', 'itba.edu.ar', 'admin@itba.edu.ar');

INSERT INTO users (userid, useremail, firstname, lastname, encodedpassword, instid, preferredlanguage) VALUES
(1, 'testuser1@itba.edu.ar', 'John', 'Doe', '123', 1, 'SPANISH'),
(2, 'admin@itba.edu.ar', 'Admin', 'User', 'admin123', 1, 'SPANISH');

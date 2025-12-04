CREATE TABLE IF NOT EXISTS institutions (
    instid                      SERIAL              PRIMARY KEY,
    instname                    VARCHAR(100)        NOT NULL,
    domainname                  VARCHAR(100)        NOT NULL UNIQUE,
    adminemail                  VARCHAR(100)        NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS partnership_requests (
    requestid                   SERIAL              PRIMARY KEY,
    institutionname             VARCHAR(100)        NOT NULL,
    domainname                  VARCHAR(100)        NOT NULL UNIQUE,
    adminemail                  VARCHAR(100)        NOT NULL UNIQUE,
    status                      INT                 NOT NULL        DEFAULT 0,
    requestdate                 TIMESTAMP           NOT NULL        DEFAULT NOW(),
    processeddate               TIMESTAMP
);

CREATE TABLE IF NOT EXISTS users (
    userid                      SERIAL              PRIMARY KEY,
    useremail                   VARCHAR(100)        NOT NULL UNIQUE,
    firstname                   VARCHAR(100),
    lastname                    VARCHAR(100),
    encodedpassword             VARCHAR(150)        NOT NULL,
    preferredlanguage           VARCHAR(20),
    instid                      INT                                 REFERENCES institutions(instid)
);

CREATE TABLE IF NOT EXISTS orgs (
    orgid                       SERIAL              PRIMARY KEY,
    orgemail                    VARCHAR(100)        NOT NULL UNIQUE,
    orgname                     VARCHAR(100)        NOT NULL,
    sociallink1                 VARCHAR(150),
    sociallink2                 VARCHAR(150),
    description                 VARCHAR(500),
    instid                      INT                 NOT NULL        REFERENCES institutions(instid)
);

CREATE TABLE IF NOT EXISTS users_roles (
    userid                      INT                 NOT NULL        REFERENCES users(userid),
    orgid                       INT                 NOT NULL        REFERENCES orgs(orgid),
    roleid                      INT                 NOT NULL,
    PRIMARY KEY (userid, orgid)
);

CREATE TABLE IF NOT EXISTS posts (
    postid                      SERIAL              PRIMARY KEY,
    orgid                       INT                 NOT NULL        REFERENCES orgs(orgid),
    title                       VARCHAR(150)        NOT NULL,
    body                        VARCHAR(5000)       NOT NULL,
    likes                       INT                 DEFAULT 0,
    comments                    INT                 DEFAULT 0,
    datetime                    TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tags (
    tagid                       SERIAL              PRIMARY KEY,
    tagname                     VARCHAR(50)         NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS subs (
    userid                      INT                 NOT NULL        REFERENCES users(userid),
    orgid                       INT                 NOT NULL        REFERENCES orgs(orgid),
    PRIMARY KEY (userid, orgid)
);

CREATE TABLE IF NOT EXISTS likes (
    userid                      INT                 NOT NULL        REFERENCES users(userid),
    postid                      INT                 NOT NULL        REFERENCES posts(postid),
    PRIMARY KEY (userid, postid)
);

CREATE TABLE IF NOT EXISTS comments (
    commentid                   SERIAL              PRIMARY KEY,
    userid                      INT                 NOT NULL        REFERENCES users(userid),
    postid                      INT                 NOT NULL        REFERENCES posts(postid),
    content                     VARCHAR(500)        NOT NULL
);

CREATE TABLE IF NOT EXISTS posts_tags (
    postid                      INT                 NOT NULL        REFERENCES posts(postid),
    tagid                       INT                 NOT NULL        REFERENCES tags(tagid),
    PRIMARY KEY (postid, tagid)
);

CREATE TABLE IF NOT EXISTS image (
    imageid                     SERIAL              PRIMARY KEY,
    photoblob                   BYTEA               NOT NULL
);

CREATE TABLE IF NOT EXISTS events (
    eventid                     SERIAL              PRIMARY KEY,
    orgid                       INT                 NOT NULL        REFERENCES orgs(orgid),
    title                       VARCHAR(150)        NOT NULL,
    description                 VARCHAR(5000)       NOT NULL,
    location                    VARCHAR(150)        NOT NULL,
    eventdate                   TIMESTAMP           NOT NULL,
    datetime                    TIMESTAMP,
    imageid                     INT                                 REFERENCES image(imageid)
);

CREATE TABLE IF NOT EXISTS events_tags (
    eventid                     INT                 NOT NULL        REFERENCES events(eventid),
    tagid                       INT                 NOT NULL        REFERENCES tags(tagid),
    PRIMARY KEY (eventid, tagid)
);

CREATE TABLE IF NOT EXISTS events_enrollments(
    enrollmentid                SERIAL              PRIMARY KEY,
    eventid                     INT                 NOT NULL        REFERENCES events (eventid),
    userid                      INT                 NOT NULL        REFERENCES users(userid),
    status                      INT                 NOT NULL        DEFAULT 0,
    UNIQUE(eventid, userid)
);

CREATE TABLE IF NOT EXISTS open_positions (
    positionid                  SERIAL              PRIMARY KEY,
    orgid                       INT                 NOT NULL        REFERENCES orgs(orgid),
    title                       VARCHAR(150)        NOT NULL,
    description                 VARCHAR(5000)       NOT NULL,
    requirements                VARCHAR(5000)       NOT NULL,
    workhours                   VARCHAR(150),
    datetime                    TIMESTAMP,
    organigrampositionid        INT                                 REFERENCES organigrams(organigramid),
    status                      VARCHAR(20)         NOT NULL
);

CREATE TABLE IF NOT EXISTS open_position_questions (
   questionid                   SERIAL              PRIMARY KEY,
   positionid                   INT                 NOT NULL        REFERENCES open_positions(positionid),
   question                     VARCHAR(150)        NOT NULL
);

CREATE TABLE IF NOT EXISTS open_position_applications (
   applicationid                SERIAL              PRIMARY KEY,
   positionid                   INT                 NOT NULL        REFERENCES open_positions(positionid),
   userid                       INT                 NOT NULL        REFERENCES users(userid),
   status                       INT                 NOT NULL        DEFAULT 0,
   UNIQUE(positionid, userid)
);

CREATE TABLE IF NOT EXISTS open_position_answers (
   answerid                     SERIAL              PRIMARY KEY,
   applicationid                INT                 NOT NULL        REFERENCES open_position_applications(applicationid),
   questionid                   INT                 NOT NULL        REFERENCES open_position_questions(questionid),
   answer                       VARCHAR(5000)       NOT NULL
);

CREATE TABLE IF NOT EXISTS open_positions_tags (
    positionid                  INT                 NOT NULL        REFERENCES open_positions(positionid),
    tagid                       INT                 NOT NULL        REFERENCES tags(tagid),
    PRIMARY KEY (positionid, tagid)
);

CREATE TABLE IF NOT EXISTS organigrams (
    organigramid                SERIAL              PRIMARY KEY,
    orgid                       INT                 NOT NULL        REFERENCES orgs(orgid),
    positionname                VARCHAR(150)        NOT NULL,
    description                 VARCHAR(500)        NOT NULL,
    parentpositionid            INT                                 REFERENCES organigrams(organigramid),
    level                       INT                 NOT NULL        DEFAULT 0,
    sortorder                   INT                 NOT NULL        DEFAULT 0,
    assigneduserid              INT                                 REFERENCES users(userid)
);

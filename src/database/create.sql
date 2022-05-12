create table ted.ted_talks (
                               id VARCHAR(64) NOT NULL PRIMARY KEY,
                               author VARCHAR(128) NOT NULL,
                               title VARCHAR(700) NOT NULL,
                               likes BIGINT NOT NULL DEFAULT 0,
                               views BIGINT NOT NULL DEFAULT 0,
                               link VARCHAR(2048),
                               date DATE,
                               INDEX IX_AUTHOR(author),
                               INDEX IX_TITLE(title),
                               INDEX IX_LIKES(likes),
                               INDEX IX_VIEWS(views)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin
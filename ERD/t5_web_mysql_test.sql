SHOW TABLES;

SELECT TABLE_NAME FROM information_schema.TABLES
WHERE TABLE_SCHEMA = 'db2502'
  AND TABLE_NAME LIKE 't5_%'
;

SELECT * FROM t5_authority;
SELECT * FROM t5_user ORDER BY id DESC;
SELECT * FROM t5_user_authorities;
SELECT * FROM t5_post ORDER BY id DESC;
SELECT * FROM t5_comment ORDER BY id DESC;
SELECT * FROM t5_attachment ORDER BY id DESC;

-- 특정 id 의 사용자 조회
SELECT
    id "id"
     , username "username"
     , password "password"
     , email "email"
     , name "name"
     , regdate "regdate"
FROM t5_user
WHERE 1 = 1
  AND id = 1
;

-- 특정 name 의 authority 조회
SELECT
    id "id"
     , name "name"
FROM t5_authority
WHERE name = 'ROLE_ADMIN'
;

-- 특정 사용자의 authority 조회
SELECT a.id "id", a.name "name"
FROM t5_authority a, t5_user_authorities u
WHERE a.id = u.authority_id  AND  u.user_id = 3
;

-- 특정 글(post ) + 작성자(user)
SELECT
    p.id "p_id",
    p.subject "p_subject",
    p.content "p_content",
    p.viewcnt "p_viewcnt",
    p.regdate "p_regdate",
    u.id "u_id",
    u.username "u_username",
    u.password "u_password",
    u.name "u_name",
    u.email "u_email",
    u.regdate "u_regdate"
FROM
    t5_post p, t5_user u
WHERE
    p.user_id = u.id
and p.id = 1
;


-- 페이징 테스트용 다량의 데이터
INSERT INTO t5_post(user_id, subject, content)
SELECT user_id, subject, content FROM t5_post;


SELECT count(*) FROM t5_post;


# 게시물 댓글 페이지 만들기?
select * from t5_post order by id desc limit 5, 5;
select * from t5_post order by id desc limit 10, 10;


-- 특정 글의 첨부 파일들을 insert 하기
insert into t5_attachment(post_id, sourcename, filename) values
                                                             ('face01.png', 'face01.png', 5),
                                                             ('face02.png', 'face02.png', 5),
                                                             ('face03.png', 'face03.png', 5)
;




# -------------------------------------------------------
# 댓글


# 특정글 의 (댓글 + 사용자) 정보
SELECT c.id "id",
       c.content "content",
       c.regdate "regdate",
       u.id "user_id",
       u.username "user_username",
       u.password "user_password",
       u.name "user_name",
       u.email "user_email",
       u.regdate "user_regdate"
FROM t5_comment c, t5_user u
WHERE c.user_id = u.id AND c.post_id = 1
ORDER BY c.id DESC
;






ALTER TABLE t5_user
    ADD COLUMN provider VARCHAR(40);

ALTER TABLE t5_user
    ADD COLUMN providerId VARCHAR(200);


select * from t5_user order by id desc;

